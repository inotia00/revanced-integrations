package app.revanced.integrations.youtube.patches.misc.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import app.revanced.integrations.shared.requests.Requester;
import app.revanced.integrations.shared.requests.Route;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.PackageUtils;

public final class PlayerRoutes {
    public static final Route.CompiledRoute GET_STORYBOARD_SPEC_RENDERER = new Route(
            Route.Method.POST,
            "player" +
                    "?fields=storyboards.playerStoryboardSpecRenderer," +
                    "storyboards.playerLiveStoryboardSpecRenderer," +
                    "playabilityStatus.status," +
                    "playabilityStatus.errorScreen"
    ).compile();

    private static final String ANDROID_CLIENT_VERSION = PackageUtils.getVersionName();
    private static final String ANDROID_USER_AGENT = "com.google.android.youtube/" +
            ANDROID_CLIENT_VERSION +
            " (Linux; U; Android 14; GB) gzip";

    private static final String TVHTML5_SIMPLY_EMBED_CLIENT_VERSION = "2.0";
    private static final String TVHTML5_SIMPLY_EMBED_USER_AGENT = "Mozilla/5.0 (SMART-TV; LINUX; Tizen 6.5)" +
            " AppleWebKit/537.36 (KHTML, like Gecko)" +
            " 85.0.4183.93/6.5 TV Safari/537.36";
    private static final String WEB_CLIENT_VERSION = "2.20240304.00.00";
    private static final String WEB_USER_AGENT = "Mozilla/5.0 (Linux; Android 10; SM-G981B)" +
            " AppleWebKit/537.36 (KHTML, like Gecko)" +
            " Chrome/80.0.3987.162 Mobile Safari/537.36";

    private static final String ANDROID_INNER_TUBE_BODY;
    private static final String TVHTML5_SIMPLY_EMBED_INNER_TUBE_BODY;
    private static final String WEB_INNER_TUBE_BODY;

    private static final String YT_API_URL = "https://www.youtube.com/youtubei/v1/";

    /**
     * TCP connection and HTTP read timeout
     */
    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 10 * 1000; // 10 Seconds.

    static {
        JSONObject androidInnerTubeBody = new JSONObject();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "ANDROID");
            client.put("clientVersion", ANDROID_CLIENT_VERSION);
            client.put("platform", "MOBILE");
            client.put("androidSdkVersion", 34);
            client.put("osName", "Android");
            client.put("osVersion", "14");

            context.put("client", client);

            androidInnerTubeBody.put("context", context);
            androidInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create Android innerTubeBody", e);
        }

        ANDROID_INNER_TUBE_BODY = androidInnerTubeBody.toString();

        JSONObject tvEmbedInnerTubeBody = new JSONObject();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "TVHTML5_SIMPLY_EMBEDDED_PLAYER");
            client.put("clientVersion", TVHTML5_SIMPLY_EMBED_CLIENT_VERSION);
            client.put("platform", "TV");
            client.put("clientScreen", "EMBED");

            JSONObject thirdParty = new JSONObject();
            thirdParty.put("embedUrl", "https://www.youtube.com/watch?v=%s");

            context.put("thirdParty", thirdParty);
            context.put("client", client);

            tvEmbedInnerTubeBody.put("context", context);
            tvEmbedInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create TV Embed innerTubeBody", e);
        }

        TVHTML5_SIMPLY_EMBED_INNER_TUBE_BODY = tvEmbedInnerTubeBody.toString();

        JSONObject webInnerTubeBody = new JSONObject();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "WEB");
            client.put("clientVersion", WEB_CLIENT_VERSION);
            client.put("clientScreen", "WATCH");

            context.put("client", client);

            webInnerTubeBody.put("context", context);
            webInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create Web innerTubeBody", e);
        }

        WEB_INNER_TUBE_BODY = webInnerTubeBody.toString();
    }

    private PlayerRoutes() {
    }

    /**
     * @noinspection SameParameterValue
     */
    public static HttpURLConnection getPlayerResponseConnectionFromRoute(Route.CompiledRoute route, String userAgent) throws IOException {
        var connection = Requester.getConnectionFromCompiledRoute(YT_API_URL, route);

        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestProperty("X-Goog-Api-Format-Version", "2");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        connection.setConnectTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
        connection.setReadTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
        return connection;
    }

    public enum RequestClient {
        ANDROID(ANDROID_INNER_TUBE_BODY, ANDROID_USER_AGENT),
        TVHTML5_SIMPLY_EMBED(TVHTML5_SIMPLY_EMBED_INNER_TUBE_BODY, TVHTML5_SIMPLY_EMBED_USER_AGENT),
        WEB(WEB_INNER_TUBE_BODY, WEB_USER_AGENT);

        final String innerTubeBody;
        final String userAgent;

        RequestClient(String innerTubeBody, String userAgent) {
            this.innerTubeBody = innerTubeBody;
            this.userAgent = userAgent;
        }
    }
}