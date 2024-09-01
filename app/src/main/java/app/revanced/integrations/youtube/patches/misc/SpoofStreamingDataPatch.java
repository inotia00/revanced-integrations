package app.revanced.integrations.youtube.patches.misc;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.util.Map;

import app.revanced.integrations.shared.settings.Setting;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.misc.client.AppClient.ClientType;
import app.revanced.integrations.youtube.patches.misc.requests.StreamingDataRequest;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.VideoInformation;

@SuppressWarnings("unused")
public class SpoofStreamingDataPatch {
    private static final boolean SPOOF_STREAMING_DATA = Settings.SPOOF_STREAMING_DATA.get();

    /**
     * Any unreachable ip address.  Used to intentionally fail requests.
     */
    private static final String UNREACHABLE_HOST_URI_STRING = "https://127.0.0.0";

    private static volatile Map<String, String> fetchHeaders;

    /**
     * Injection point.
     * <p>
     * Blocks /initplayback requests.
     */
    public static String blockInitPlaybackRequest(String originalUrlString) {
        if (SPOOF_STREAMING_DATA) {
            try {
                var originalUri = Uri.parse(originalUrlString);
                String path = originalUri.getPath();

                if (path != null && path.contains("initplayback")) {
                    Logger.printDebug(() -> "Blocking 'initplayback' by returning unreachable url");

                    return UNREACHABLE_HOST_URI_STRING;
                }
            } catch (Exception ex) {
                Logger.printException(() -> "blockInitPlaybackRequest failure", ex);
            }
        }

        return originalUrlString;
    }

    /**
     * Injection point.
     */
    public static boolean isSpoofingEnabled() {
        return SPOOF_STREAMING_DATA;
    }

    /**
     * Injection point.
     */
    public static void setFetchHeaders(String url, Map<String, String> headers) {
        if (SPOOF_STREAMING_DATA) {
            try {
                Uri uri = Uri.parse(url);
                String path = uri.getPath();
                if (path != null && path.contains("browse")) {
                    fetchHeaders = headers;
                }
            } catch (Exception ex) {
                Logger.printException(() -> "setFetchHeaders failure", ex);
            }
        }
    }

    /**
     * Injection point.
     */
    public static void fetchStreamingData(@NonNull String videoId, boolean isShortAndOpeningOrPlaying) {
        if (SPOOF_STREAMING_DATA) {
            try {
                final boolean videoIdIsShort = VideoInformation.lastPlayerResponseIsShort();
                // Shorts shelf in home and subscription feed causes player response hook to be called,
                // and the 'is opening/playing' parameter will be false.
                // This hook will be called again when the Short is actually opened.
                if (videoIdIsShort && !isShortAndOpeningOrPlaying) {
                    return;
                }

                StreamingDataRequest.fetchRequestIfNeeded(videoId, fetchHeaders);
            } catch (Exception ex) {
                Logger.printException(() -> "fetchStreamingData failure", ex);
            }
        }
    }

    /**
     * Injection point.
     * Fix playback by replace the streaming data.
     * Called after {@link #setFetchHeaders(String, Map)} .
     */
    @Nullable
    public static ByteBuffer getStreamingData(String videoId) {
        if (SPOOF_STREAMING_DATA) {
            try {
                Utils.verifyOffMainThread();

                StreamingDataRequest request = StreamingDataRequest.getRequestForVideoId(videoId);
                if (request != null) {
                    var stream = request.getStream();
                    if (stream != null) {
                        Logger.printDebug(() -> "Overriding video stream: " + videoId);
                        return stream;
                    }
                }

                Logger.printDebug(() -> "Not overriding streaming data (video stream is null): " + videoId);
            } catch (Exception ex) {
                Logger.printException(() -> "getStreamingData failure", ex);
            }
        }

        return null;
    }

    /**
     * Injection point.
     * Called after {@link #getStreamingData(String)}.
     */
    @Nullable
    public static byte[] removeVideoPlaybackPostBody(Uri uri, int method, byte[] postData) {
        if (SPOOF_STREAMING_DATA) {
            try {
                final int methodPost = 2;
                if (method == methodPost) {
                    String path = uri.getPath();
                    String clientName = "c";
                    final boolean iosClient = ClientType.IOS.name().equals(uri.getQueryParameter(clientName));
                    if (iosClient && path != null && path.contains("videoplayback")) {
                        return null;
                    }
                }
            }  catch (Exception ex) {
                Logger.printException(() -> "removeVideoPlaybackPostBody failure", ex);
            }
        }

        return postData;
    }

    /**
     * Injection point.
     */
    public static String appendSpoofedClient(String videoFormat) {
        try {
            if (SPOOF_STREAMING_DATA && Settings.SPOOF_STREAMING_DATA_STATS_FOR_NERDS.get()
                    && !TextUtils.isEmpty(videoFormat)) {
                // Force LTR layout, to match the same LTR video time/length layout YouTube uses for all languages
                return "\u202D" + videoFormat + String.format("\u2009(%s)", StreamingDataRequest.getLastSpoofedClientName()); // u202D = left to right override
            }
        } catch (Exception ex) {
            Logger.printException(() -> "appendSpoofedClient failure", ex);
        }

        return videoFormat;
    }

    public static final class ForceiOSAVCAvailability implements Setting.Availability {
        @Override
        public boolean isAvailable() {
            return Settings.SPOOF_STREAMING_DATA.get() && Settings.SPOOF_STREAMING_DATA_TYPE.get() == ClientType.IOS;
        }
    }
}
