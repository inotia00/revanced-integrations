package app.revanced.integrations.youtube.patches.misc.requests;

import static app.revanced.integrations.youtube.patches.misc.requests.PlayerRoutes.GET_LIVE_STREAM_RENDERER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import app.revanced.integrations.shared.requests.Requester;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.misc.LiveStreamRenderer;
import app.revanced.integrations.youtube.patches.misc.requests.PlayerRoutes.ClientType;

public class LiveStreamRendererRequester {

    private LiveStreamRendererRequester() {
    }

    private static void handleConnectionError(@NonNull String toastMessage, @Nullable Exception ex) {
        Logger.printInfo(() -> toastMessage, ex);
    }

    @Nullable
    private static JSONObject fetchPlayerResponse(@NonNull String requestBody,
                                                  @NonNull String userAgent) {
        final long startTime = System.currentTimeMillis();
        try {
            Utils.verifyOffMainThread();
            Objects.requireNonNull(requestBody);

            final byte[] innerTubeBody = requestBody.getBytes(StandardCharsets.UTF_8);

            HttpURLConnection connection = PlayerRoutes.getPlayerResponseConnectionFromRoute(GET_LIVE_STREAM_RENDERER, userAgent);
            connection.getOutputStream().write(innerTubeBody, 0, innerTubeBody.length);

            final int responseCode = connection.getResponseCode();
            if (responseCode == 200) return Requester.parseJSONObject(connection);

            // Always show a toast for this, as a non 200 response means something is broken.
            handleConnectionError("Fetch livestreams not available: " + responseCode, null);
            connection.disconnect();
        } catch (SocketTimeoutException ex) {
            handleConnectionError("Fetch livestreams temporarily not available (API timed out)", ex);
        } catch (IOException ex) {
            handleConnectionError("Fetch livestreams temporarily not available: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            Logger.printException(() -> "Fetch livestreams failed", ex); // Should never happen.
        } finally {
            Logger.printDebug(() -> "Request took: " + (System.currentTimeMillis() - startTime) + "ms");
        }

        return null;
    }

    private static boolean isPlayabilityStatusOk(@NonNull JSONObject playerResponse) {
        try {
            return playerResponse.getJSONObject("playabilityStatus").getString("status").equals("OK");
        } catch (JSONException e) {
            Logger.printDebug(() -> "Failed to get playabilityStatus for response: " + playerResponse);
        }

        return false;
    }

    private static boolean isLiveStream(@NonNull JSONObject playerResponse) {
        try {
            return playerResponse.getJSONObject("videoDetails").getBoolean("isLive");
        } catch (JSONException e) {
            Logger.printDebug(() -> "Failed to get videoDetails for response: " + playerResponse);
        }

        return false;
    }

    /**
     * Fetches the liveStreamRenderer from the innerTubeBody.
     *
     * @return LiveStreamRenderer or null if playabilityStatus is not OK.
     */
    @Nullable
    private static LiveStreamRenderer getLiveStreamRendererUsingBody(@NonNull String videoId,
                                                                     @NonNull ClientType clientType) {
        final JSONObject playerResponse = fetchPlayerResponse(
                String.format(clientType.innerTubeBody, videoId),
                clientType.userAgent
        );
        if (playerResponse != null)
            return getLiveStreamRendererUsingResponse(videoId, playerResponse, clientType);

        return null;
    }

    @Nullable
    private static LiveStreamRenderer getLiveStreamRendererUsingResponse(@NonNull String videoId,
                                                                         @NonNull JSONObject playerResponse,
                                                                         @NonNull ClientType clientType) {
        try {
            Logger.printDebug(() -> "Parsing liveStreamRenderer from response: " + playerResponse);

            final String clientName = clientType.name();
            final boolean isPlayabilityOk = isPlayabilityStatusOk(playerResponse);
            final boolean isLiveStream = isLiveStream(playerResponse);

            LiveStreamRenderer renderer = new LiveStreamRenderer(
                    videoId,
                    clientName,
                    isPlayabilityOk,
                    isLiveStream
            );
            Logger.printDebug(() -> "Fetched: " + renderer);

            return renderer;
        } catch (Exception e) {
            Logger.printException(() -> "Failed to get liveStreamRenderer", e);
        }

        return null;
    }

    @Nullable
    public static LiveStreamRenderer getLiveStreamRenderer(@NonNull String videoId, @NonNull ClientType clientType) {
        Objects.requireNonNull(videoId);

        LiveStreamRenderer renderer = getLiveStreamRendererUsingBody(videoId, clientType);
        if (renderer == null) {
            String finalClientName1 = clientType.name();
            Logger.printDebug(() -> videoId + " not available using " + finalClientName1 + " client");

            clientType = ClientType.TVHTML5_SIMPLY_EMBEDDED_PLAYER;
            renderer = getLiveStreamRendererUsingBody(videoId, clientType);
            if (renderer == null) {
                String finalClientName2 = clientType.name();
                Logger.printDebug(() -> videoId + " not available using " + finalClientName2 + " client");
            }
        }

        return renderer;
    }
}