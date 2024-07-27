package app.revanced.integrations.youtube.patches.misc;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class WatchHistoryPatch {

    public enum WatchHistoryType {
        ORIGINAL,
        REPLACE,
        BLOCK
    }

    private static final String UNREACHABLE_HOST_URI_STRING = "https://127.0.0.0";
    private static final String YOUTUBE_DOMAIN = "www.youtube.com";
    private static final String YOUTUBE_TRACKING_DOMAIN = "s.youtube.com";

    public static String replaceTrackingUrl(String originalUrl) {
        final WatchHistoryType watchHistoryType = Settings.WATCH_HISTORY_TYPE.get();
        if (watchHistoryType != WatchHistoryType.ORIGINAL) {
            try {
                if (originalUrl.contains(YOUTUBE_TRACKING_DOMAIN)) {
                    if (watchHistoryType == WatchHistoryType.REPLACE) {
                        final String replacement = originalUrl.replaceAll(YOUTUBE_TRACKING_DOMAIN, YOUTUBE_DOMAIN);
                        if (!replacement.equals(originalUrl)) {
                            Logger.printDebug(() -> "Replaced: '" + originalUrl + "'\nwith: '" + replacement + "'");
                        }
                        return replacement;
                    } else if (watchHistoryType == WatchHistoryType.BLOCK) {
                        Logger.printDebug(() -> "Blocking: " + originalUrl + " by returning: " + UNREACHABLE_HOST_URI_STRING);
                        return UNREACHABLE_HOST_URI_STRING;
                    }
                }
            } catch (Exception ex) {
                Logger.printException(() -> "replaceTrackingUrl failure", ex);
            }
        }

        return originalUrl;
    }

}
