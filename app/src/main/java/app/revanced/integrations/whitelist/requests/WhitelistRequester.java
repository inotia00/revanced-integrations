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

    private static final String videoId = VideoInformation.getVideoId();

    private WhitelistRequester() {
    }

    public static void addChannelToWhitelist(WhitelistType whitelistType) {
        try {
            var context = Objects.requireNonNull(ReVancedUtils.getContext());

            HttpURLConnection connection = getConnectionFromRoute(videoId);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setUseCaches(false);
            connection.setConnectTimeout(2000); // timeout for TCP connection to server
            connection.setReadTimeout(4000); // timeout for server response
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                JSONObject json = getJSONObject(connection);
                JSONArray items = json.getJSONArray("items");
                JSONObject item = items.getJSONObject(1);
                JSONObject snippet = item.getJSONObject("snippet");
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
            } else {
                //runOnMainThread(() -> showToastShort(str("revanced_whitelisting_fetch_failed", responseCode)));
                runOnMainThread(() -> showToastShort(connection.getResponseMessage()));
            }
            connection.disconnect();
        } catch (Exception ex) {
            LogHelper.printException(WhitelistRequester.class, "Failed to fetch channelId", ex);
            runOnMainThread(() -> showToastShort(str("revanced_whitelisting_fetch_failed")));
        }
    }

    // helpers

    private static HttpURLConnection getConnectionFromRoute(String... params) throws IOException {
        return Requester.getConnectionFromRoute(YT_API_URL, WhitelistRoutes.GET_CHANNEL_DETAILS, params);
    }

    private static JSONObject getJSONObject(HttpURLConnection connection) throws Exception {
        return Requester.parseJSONObjectAndDisconnect(connection);
    }
}
