package app.revanced.integrations.patches.misc.requests;

import static app.revanced.integrations.patches.misc.requests.StoryBoardRendererRoutes.getPlayerResponseConnectionFromRoute;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import app.revanced.integrations.patches.misc.SpoofPlayerParameterPatch;
import app.revanced.integrations.patches.video.VideoChannel;
import app.revanced.integrations.requests.Requester;
import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.utils.ReVancedUtils;

public class StoryBoardRendererRequester {

    private static VideoChannel channelModel;
    /**
     * TCP timeout
     */
    private static final int TIMEOUT_TCP_DEFAULT_MILLISECONDS = 2000;

    /**
     * HTTP response timeout
     */
    private static final int TIMEOUT_HTTP_DEFAULT_MILLISECONDS = 4000;

    /**
     * Response code of a successful API call
     */
    private static final int HTTP_STATUS_CODE_SUCCESS = 200;

    /**
     * Json string
     */
    private static final String INNER_TUBE_BODY = "{\"context\": {\"client\": { \"clientName\": \"ANDROID\", \"clientVersion\": \"18.31.40\", \"platform\": \"MOBILE\", \"osName\": \"Android\", \"osVersion\": \"12\", \"androidSdkVersion\": 31 } }, \"videoId\": \"%s\"}";

    private StoryBoardRendererRequester() {
    }

    public static void fetchStoryboardsRenderer(@NonNull String videoId) {
        ReVancedUtils.verifyOffMainThread();

        try {
            final byte[] innerTubeBody = String.format(INNER_TUBE_BODY, videoId).getBytes(StandardCharsets.UTF_8);

            HttpURLConnection connection = getConnectionFromRoute();
            connection.getOutputStream().write(innerTubeBody, 0, innerTubeBody.length);

            final int responseCode = connection.getResponseCode();

            if (responseCode == HTTP_STATUS_CODE_SUCCESS) {
                final JSONObject playerResponse = Requester.parseJSONObject(connection);
                final JSONObject storyboards = playerResponse.getJSONObject("storyboards");
                final String storyboardsRendererTag = storyboards.has("playerLiveStoryboardSpecRenderer")
                        ? "playerLiveStoryboardSpecRenderer"
                        : "playerStoryboardSpecRenderer";
                final JSONObject storyboardsRenderer = storyboards.getJSONObject(storyboardsRendererTag);
                final String storyboardsRendererSpec = storyboardsRenderer.getString("spec");

                SpoofPlayerParameterPatch.setStoryboardRendererSpec(storyboardsRendererSpec);
                // Get the author information too
                JSONObject videoInfo = playerResponse.getJSONObject("videoDetails");
                channelModel = new VideoChannel(videoInfo.getString("author"), videoInfo.getString("channelId"));
            } else {
                handleConnectionError("API not available: " + responseCode, null);
            }
            connection.disconnect();
        } catch (SocketTimeoutException ex) {
            handleConnectionError("API timed out", ex);
        } catch (IOException ex) {
            handleConnectionError(String.format("Failed to fetch StoryBoard URL (%s)", ex.getMessage()), ex);
        } catch (Exception ex) {
            handleConnectionError("Failed to fetch StoryBoard URL", ex);
        }
    }

    public static VideoChannel getchannelModel() {
        return channelModel;
    }

    private static void handleConnectionError(@NonNull String toastMessage, @Nullable Exception ex) {
        if (ex != null) {
            LogHelper.printException(StoryBoardRendererRequester.class, toastMessage, ex);
        } else {
            LogHelper.printException(StoryBoardRendererRequester.class, toastMessage);
        }
        SpoofPlayerParameterPatch.setStoryboardRendererSpec("");
    }

    // helpers

    private static HttpURLConnection getConnectionFromRoute() throws IOException {
        HttpURLConnection connection = getPlayerResponseConnectionFromRoute();
        connection.setRequestProperty("User-Agent", "com.google.android.youtube/18.31.40 (Linux; U; Android 12; GB) gzip");
        connection.setRequestProperty("X-Goog-Api-Format-Version", "2");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept-Language", "en-GB, en;q=0.9");
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setConnectTimeout(TIMEOUT_TCP_DEFAULT_MILLISECONDS);
        connection.setReadTimeout(TIMEOUT_HTTP_DEFAULT_MILLISECONDS);
        return connection;
    }
}
