package app.revanced.integrations.youtube.patches.misc.requests;

import static app.revanced.integrations.shared.utils.Utils.isSDKAbove;
import static app.revanced.integrations.youtube.patches.misc.requests.PlayerRoutes.GET_STREAMING_DATA;

import android.annotation.SuppressLint;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.misc.client.AppClient.ClientType;
import app.revanced.integrations.youtube.settings.Settings;

public class StreamingDataRequest {
    private static final ClientType[] allClientTypes = {
            ClientType.IOS,
            ClientType.ANDROID_VR,
            ClientType.ANDROID_UNPLUGGED,
            ClientType.ANDROID_TESTSUITE,
            ClientType.ANDROID_EMBEDDED_PLAYER,
            ClientType.WEB,
            ClientType.TVHTML5_SIMPLY_EMBEDDED_PLAYER
    };

    private static final ClientType[] clientTypesToUse;

    static {
        final ClientType clientType = Settings.SPOOF_STREAMING_DATA_TYPE.get();
        ClientType[] clientTypeArray = new ClientType[allClientTypes.length + 1];
        clientTypeArray[0] = clientType;
        int i = 1;
        for (ClientType c : allClientTypes) {
            clientTypeArray[i] = c;
            i++;
        }
        ArrayList<ClientType> tmpArrayList = new ArrayList<>();
        for (ClientType c : clientTypeArray){
            if (!tmpArrayList.contains(c))
                tmpArrayList.add(c);
        }
        clientTypesToUse = Arrays.copyOfRange(tmpArrayList.toArray(new ClientType[0]), 0, 3);
    }

    private static String lastSpoofedClientName = "Unknown";

    public static String getLastSpoofedClientName() {
        return lastSpoofedClientName;
    }

    /**
     * How long to keep fetches until they are expired.
     */
    private static final long CACHE_RETENTION_TIME_MILLISECONDS = 60 * 1000; // 1 Minute

    private static final long MAX_MILLISECONDS_TO_WAIT_FOR_FETCH = 20 * 1000; // 20 seconds

    @GuardedBy("itself")
    private static final Map<String, StreamingDataRequest> cache = new HashMap<>();

    @SuppressLint("ObsoleteSdkInt")
    public static void fetchRequestIfNeeded(@Nullable String videoId, Map<String, String> fetchHeaders) {
        Objects.requireNonNull(videoId);
        synchronized (cache) {
            final long now = System.currentTimeMillis();

            // Remove any expired entries.
            if (isSDKAbove(24)) {
                cache.values().removeIf(request -> {
                    final boolean expired = request.isExpired(now);
                    if (expired) Logger.printDebug(() -> "Removing expired stream: " + request.videoId);
                    return expired;
                });
            } else {
                final Iterator<Map.Entry<String, StreamingDataRequest>> itr = cache.entrySet().iterator();
                while (itr.hasNext()) {
                    final Map.Entry<String, StreamingDataRequest> entry = itr.next();
                    final StreamingDataRequest request = entry.getValue();
                    if (entry.getValue().isExpired(now)) {
                        Logger.printDebug(() -> "Removing expired stream: " + request.videoId);
                        itr.remove();
                    }
                }
            }

            if (!cache.containsKey(videoId)) {
                cache.put(videoId, new StreamingDataRequest(videoId, fetchHeaders));
            }
        }
    }

    @Nullable
    public static StreamingDataRequest getRequestForVideoId(@Nullable String videoId) {
        synchronized (cache) {
            return cache.get(videoId);
        }
    }

    private static void handleConnectionError(String toastMessage, @Nullable Exception ex) {
        Logger.printInfo(() -> toastMessage, ex);
    }

    @Nullable
    private static HttpURLConnection send(ClientType clientType, String videoId,
                                          Map<String, String> playerHeaders) {
        Objects.requireNonNull(clientType);
        Objects.requireNonNull(videoId);
        Objects.requireNonNull(playerHeaders);

        final long startTime = System.currentTimeMillis();
        String clientTypeName = clientType.name();
        Logger.printDebug(() -> "Fetching video streams for: " + videoId + " using client: " + clientType.name());

        try {
            HttpURLConnection connection = PlayerRoutes.getPlayerResponseConnectionFromRoute(GET_STREAMING_DATA, clientType);

            String authHeader = playerHeaders.get("Authorization");
            String visitorId = playerHeaders.get("X-Goog-Visitor-Id");
            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("X-Goog-Visitor-Id", visitorId);

            String innerTubeBody = PlayerRoutes.createInnertubeBody(clientType, videoId);
            byte[] requestBody = innerTubeBody.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(requestBody.length);
            connection.getOutputStream().write(requestBody);

            final int responseCode = connection.getResponseCode();
            if (responseCode == 200) return connection;

            handleConnectionError(clientTypeName + " not available with response code: "
                            + responseCode + " message: " + connection.getResponseMessage(),
                    null);
        } catch (SocketTimeoutException ex) {
            handleConnectionError("Connection timeout", ex);
        } catch (IOException ex) {
            handleConnectionError("Network error", ex);
        } catch (Exception ex) {
            Logger.printException(() -> "send failed", ex);
        } finally {
            Logger.printDebug(() -> "video: " + videoId + " took: " + (System.currentTimeMillis() - startTime) + "ms");
        }

        return null;
    }

    private static ByteBuffer fetch(@NonNull String videoId, Map<String, String> playerHeaders) {
        // Retry with different client if empty response body is received.
        for (ClientType clientType : clientTypesToUse) {
            HttpURLConnection connection = send(clientType, videoId, playerHeaders);
            if (connection != null) {
                try {
                    // gzip encoding doesn't response with content length (-1),
                    // but empty response body does.
                    if (connection.getContentLength() != 0) {
                        try (InputStream inputStream = new BufferedInputStream(connection.getInputStream())) {
                            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                                    baos.write(buffer, 0, bytesRead);
                                }

                                lastSpoofedClientName = clientType.friendlyName;

                                return ByteBuffer.wrap(baos.toByteArray());
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.printException(() -> "Fetch failed while processing response data", ex);
                }
            }
        }

        handleConnectionError("Could not fetch any client streams", null);
        return null;
    }

    /**
     * Time this instance and the fetch future was created.
     */
    private final long timeFetched;
    private final String videoId;
    private final Future<ByteBuffer> future;

    private StreamingDataRequest(String videoId, Map<String, String> playerHeaders) {
        Objects.requireNonNull(playerHeaders);
        this.timeFetched = System.currentTimeMillis();
        this.videoId = videoId;
        this.future = Utils.submitOnBackgroundThread(() -> fetch(videoId, playerHeaders));
    }

    public boolean isExpired(long now) {
        final long timeSinceCreation = now - timeFetched;
        if (timeSinceCreation > CACHE_RETENTION_TIME_MILLISECONDS) {
            return true;
        }

        // Only expired if the fetch failed (API null response).
        return (fetchCompleted() && getStream() == null);
    }

    /**
     * @return if the fetch call has completed.
     */
    public boolean fetchCompleted() {
        return future.isDone();
    }

    @Nullable
    public ByteBuffer getStream() {
        try {
            return future.get(MAX_MILLISECONDS_TO_WAIT_FOR_FETCH, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            Logger.printInfo(() -> "getStream timed out", ex);
        } catch (InterruptedException ex) {
            Logger.printException(() -> "getStream interrupted", ex);
            Thread.currentThread().interrupt(); // Restore interrupt status flag.
        } catch (ExecutionException ex) {
            Logger.printException(() -> "getStream failure", ex);
        }

        return null;
    }
}
