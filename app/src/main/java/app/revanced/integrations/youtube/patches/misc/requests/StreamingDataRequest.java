package app.revanced.integrations.youtube.patches.misc.requests;

import static app.revanced.integrations.youtube.patches.misc.requests.PlayerRoutes.GET_STREAMING_DATA;

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
import java.util.Collections;
import java.util.LinkedHashMap;
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
            ClientType.ANDROID_VR,
            ClientType.ANDROID_UNPLUGGED,
            ClientType.IOS,
    };

    private static final ClientType[] clientTypesToUse;

    static {
        ClientType preferredClient = Settings.SPOOF_STREAMING_DATA_TYPE.get();
        clientTypesToUse = new ClientType[allClientTypes.length];

        clientTypesToUse[0] = preferredClient;

        int i = 1;
        for (ClientType c : allClientTypes) {
            if (c != preferredClient) {
                clientTypesToUse[i++] = c;
            }
        }
    }

    private static ClientType lastSpoofedClientType;

    public static String getLastSpoofedClientName() {
        return lastSpoofedClientType == null
                ? "Unknown"
                : lastSpoofedClientType.friendlyName;
    }

    /**
     * How long to keep fetches until they are expired.
     */
    private static final long CACHE_RETENTION_TIME_MILLISECONDS = 5 * 60 * 1000; // 5 Minutes

    /**
     * TCP connection and HTTP read timeout.
     */
    private static final int HTTP_TIMEOUT_MILLISECONDS = 10 * 1000;

    /**
     * Any arbitrarily large value, but must be at least twice {@link #HTTP_TIMEOUT_MILLISECONDS}
     */
    private static final int MAX_MILLISECONDS_TO_WAIT_FOR_FETCH = 20 * 1000;

    @GuardedBy("itself")
    private static final Map<String, StreamingDataRequest> cache = Collections.synchronizedMap(
            new LinkedHashMap<>(30) {
                /**
                 * Cache limit must be greater than the maximum number of videos open at once,
                 * which theoretically is more than 4 (3 Shorts + one regular minimized video).
                 * But instead use a much larger value, to handle if a video viewed a while ago
                 * is somehow still referenced.  Each stream is a small array of Strings
                 * so memory usage is not a concern.
                 */
                private static final int CACHE_LIMIT = 15;

                @Override
                protected boolean removeEldestEntry(Entry eldest) {
                    return size() > CACHE_LIMIT; // Evict the oldest entry if over the cache limit.
                }
            });

    public static void fetchRequest(@NonNull String videoId, Map<String, String> fetchHeaders) {
        synchronized (cache) {
            // Remove any expired entries.
            final long now = System.currentTimeMillis();
            cache.values().removeIf(request -> {
                final boolean expired = request.isExpired(now);
                if (expired) Logger.printDebug(() -> "Removing expired stream: " + request.videoId);
                return expired;
            });
            cache.put(videoId, new StreamingDataRequest(videoId, fetchHeaders));
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
            connection.setConnectTimeout(HTTP_TIMEOUT_MILLISECONDS);
            connection.setReadTimeout(HTTP_TIMEOUT_MILLISECONDS);

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
        lastSpoofedClientType = null;

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
                                byte[] buffer = new byte[2048];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                                    baos.write(buffer, 0, bytesRead);
                                }
                                lastSpoofedClientType = clientType;

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

    @NonNull
    @Override
    public String toString() {
        return "StreamingDataRequest{" + "videoId='" + videoId + '\'' + '}';
    }
}
