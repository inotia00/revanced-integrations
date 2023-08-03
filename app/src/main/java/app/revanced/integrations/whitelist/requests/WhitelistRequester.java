package app.revanced.integrations.whitelist.requests;

import static app.revanced.integrations.utils.ReVancedUtils.runOnMainThread;
import static app.revanced.integrations.utils.ReVancedUtils.showToastShort;
import static app.revanced.integrations.utils.StringRef.str;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import app.revanced.integrations.patches.video.VideoChannel;
import app.revanced.integrations.patches.video.VideoInformation;
import app.revanced.integrations.requests.Requester;
import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.utils.ReVancedHelper;
import app.revanced.integrations.utils.ReVancedUtils;
import app.revanced.integrations.whitelist.Whitelist;
import app.revanced.integrations.whitelist.WhitelistType;

public class WhitelistRequester {
    private static final String YT_API_URL = "https://www.googleapis.com/youtube/v3/";

    private static final int TIMEOUT_TCP_DEFAULT_MILLISECONDS = 2000;

    private static final int TIMEOUT_HTTP_DEFAULT_MILLISECONDS = 4000;

    private static final String videoId = VideoInformation.getVideoId();

    private WhitelistRequester() {
    }

    public static void addChannelToWhitelist(WhitelistType whitelistType) {
        try {
            var context = Objects.requireNonNull(ReVancedUtils.getContext());

            HttpURLConnection connection = getChannelConnectionFromRoute(videoId);
            connection.setConnectTimeout(TIMEOUT_TCP_DEFAULT_MILLISECONDS); // timeout for TCP connection to server
            connection.setReadTimeout(TIMEOUT_HTTP_DEFAULT_MILLISECONDS); // timeout for server response

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                JSONObject json = getJSONObject(connection);
                JSONArray items = json.getJSONArray("items");
                JSONObject snippet = items.getJSONObject(0).getJSONObject("snippet");
                VideoChannel channelModel = new VideoChannel(snippet.getString("channelTitle"), snippet.getString("channelId"));
                String author = channelModel.getAuthor();

                boolean success = Whitelist.addToWhitelist(whitelistType, context, channelModel);
                String whitelistTypeName = whitelistType.getFriendlyName();
                runOnMainThread(() -> {
                    if (success) {
                        showToastShort(str("revanced_whitelisting_added", author, whitelistTypeName));
                    } else {
                        showToastShort(str("revanced_whitelisting_add_failed", author, whitelistTypeName));
                    }
                });
            } else if (responseCode == 403) {
                runOnMainThread(() -> showToastShort(str("revanced_whitelisting_missing_api_key")));
            } else {
                runOnMainThread(() -> showToastShort(str("revanced_whitelisting_fetch_failed", responseCode)));
            }
            connection.disconnect();
        } catch (Exception ex) {
            LogHelper.printException(WhitelistRequester.class, "Failed to fetch channelId", ex);
            runOnMainThread(() -> showToastShort(str("revanced_whitelisting_fetch_failed")));
        }
    }

    // helpers

    private static HttpURLConnection getChannelConnectionFromRoute(String... params) throws IOException {
        return Requester.getConnectionFromRoute(YT_API_URL, WhitelistRoutes.GET_CHANNEL_DETAILS, params);
    }

    private static JSONObject getJSONObject(HttpURLConnection connection) throws Exception {
        return Requester.parseJSONObjectAndDisconnect(connection);
    }
}
