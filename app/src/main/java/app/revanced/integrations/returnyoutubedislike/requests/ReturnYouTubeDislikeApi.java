package app.revanced.integrations.returnyoutubedislike.requests;

import static app.revanced.integrations.returnyoutubedislike.requests.ReturnYouTubeDislikeRoutes.getRYDConnectionFromRoute;
import static app.revanced.integrations.utils.StringRef.str;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

import app.revanced.integrations.requests.Requester;
import app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.utils.ReVancedUtils;

public class ReturnYouTubeDislikeApi {
    /**
     * {@link #fetchVotes(String)} TCP connection timeout
     */
    private static final int API_GET_VOTES_TCP_TIMEOUT_MILLISECONDS = 2000;

    /**
     * {@link #fetchVotes(String)} HTTP read timeout.
     * To locally debug and force timeouts, change this to a very small number (ie: 100)
     */
    private static final int API_GET_VOTES_HTTP_TIMEOUT_MILLISECONDS = 5000;

    /**
     * Default connection and response timeout for voting and registration.
     * <p>
     * Voting and user registration runs in the background and has has no urgency
     * so this can be a larger value.
     */
    private static final int API_REGISTER_VOTE_TIMEOUT_MILLISECONDS = 90000;

    /**
     * Response code of a successful API call
     */
    private static final int HTTP_STATUS_CODE_SUCCESS = 200;

    /**
     * Response code indicating the video id is not for a video that can be voted for.
     * (it's not a Short or a regular video, and it's likely a YouTube Story)
     */
    private static final int HTTP_STATUS_CODE_NOT_FOUND = 404;

    /**
     * Indicates a client rate limit has been reached
     */
    private static final int RATE_LIMIT_HTTP_STATUS_CODE = 429;

    /**
     * How long to wait until API calls are resumed, if a rate limit is hit.
     * No clear guideline of how long to backoff. Using 2 minutes for now.
     */
    private static final int RATE_LIMIT_BACKOFF_SECONDS = 120;

    /**
     * Last time a {@link #RATE_LIMIT_HTTP_STATUS_CODE} was reached.
     * zero if has not been reached.
     */
    private static volatile long lastTimeRateLimitWasHit; // must be volatile, since different threads read/write to this

    private ReturnYouTubeDislikeApi() {
    } // utility class

    /**
     * @return True, if api rate limit is in effect.
     */
    private static boolean checkIfRateLimitInEffect(String apiEndPointName) {
        if (lastTimeRateLimitWasHit == 0) {
            return false;
        }
        final long numberOfSecondsSinceLastRateLimit = (System.currentTimeMillis() - lastTimeRateLimitWasHit) / 1000;
        if (numberOfSecondsSinceLastRateLimit < RATE_LIMIT_BACKOFF_SECONDS) {
            LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Ignoring api call " + apiEndPointName + " as only "
                    + numberOfSecondsSinceLastRateLimit + " seconds has passed since last rate limit.");
            return true;
        }
        return false;
    }

    /**
     * @return True, if a client rate limit was requested
     */
    private static boolean checkIfRateLimitWasHit(int httpResponseCode) {
        if (httpResponseCode == RATE_LIMIT_HTTP_STATUS_CODE) {
            lastTimeRateLimitWasHit = System.currentTimeMillis();
            ReVancedUtils.showToastLong(str("revanced_ryd_failure_client_rate_limit_requested"));
            return true;
        }
        return false;
    }

    private static void updateStatistics(boolean connectionError, boolean rateLimitHit) {
        if (connectionError && rateLimitHit) {
            throw new IllegalArgumentException();
        }
    }

    private static void handleConnectionError(@NonNull String toastMessage, @Nullable Exception ex) {
        if (ex != null) {
            LogHelper.printException(ReturnYouTubeDislikeApi.class, toastMessage, ex);
        }
    }

    /**
     * @return NULL if fetch failed, or if a rate limit is in effect.
     */
    @Nullable
    public static RYDVoteData fetchVotes(String videoId) {
        ReVancedUtils.verifyOffMainThread();
        Objects.requireNonNull(videoId);

        if (checkIfRateLimitInEffect("fetchVotes")) {
            return null;
        }
        LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Fetching votes for: " + videoId);

        try {
            HttpURLConnection connection = getRYDConnectionFromRoute(ReturnYouTubeDislikeRoutes.GET_DISLIKES, videoId);
            // request headers, as per https://returnyoutubedislike.com/docs/fetching
            // the documentation says to use 'Accept:text/html', but the RYD browser plugin uses 'Accept:application/json'
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Connection", "keep-alive"); // keep-alive is on by default with http 1.1, but specify anyways
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setUseCaches(false);
            connection.setConnectTimeout(API_GET_VOTES_TCP_TIMEOUT_MILLISECONDS); // timeout for TCP connection to server
            connection.setReadTimeout(API_GET_VOTES_HTTP_TIMEOUT_MILLISECONDS); // timeout for server response

            final int responseCode = connection.getResponseCode();
            if (checkIfRateLimitWasHit(responseCode)) {
                connection.disconnect(); // rate limit hit, should disconnect
                updateStatistics(false, true);
                return null;
            }

            if (responseCode == HTTP_STATUS_CODE_SUCCESS) {
                // do not disconnect, the same server connection will likely be used again soon
                JSONObject json = Requester.parseJSONObject(connection);
                try {
                    RYDVoteData votingData = new RYDVoteData(json);
                    updateStatistics(false, false);
                    LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Voting data fetched: " + votingData);
                    return votingData;
                } catch (JSONException ex) {
                    LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to parse video: " + videoId + " json: " + json, ex);
                    // fall thru to update statistics
                }
            } else if (responseCode == HTTP_STATUS_CODE_NOT_FOUND) {
                // normal response when viewing YouTube Stories (cannot vote for these)
                LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Video has no like/dislikes (video is a YouTube Story?): " + videoId);
                return null; // do not updated connection statistics
            } else {
                handleConnectionError(str("revanced_ryd_failure_connection_status_code", responseCode), null);
            }
            connection.disconnect(); // something went wrong, might as well disconnect
        } catch (
                SocketTimeoutException ex) { // connection timed out, response timeout, or some other network error
            handleConnectionError((str("revanced_ryd_failure_connection_timeout")), ex);
        } catch (IOException ex) {
            handleConnectionError((str("revanced_ryd_failure_generic", ex.getMessage())), ex);
        } catch (Exception ex) {
            // should never happen
            LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to fetch votes", ex);
        }

        updateStatistics(true, false);
        return null;
    }

    /**
     * @return The newly created and registered user id.  Returns NULL if registration failed.
     */
    @Nullable
    public static String registerAsNewUser() {
        ReVancedUtils.verifyOffMainThread();
        try {
            if (checkIfRateLimitInEffect("registerAsNewUser")) {
                return null;
            }
            String userId = randomString();
            LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Trying to register new user: " + userId);

            HttpURLConnection connection = getRYDConnectionFromRoute(ReturnYouTubeDislikeRoutes.GET_REGISTRATION, userId);
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(API_REGISTER_VOTE_TIMEOUT_MILLISECONDS);
            connection.setReadTimeout(API_REGISTER_VOTE_TIMEOUT_MILLISECONDS);

            final int responseCode = connection.getResponseCode();
            if (checkIfRateLimitWasHit(responseCode)) {
                connection.disconnect(); // disconnect, as no more connections will be made for a little while
                return null;
            }
            if (responseCode == HTTP_STATUS_CODE_SUCCESS) {
                JSONObject json = Requester.parseJSONObject(connection);
                String challenge = json.getString("challenge");
                int difficulty = json.getInt("difficulty");

                String solution = solvePuzzle(challenge, difficulty);
                return confirmRegistration(userId, solution);
            }
            handleConnectionError(str("revanced_ryd_failure_connection_status_code", responseCode), null);
            connection.disconnect();
        } catch (SocketTimeoutException ex) {
            handleConnectionError(str("revanced_ryd_failure_connection_timeout"), ex);
        } catch (IOException ex) {
            handleConnectionError(str("revanced_ryd_failure_generic", "registration failed"), ex);
        } catch (Exception ex) {
            LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to register user", ex); // should never happen
        }
        return null;
    }

    @Nullable
    private static String confirmRegistration(String userId, String solution) {
        ReVancedUtils.verifyOffMainThread();
        Objects.requireNonNull(userId);
        Objects.requireNonNull(solution);
        try {
            if (checkIfRateLimitInEffect("confirmRegistration")) {
                return null;
            }
            LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Trying to confirm registration with solution: " + solution);

            HttpURLConnection connection = getRYDConnectionFromRoute(ReturnYouTubeDislikeRoutes.CONFIRM_REGISTRATION, userId);
            applyCommonPostRequestSettings(connection);

            String jsonInputString = "{\"solution\": \"" + solution + "\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            final int responseCode = connection.getResponseCode();
            if (checkIfRateLimitWasHit(responseCode)) {
                connection.disconnect(); // disconnect, as no more connections will be made for a little while
                return null;
            }
            String result = null;
            if (responseCode == HTTP_STATUS_CODE_SUCCESS) {
                result = Requester.parseJson(connection);
                if (result.equalsIgnoreCase("true")) {
                    LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Registration confirmation successful");
                    return userId;
                }
            }
            final String resultLog = result == null ? "(no response)" : result;
            LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to confirm registration for user: " + userId
                    + " solution: " + solution + " responseCode: " + responseCode + " responseString: " + resultLog);
            handleConnectionError(str("revanced_ryd_failure_connection_status_code", responseCode), null);
            connection.disconnect(); // something went wrong, might as well disconnect
        } catch (SocketTimeoutException ex) {
            handleConnectionError(str("revanced_ryd_failure_connection_timeout"), ex);
        } catch (IOException ex) {
            handleConnectionError(str("revanced_ryd_failure_generic", "confirm registration failed"), ex);
        } catch (Exception ex) {
            LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to confirm registration for user: " + userId
                    + "solution: " + solution, ex);
        }
        return null;
    }

    /**
     * Must call off main thread, as this will make a network call if user is not yet registered.
     *
     * @return ReturnYouTubeDislike user ID. If user registration has never happened
     * and the network call fails, this returns NULL.
     */
    @Nullable
    private static String getUserId() {
        ReVancedUtils.verifyOffMainThread();

        String userId = SettingsEnum.RYD_USER_ID.getString();
        if (!userId.isEmpty()) {
            return userId;
        }

        userId = registerAsNewUser();
        if (userId != null) {
            SettingsEnum.RYD_USER_ID.saveValue(userId);
        }
        return userId;
    }

    public static boolean sendVote(String videoId, ReturnYouTubeDislike.Vote vote) {
        ReVancedUtils.verifyOffMainThread();
        Objects.requireNonNull(videoId);
        Objects.requireNonNull(vote);

        try {
            String userId = getUserId();
            if (userId == null) return false;

            if (checkIfRateLimitInEffect("sendVote")) {
                return false;
            }
            LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Trying to vote for video: " + videoId + " with vote: " + vote);

            HttpURLConnection connection = getRYDConnectionFromRoute(ReturnYouTubeDislikeRoutes.SEND_VOTE);
            applyCommonPostRequestSettings(connection);

            String voteJsonString = "{\"userId\": \"" + userId + "\", \"videoId\": \"" + videoId + "\", \"value\": \"" + vote.value + "\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = voteJsonString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            final int responseCode = connection.getResponseCode();
            if (checkIfRateLimitWasHit(responseCode)) {
                connection.disconnect(); // disconnect, as no more connections will be made for a little while
                return false;
            }
            if (responseCode == HTTP_STATUS_CODE_SUCCESS) {
                JSONObject json = Requester.parseJSONObject(connection);
                String challenge = json.getString("challenge");
                int difficulty = json.getInt("difficulty");

                String solution = solvePuzzle(challenge, difficulty);
                return confirmVote(videoId, userId, solution);
            }
            LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to send vote for video: " + videoId + " vote: " + vote
                    + " response code was: " + responseCode);
            handleConnectionError(str("revanced_ryd_failure_connection_status_code", responseCode), null);
            connection.disconnect(); // something went wrong, might as well disconnect
        } catch (SocketTimeoutException ex) {
            handleConnectionError(str("revanced_ryd_failure_connection_timeout"), ex);
        } catch (IOException ex) {
            handleConnectionError(str("revanced_ryd_failure_generic", "send vote failed"), ex);
        } catch (Exception ex) {
            // should never happen
            LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to send vote for video: " + videoId + " vote: " + vote, ex);
        }
        return false;
    }

    private static boolean confirmVote(String videoId, String userId, String solution) {
        ReVancedUtils.verifyOffMainThread();
        Objects.requireNonNull(videoId);
        Objects.requireNonNull(userId);
        Objects.requireNonNull(solution);

        try {
            if (checkIfRateLimitInEffect("confirmVote")) {
                return false;
            }
            LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Trying to confirm vote for video: " + videoId + " solution: " + solution);
            HttpURLConnection connection = getRYDConnectionFromRoute(ReturnYouTubeDislikeRoutes.CONFIRM_VOTE);
            applyCommonPostRequestSettings(connection);

            String jsonInputString = "{\"userId\": \"" + userId + "\", \"videoId\": \"" + videoId + "\", \"solution\": \"" + solution + "\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            final int responseCode = connection.getResponseCode();
            if (checkIfRateLimitWasHit(responseCode)) {
                connection.disconnect(); // disconnect, as no more connections will be made for a little while
                return false;
            }
            String result = null;
            if (responseCode == HTTP_STATUS_CODE_SUCCESS) {
                result = Requester.parseJson(connection);
                if (result.equalsIgnoreCase("true")) {
                    LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Vote confirm successful for video: " + videoId);
                    return true;
                }
            }
            final String resultLog = result == null ? "(no response)" : result;
            LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to confirm vote for video: " + videoId
                    + " solution: " + solution + " responseCode: " + responseCode + " responseString: " + resultLog);
            handleConnectionError(str("revanced_ryd_failure_connection_status_code", responseCode), null);
            connection.disconnect(); // something went wrong, might as well disconnect
        } catch (SocketTimeoutException ex) {
            handleConnectionError(str("revanced_ryd_failure_connection_timeout"), ex);
        } catch (IOException ex) {
            handleConnectionError(str("revanced_ryd_failure_generic", "confirm vote failed"), ex);
        } catch (Exception ex) {
            LogHelper.printException(ReturnYouTubeDislikeApi.class, "Failed to confirm vote for video: " + videoId
                    + " solution: " + solution, ex); // should never happen
        }
        return false;
    }

    private static void applyCommonPostRequestSettings(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setConnectTimeout(API_REGISTER_VOTE_TIMEOUT_MILLISECONDS); // timeout for TCP connection to server
        connection.setReadTimeout(API_REGISTER_VOTE_TIMEOUT_MILLISECONDS); // timeout for server response
    }


    private static String solvePuzzle(String challenge, int difficulty) {
        final long timeSolveStarted = System.currentTimeMillis();
        byte[] decodedChallenge = Base64.decode(challenge, Base64.NO_WRAP);

        byte[] buffer = new byte[20];
        System.arraycopy(decodedChallenge, 0, buffer, 4, 16);

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex); // should never happen
        }

        final int maxCount = (int) (Math.pow(2, difficulty + 1) * 5);
        for (int i = 0; i < maxCount; i++) {
            buffer[0] = (byte) i;
            buffer[1] = (byte) (i >> 8);
            buffer[2] = (byte) (i >> 16);
            buffer[3] = (byte) (i >> 24);
            byte[] messageDigest = md.digest(buffer);

            if (countLeadingZeroes(messageDigest) >= difficulty) {
                String solution = Base64.encodeToString(new byte[]{buffer[0], buffer[1], buffer[2], buffer[3]}, Base64.NO_WRAP);
                LogHelper.printDebug(ReturnYouTubeDislikeApi.class, "Found puzzle solution: " + solution + " of difficulty: " + difficulty
                        + " in: " + (System.currentTimeMillis() - timeSolveStarted) + " ms");
                return solution;
            }
        }

        // should never be reached
        throw new IllegalStateException("Failed to solve puzzle challenge: " + challenge + " difficulty: " + difficulty);
    }

    // https://stackoverflow.com/a/157202
    private static String randomString() {
        String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(36);
        for (int i = 0; i < 36; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private static int countLeadingZeroes(byte[] uInt8View) {
        int zeroes = 0;
        for (byte b : uInt8View) {
            int value = b & 0xFF;
            if (value == 0) {
                zeroes += 8;
            } else {
                int count = 1;
                if (value >>> 4 == 0) {
                    count += 4;
                    value <<= 4;
                }
                if (value >>> 6 == 0) {
                    count += 2;
                    value <<= 2;
                }
                zeroes += count - (value >>> 7);
                break;
            }
        }
        return zeroes;
    }
}
