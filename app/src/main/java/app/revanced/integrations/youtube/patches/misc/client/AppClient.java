package app.revanced.integrations.youtube.patches.misc.client;

import static app.revanced.integrations.shared.utils.StringRef.str;

import android.os.Build;

import androidx.annotation.Nullable;

import app.revanced.integrations.shared.utils.PackageUtils;

public class AppClient {

    // WEB
    private static final String CLIENT_VERSION_WEB = "2.20240726.00.00";
    private static final String DEVICE_MODEL_WEB = "Surface Book 3";
    private static final String OS_VERSION_WEB = "10";
    private static final String USER_AGENT_WEB = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:129.0)" +
            " Gecko/20100101" +
            " Firefox/129.0";

    // ANDROID
    private static final String CLIENT_VERSION_ANDROID = PackageUtils.getVersionName();
    private static final String DEVICE_MODEL_ANDROID = Build.MODEL;
    private static final String OS_NAME_ANDROID = "Android";
    private static final String OS_VERSION_ANDROID = Build.VERSION.RELEASE;
    private static final int ANDROID_SDK_VERSION_ANDROID = Build.VERSION.SDK_INT;
    private static final String USER_AGENT_ANDROID = "com.google.android.youtube/" +
            CLIENT_VERSION_ANDROID +
            " (Linux; U; Android " +
            OS_VERSION_ANDROID +
            "; GB) gzip";

    // IOS
    /**
     * The hardcoded client version of the iOS app used for InnerTube requests with this client.
     *
     * <p>
     * It can be extracted by getting the latest release version of the app on
     * <a href="https://apps.apple.com/us/app/youtube-watch-listen-stream/id544007664/">the App
     * Store page of the YouTube app</a>, in the {@code What’s New} section.
     * </p>
     */
    private static final String CLIENT_VERSION_IOS = "19.16.3";
    private static final String DEVICE_MAKE_IOS = "Apple";
    /**
     * The device machine id for the iPhone XS Max (iPhone11,4), used to get 60fps.
     * The device machine id for the iPhone 15 Pro Max (iPhone16,2), used to get HDR with AV1 hardware decoding.
     *
     * <p>
     * See <a href="https://gist.github.com/adamawolf/3048717">this GitHub Gist</a> for more
     * information.
     * </p>
     */
    private static final String DEVICE_MODEL_IOS = DeviceHardwareSupport.allowAV1()
            ? "iPhone16,2"
            : "iPhone11,4";
    private static final String OS_NAME_IOS = "iOS";
    /**
     * The minimum supported OS version for the iOS YouTube client is iOS 14.0.
     * Using an invalid OS version will use the AVC codec.
     */
    private static final String OS_VERSION_IOS = DeviceHardwareSupport.allowVP9()
            ? "17.6.1.21G101"
            : "13.7.17H35";
    private static final String USER_AGENT_VERSION_IOS = DeviceHardwareSupport.allowVP9()
            ? "17_6_1"
            : "13_7";
    private static final String USER_AGENT_IOS = "com.google.ios.youtube/" +
            CLIENT_VERSION_IOS +
            "(" +
            DEVICE_MODEL_IOS +
            "; U; CPU iOS " +
            USER_AGENT_VERSION_IOS +
            " like Mac OS X)";

    // ANDROID VR
    /**
     * The hardcoded client version of the Android VR app used for InnerTube requests with this client.
     *
     * <p>
     * It can be extracted by getting the latest release version of the app on
     * <a href="https://www.meta.com/en-us/experiences/2002317119880945/">the App
     * Store page of the YouTube app</a>, in the {@code Additional details} section.
     * </p>
     */
    private static final String CLIENT_VERSION_ANDROID_VR = "1.56.21";
    /**
     * The device machine id for the Meta Quest 3, used to get opus codec with the Android VR client.
     *
     * <p>
     * See <a href="https://dumps.tadiphone.dev/dumps/oculus/eureka">this GitLab</a> for more
     * information.
     * </p>
     */
    private static final String DEVICE_MODEL_ANDROID_VR = "Quest 3";
    private static final String OS_VERSION_ANDROID_VR = "12";
    /**
     * The SDK version for Android 12 is 31,
     * but for some reason the build.props for the {@code Quest 3} state that the SDK version is 32.
     */
    private static final int ANDROID_SDK_VERSION_ANDROID_VR = 32;
    /**
     * Package name for YouTube VR (Google DayDream): com.google.android.apps.youtube.vr (Deprecated)
     * Package name for YouTube VR (Meta Quests): com.google.android.apps.youtube.vr.oculus
     * Package name for YouTube VR (ByteDance Pico 4): com.google.android.apps.youtube.vr.pico
     */
    private static final String USER_AGENT_ANDROID_VR = "com.google.android.youtube/" +
            CLIENT_VERSION_ANDROID_VR +
            " (Linux; U; Android " +
            OS_VERSION_ANDROID_VR +
            "; GB) gzip";

    // ANDROID UNPLUGGED
    private static final String CLIENT_VERSION_ANDROID_UNPLUGGED = "8.16.0";
    /**
     * The device machine id for the Chromecast with Google TV 4K.
     *
     * <p>
     * See <a href="https://dumps.tadiphone.dev/dumps/google/sabrina">this GitLab</a> for more
     * information.
     * </p>
     */
    private static final String DEVICE_MODEL_ANDROID_UNPLUGGED = "Chromecast";
    private static final String OS_VERSION_ANDROID_UNPLUGGED = "12";
    private static final int ANDROID_SDK_VERSION_ANDROID_UNPLUGGED = 31;
    private static final String USER_AGENT_ANDROID_UNPLUGGED = "com.google.android.apps.youtube.unplugged/" +
            CLIENT_VERSION_ANDROID_UNPLUGGED +
            " (Linux; U; Android " +
            OS_VERSION_ANDROID_UNPLUGGED +
            "; GB) gzip";

    // ANDROID TESTSUITE
    private static final String CLIENT_VERSION_ANDROID_TESTSUITE = "1.9";
    private static final String USER_AGENT_ANDROID_TESTSUITE = "com.google.android.youtube/" +
            CLIENT_VERSION_ANDROID_TESTSUITE +
            " (Linux; U; Android " +
            OS_VERSION_ANDROID +
            "; GB) gzip";

    // TVHTML5 SIMPLY EMBEDDED PLAYER
    private static final String CLIENT_VERSION_TVHTML5_SIMPLY_EMBEDDED_PLAYER = "2.0";

    private AppClient() {
    }

    public enum ClientType {
        WEB(1,
                null,
                DEVICE_MODEL_WEB,
                CLIENT_VERSION_WEB,
                null,
                OS_VERSION_WEB,
                null,
                USER_AGENT_WEB
        ),
        ANDROID(3,
                null,
                DEVICE_MODEL_ANDROID,
                CLIENT_VERSION_ANDROID,
                OS_NAME_ANDROID,
                OS_VERSION_ANDROID,
                ANDROID_SDK_VERSION_ANDROID,
                USER_AGENT_ANDROID
        ),
        IOS(5,
                DEVICE_MAKE_IOS,
                DEVICE_MODEL_IOS,
                CLIENT_VERSION_IOS,
                OS_NAME_IOS,
                OS_VERSION_IOS,
                null,
                USER_AGENT_IOS
        ),
        ANDROID_VR(28,
                null,
                DEVICE_MODEL_ANDROID_VR,
                CLIENT_VERSION_ANDROID_VR,
                OS_NAME_ANDROID,
                OS_VERSION_ANDROID_VR,
                ANDROID_SDK_VERSION_ANDROID_VR,
                USER_AGENT_ANDROID_VR
        ),
        ANDROID_UNPLUGGED(29,
                null,
                DEVICE_MODEL_ANDROID_UNPLUGGED,
                CLIENT_VERSION_ANDROID_UNPLUGGED,
                OS_NAME_ANDROID,
                OS_VERSION_ANDROID_UNPLUGGED,
                ANDROID_SDK_VERSION_ANDROID_UNPLUGGED,
                USER_AGENT_ANDROID_UNPLUGGED
        ),
        ANDROID_TESTSUITE(30,
                null,
                DEVICE_MODEL_ANDROID,
                CLIENT_VERSION_ANDROID_TESTSUITE,
                OS_NAME_ANDROID,
                OS_VERSION_ANDROID,
                ANDROID_SDK_VERSION_ANDROID,
                USER_AGENT_ANDROID_TESTSUITE
        ),
        ANDROID_EMBEDDED_PLAYER(55,
                null,
                DEVICE_MODEL_ANDROID,
                CLIENT_VERSION_ANDROID,
                OS_NAME_ANDROID,
                OS_VERSION_ANDROID,
                ANDROID_SDK_VERSION_ANDROID,
                USER_AGENT_ANDROID
        ),
        TVHTML5_SIMPLY_EMBEDDED_PLAYER(85,
                null,
                DEVICE_MODEL_WEB,
                CLIENT_VERSION_TVHTML5_SIMPLY_EMBEDDED_PLAYER,
                null,
                OS_VERSION_WEB,
                null,
                USER_AGENT_WEB
        );

        public final String friendlyName;

        /**
         * YouTube
         * <a href="https://github.com/zerodytrash/YouTube-Internal-Clients?tab=readme-ov-file#clients">client type</a>
         */
        public final int id;

        /**
         * Device manufacturer.
         */
        @Nullable
        public final String make;

        /**
         * Device model, equivalent to {@link Build#MODEL} (System property: ro.product.model)
         */
        public final String model;

        /**
         * Device OS name.
         */
        @Nullable
        public final String osName;

        /**
         * Device OS version.
         */
        public final String osVersion;

        /**
         * Player user-agent.
         */
        public final String userAgent;

        /**
         * Android SDK version, equivalent to {@link Build.VERSION#SDK} (System property: ro.build.version.sdk)
         * Field is null if not applicable.
         */
        public final Integer androidSdkVersion;

        /**
         * App version.
         */
        public final String appVersion;

        ClientType(int id, @Nullable String make, String model, String appVersion, @Nullable String osName,
                   String osVersion, Integer androidSdkVersion, String userAgent) {
            this.friendlyName = str("revanced_spoof_streaming_data_type_entry_" + name().toLowerCase());
            this.id = id;
            this.make = make;
            this.model = model;
            this.appVersion = appVersion;
            this.osName = osName;
            this.osVersion = osVersion;
            this.androidSdkVersion = androidSdkVersion;
            this.userAgent = userAgent;
        }
    }
}
