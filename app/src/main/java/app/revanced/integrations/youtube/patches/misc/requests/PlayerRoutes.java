package app.revanced.integrations.youtube.patches.misc.requests;

import static app.revanced.integrations.shared.utils.StringRef.str;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

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

    public static final Route.CompiledRoute GET_LIVE_STREAM_RENDERER = new Route(
            Route.Method.POST,
            "player" +
                    "?fields=playabilityStatus.status," +
                    "videoDetails.isLive"
    ).compile();


    private static final String ANDROID_CLIENT_VERSION = PackageUtils.getVersionName();
    private static final String ANDROID_DEVICE_MODEL = Build.MODEL;
    private static final String ANDROID_OS_RELEASE_VERSION = Build.VERSION.RELEASE;
    private static final int ANDROID_OS_SDK_VERSION = Build.VERSION.SDK_INT;
    private static final String ANDROID_USER_AGENT = "com.google.android.youtube/" +
            ANDROID_CLIENT_VERSION +
            " (Linux; U; Android " +
            ANDROID_OS_RELEASE_VERSION +
            "; GB) gzip";

    private static final String ANDROID_TESTSUITE_CLIENT_VERSION = "1.9";


    private static final String ANDROID_UNPLUGGED_CLIENT_VERSION = "8.30.1";
    /**
     * The device machine id for the Chromecast with Google TV 4K.
     *
     * <p>
     * See <a href="https://dumps.tadiphone.dev/dumps/google/sabrina">this GitLab</a> for more
     * information.
     * </p>
     */
    private static final String ANDROID_UNPLUGGED_DEVICE_MODEL = "Chromecast";
    private static final String ANDROID_UNPLUGGED_OS_RELEASE_VERSION = "12";
    private static final int ANDROID_UNPLUGGED_OS_SDK_VERSION = 31;
    private static final String ANDROID_UNPLUGGED_USER_AGENT = "com.google.android.apps.youtube.unplugged/" +
            ANDROID_UNPLUGGED_CLIENT_VERSION +
            " (Linux; U; Android " +
            ANDROID_UNPLUGGED_OS_RELEASE_VERSION +
            "; GB) gzip";


    /**
     * The hardcoded client version of the Android VR app used for InnerTube requests with this client.
     *
     * <p>
     * It can be extracted by getting the latest release version of the app on
     * <a href="https://www.meta.com/en-us/experiences/2002317119880945/">the App
     * Store page of the YouTube app</a>, in the {@code Additional details} section.
     * </p>
     */
    private static final String ANDROID_VR_CLIENT_VERSION = "1.58.14";

    /**
     * The device machine id for the Meta Quest 3, used to get opus codec with the Android VR client.
     *
     * <p>
     * See <a href="https://dumps.tadiphone.dev/dumps/oculus/eureka">this GitLab</a> for more
     * information.
     * </p>
     */
    private static final String ANDROID_VR_DEVICE_MODEL = "Quest 3";

    private static final String ANDROID_VR_OS_RELEASE_VERSION = "12";
    /**
     * The SDK version for Android 12 is 31,
     * but for some reason the build.props for the {@code Quest 3} state that the SDK version is 32.
     */
    private static final int ANDROID_VR_OS_SDK_VERSION = 32;

    /**
     * Package name for YouTube VR (Google DayDream): com.google.android.apps.youtube.vr (Deprecated)
     * Package name for YouTube VR (Meta Quests): com.google.android.apps.youtube.vr.oculus
     * Package name for YouTube VR (ByteDance Pico 4): com.google.android.apps.youtube.vr.pico
     */
    private static final String ANDROID_VR_USER_AGENT = "com.google.android.apps.youtube.vr.oculus/" +
            ANDROID_VR_CLIENT_VERSION +
            " (Linux; U; Android 12; GB) gzip";


    /**
     * The hardcoded client version of the iOS app used for InnerTube requests with this client.
     *
     * <p>
     * It can be extracted by getting the latest release version of the app on
     * <a href="https://apps.apple.com/us/app/youtube-watch-listen-stream/id544007664/">the App
     * Store page of the YouTube app</a>, in the {@code What’s New} section.
     * </p>
     */
    private static final String IOS_CLIENT_VERSION = "19.29.1";
    /**
     * The device machine id for the iPhone 14 Pro Max (iPhone15,3), used to get 60fps.
     * The device machine id for the iPhone 15 Pro Max (iPhone16,2), used to get HDR with AV1 hardware decoding.
     *
     * <p>
     * See <a href="https://gist.github.com/adamawolf/3048717">this GitHub Gist</a> for more
     * information.
     * </p>
     */
    private static final String IOS_DEVICE_MODEL = deviceHasAV1HardwareDecoding() ? "iPhone16,2" : "iPhone15,3";
    private static final String IOS_OS_VERSION = "17.5.1.21F90";
    private static final String IOS_USER_AGENT_VERSION = "17_5_1";
    private static final String IOS_USER_AGENT = "com.google.ios.youtube/" +
            IOS_CLIENT_VERSION +
            "(" +
            IOS_DEVICE_MODEL +
            "; U; CPU iOS " +
            IOS_USER_AGENT_VERSION +
            " like Mac OS X)";

    private static final String TVHTML5_SIMPLY_EMBEDDED_PLAYER_CLIENT_VERSION = "2.0";
    private static final String TVHTML5_SIMPLY_EMBEDDED_PLAYER_USER_AGENT = "Mozilla/5.0 (SMART-TV; LINUX; Tizen 6.5)" +
            " AppleWebKit/537.36 (KHTML, like Gecko)" +
            " 85.0.4183.93/6.5 TV Safari/537.36";
    private static final String WEB_CLIENT_VERSION = "2.20240718.01.00";
    private static final String WEB_USER_AGENT = "Mozilla/5.0 (Linux; Android 10; SM-G981B)" +
            " AppleWebKit/537.36 (KHTML, like Gecko)" +
            " Chrome/80.0.3987.162 Mobile Safari/537.36";

    private static final String ANDROID_INNER_TUBE_BODY;
    private static final String ANDROID_EMBED_INNER_TUBE_BODY;
    private static final String ANDROID_TESTSUITE_INNER_TUBE_BODY;
    private static final String ANDROID_UNPLUGGED_INNER_TUBE_BODY;
    private static final String ANDROID_VR_INNER_TUBE_BODY;
    private static final String IOS_INNER_TUBE_BODY;
    private static final String TVHTML5_SIMPLY_EMBED_INNER_TUBE_BODY;
    private static final String WEB_INNER_TUBE_BODY;

    private static final String YT_API_URL = "https://www.youtube.com/youtubei/v1/";

    /**
     * TCP connection and HTTP read timeout
     */
    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 10 * 1000; // 10 Seconds.

    static {
        JSONObject androidInnerTubeBody = new JSONObject();
        JSONObject androidEmbedInnerTubeBody = new JSONObject();
        JSONObject androidTestsuiteInnerTubeBody = new JSONObject();
        JSONObject androidUnpluggedInnerTubeBody = new JSONObject();
        JSONObject androidVRInnerTubeBody = new JSONObject();
        JSONObject iOSInnerTubeBody = new JSONObject();
        JSONObject tvEmbedInnerTubeBody = new JSONObject();
        JSONObject webInnerTubeBody = new JSONObject();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "ANDROID");
            client.put("clientVersion", ANDROID_CLIENT_VERSION);
            client.put("platform", "MOBILE");
            client.put("androidSdkVersion", ANDROID_OS_SDK_VERSION);
            client.put("osName", "Android");
            client.put("osVersion", ANDROID_OS_RELEASE_VERSION);

            context.put("client", client);

            androidInnerTubeBody.put("context", context);
            androidInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create Android innerTubeBody", e);
        }

        ANDROID_INNER_TUBE_BODY = androidInnerTubeBody.toString();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "ANDROID_EMBEDDED_PLAYER");
            client.put("clientVersion", ANDROID_CLIENT_VERSION);
            client.put("clientScreen", "EMBED");
            client.put("platform", "MOBILE");
            client.put("androidSdkVersion", ANDROID_OS_SDK_VERSION);
            client.put("osName", "Android");
            client.put("osVersion", ANDROID_OS_RELEASE_VERSION);

            JSONObject thirdParty = new JSONObject();
            thirdParty.put("embedUrl", "https://www.youtube.com/embed/%s");

            context.put("thirdParty", thirdParty);
            context.put("client", client);

            androidEmbedInnerTubeBody.put("context", context);
            androidEmbedInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create Android Embed innerTubeBody", e);
        }

        ANDROID_EMBED_INNER_TUBE_BODY = androidEmbedInnerTubeBody.toString();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "ANDROID_TESTSUITE");
            client.put("clientVersion", ANDROID_TESTSUITE_CLIENT_VERSION);
            client.put("platform", "MOBILE");
            client.put("androidSdkVersion", ANDROID_OS_SDK_VERSION);
            client.put("osName", "Android");
            client.put("osVersion", ANDROID_OS_RELEASE_VERSION);

            context.put("client", client);

            androidTestsuiteInnerTubeBody.put("context", context);
            androidTestsuiteInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create Android Testsuite innerTubeBody", e);
        }

        ANDROID_TESTSUITE_INNER_TUBE_BODY = androidTestsuiteInnerTubeBody.toString();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "ANDROID_UNPLUGGED");
            client.put("clientVersion", ANDROID_UNPLUGGED_CLIENT_VERSION);
            client.put("platform", "MOBILE");
            client.put("androidSdkVersion", ANDROID_UNPLUGGED_OS_SDK_VERSION);
            client.put("osName", "Android");
            client.put("osVersion", ANDROID_UNPLUGGED_OS_RELEASE_VERSION);

            context.put("client", client);

            androidUnpluggedInnerTubeBody.put("context", context);
            androidUnpluggedInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create Android Unplugged innerTubeBody", e);
        }

        ANDROID_UNPLUGGED_INNER_TUBE_BODY = androidUnpluggedInnerTubeBody.toString();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "ANDROID_VR");
            client.put("clientVersion", ANDROID_VR_CLIENT_VERSION);
            client.put("platform", "MOBILE");
            client.put("androidSdkVersion", ANDROID_VR_OS_SDK_VERSION);
            client.put("osName", "Android");
            client.put("osVersion", ANDROID_VR_OS_RELEASE_VERSION);

            context.put("client", client);

            androidVRInnerTubeBody.put("context", context);
            androidVRInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create Android VR innerTubeBody", e);
        }

        ANDROID_VR_INNER_TUBE_BODY = androidVRInnerTubeBody.toString();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "IOS");
            client.put("clientVersion", IOS_CLIENT_VERSION);
            client.put("deviceMake", "Apple");
            client.put("deviceModel", IOS_DEVICE_MODEL);
            client.put("platform", "MOBILE");
            client.put("osName", "iOS");
            client.put("osVersion", IOS_OS_VERSION);

            context.put("client", client);

            iOSInnerTubeBody.put("context", context);
            iOSInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create iOS innerTubeBody", e);
        }

        IOS_INNER_TUBE_BODY = iOSInnerTubeBody.toString();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "TVHTML5_SIMPLY_EMBEDDED_PLAYER");
            client.put("clientVersion", TVHTML5_SIMPLY_EMBEDDED_PLAYER_CLIENT_VERSION);
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

    private static boolean deviceHasAV1HardwareDecoding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);

            for (MediaCodecInfo codecInfo : codecList.getCodecInfos()) {
                if (codecInfo.isHardwareAccelerated() && !codecInfo.isEncoder()) {
                    String[] supportedTypes = codecInfo.getSupportedTypes();
                    for (String type : supportedTypes) {
                        if (type.equalsIgnoreCase("video/av01")) {
                            MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(type);
                            if (capabilities != null) {
                                Logger.printDebug(() -> "Device supports AV1 hardware decoding.");
                                return true;
                            }
                        }
                    }
                }
            }
        }

        Logger.printDebug(() -> "Device does not support AV1 hardware decoding.");
        return false;
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

    public enum ClientType {
        ANDROID(3, ANDROID_DEVICE_MODEL, ANDROID_CLIENT_VERSION, ANDROID_INNER_TUBE_BODY, ANDROID_OS_RELEASE_VERSION, ANDROID_USER_AGENT),
        ANDROID_EMBEDDED_PLAYER(55, ANDROID_DEVICE_MODEL, ANDROID_CLIENT_VERSION, ANDROID_EMBED_INNER_TUBE_BODY, ANDROID_OS_RELEASE_VERSION, ANDROID_USER_AGENT),
        ANDROID_TESTSUITE(30, ANDROID_DEVICE_MODEL, ANDROID_TESTSUITE_CLIENT_VERSION, ANDROID_TESTSUITE_INNER_TUBE_BODY, ANDROID_OS_RELEASE_VERSION, ANDROID_USER_AGENT),
        ANDROID_UNPLUGGED(29, ANDROID_UNPLUGGED_DEVICE_MODEL, ANDROID_UNPLUGGED_CLIENT_VERSION, ANDROID_UNPLUGGED_INNER_TUBE_BODY, ANDROID_UNPLUGGED_OS_RELEASE_VERSION, ANDROID_UNPLUGGED_USER_AGENT),
        ANDROID_VR(28, ANDROID_VR_DEVICE_MODEL, ANDROID_VR_CLIENT_VERSION, ANDROID_VR_INNER_TUBE_BODY, ANDROID_VR_OS_RELEASE_VERSION, ANDROID_VR_USER_AGENT),
        IOS(5, IOS_DEVICE_MODEL, IOS_CLIENT_VERSION, IOS_INNER_TUBE_BODY, IOS_OS_VERSION, IOS_USER_AGENT),
        // No suitable model name was found for TVHTML5_SIMPLY_EMBEDDED_PLAYER. Use the model name of ANDROID.
        TVHTML5_SIMPLY_EMBEDDED_PLAYER(85, ANDROID_DEVICE_MODEL, TVHTML5_SIMPLY_EMBEDDED_PLAYER_CLIENT_VERSION, TVHTML5_SIMPLY_EMBED_INNER_TUBE_BODY, TVHTML5_SIMPLY_EMBEDDED_PLAYER_CLIENT_VERSION, TVHTML5_SIMPLY_EMBEDDED_PLAYER_USER_AGENT),
        // No suitable model name was found for WEB. Use the model name of ANDROID.
        WEB(1, ANDROID_DEVICE_MODEL, WEB_CLIENT_VERSION, WEB_INNER_TUBE_BODY, WEB_CLIENT_VERSION, WEB_USER_AGENT);

        public final String friendlyName;
        public final int id;
        public final String model;
        public final String version;
        public final String innerTubeBody;
        public final String osVersion;
        public final String userAgent;

        ClientType(int id, String model, String version, String innerTubeBody,
                   String osVersion, String userAgent) {
            this.friendlyName = str("revanced_spoof_client_options_entry_" + name().toLowerCase());
            this.id = id;
            this.model = model;
            this.version = version;
            this.innerTubeBody = innerTubeBody;
            this.osVersion = osVersion;
            this.userAgent = userAgent;
        }
    }
}