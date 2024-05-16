package app.revanced.integrations.youtube.utils;

import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.youtube.patches.video.PlaybackSpeedPatch.userSelectedPlaybackSpeed;
import static app.revanced.integrations.youtube.settings.preference.ExternalDownloaderPreference.checkPackageIsEnabled;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import app.revanced.integrations.shared.settings.BooleanSetting;
import app.revanced.integrations.shared.settings.StringSetting;
import app.revanced.integrations.shared.utils.IntentUtils;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.patches.video.CustomPlaybackSpeedPatch;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.VideoInformation;

@SuppressWarnings("unused")
public class VideoUtils extends IntentUtils {
    private static final BooleanSetting externalDownloaderActionButton =
            Settings.EXTERNAL_DOWNLOADER_ACTION_BUTTON;
    private static final StringSetting externalDownloaderPackageName =
            Settings.EXTERNAL_DOWNLOADER_PACKAGE_NAME;
    private static final AtomicBoolean isExternalDownloaderLaunched = new AtomicBoolean(false);

    public static void copyUrl(boolean withTimestamp) {
        StringBuilder builder = new StringBuilder("https://youtu.be/");
        builder.append(VideoInformation.getVideoId());
        final long currentVideoTimeInSeconds = VideoInformation.getVideoTime() / 1000;
        if (withTimestamp && currentVideoTimeInSeconds > 0) {
            builder.append("?t=");
            builder.append(currentVideoTimeInSeconds);
        }

        setClipboard(builder.toString(), withTimestamp
                ? str("revanced_share_copy_url_timestamp_success")
                : str("revanced_share_copy_url_success")
        );
    }

    public static void copyTimeStamp() {
        final String timeStamp = getTimeStamp(VideoInformation.getVideoTime(), false);
        setClipboard(timeStamp, str("revanced_share_copy_timestamp_success", timeStamp));
    }

    /**
     * Injection point.
     * <p>
     * Called from the in app download hook,
     * for both the player action button (below the video)
     * and the 'Download video' flyout option for feed videos.
     * <p>
     * Appears to always be called from the main thread.
     */
    public static boolean inAppDownloadButtonOnClick(String videoId) {
        try {
            if (!externalDownloaderActionButton.get()) {
                return false;
            }
            if (videoId == null || videoId.isEmpty()) {
                return false;
            }
            launchExternalDownloader(videoId);

            return true;
        } catch (Exception ex) {
            Logger.printException(() -> "inAppDownloadButtonOnClick failure", ex);
        }
        return false;
    }

    public static void launchExternalDownloader() {
        launchExternalDownloader(VideoInformation.getVideoId());
    }

    public static void launchExternalDownloader(@NonNull String videoId) {
        try {
            String downloaderPackageName = externalDownloaderPackageName.get().trim();

            if (downloaderPackageName.isEmpty()) {
                externalDownloaderPackageName.resetToDefault();
                downloaderPackageName = externalDownloaderPackageName.defaultValue;
            }

            if (!checkPackageIsEnabled()) {
                return;
            }

            isExternalDownloaderLaunched.compareAndSet(false, true);
            final String content = String.format("https://youtu.be/%s", videoId);
            launchExternalDownloader(content, downloaderPackageName);
        } catch (Exception ex) {
            Logger.printException(() -> "launchExternalDownloader failure", ex);
        } finally {
            runOnMainThreadDelayed(() -> isExternalDownloaderLaunched.compareAndSet(true, false), 500);
        }
    }

    /**
     * Create playlist from all channel videos from oldest to newest,
     * starting from the video where button is clicked.
     */
    public static void playlistFromChannelVideosListener(boolean activated) {
        String baseUri = "vnd.youtube://" + VideoInformation.getVideoId() + "?start=" + VideoInformation.getVideoTime() / 1000;
        if (activated) {
            baseUri += "&list=UL" + VideoInformation.getVideoId();
        }

        launchView(baseUri, getContext().getPackageName());
    }

    public static void showPlaybackSpeedDialog(@NonNull Context context) {
        final String[] playbackSpeedWithAutoEntries = CustomPlaybackSpeedPatch.getListEntries();
        final String[] playbackSpeedWithAutoEntryValues = CustomPlaybackSpeedPatch.getListEntryValues();

        final String[] playbackSpeedEntries = Arrays.copyOfRange(playbackSpeedWithAutoEntries, 1, playbackSpeedWithAutoEntries.length);
        final String[] playbackSpeedEntryValues = Arrays.copyOfRange(playbackSpeedWithAutoEntryValues, 1, playbackSpeedWithAutoEntryValues.length);

        final float playbackSpeed = VideoInformation.getPlaybackSpeed();
        final int index = Arrays.binarySearch(playbackSpeedEntryValues, String.valueOf(playbackSpeed));

        new AlertDialog.Builder(context)
                .setSingleChoiceItems(playbackSpeedEntries, index, (mDialog, mIndex) -> {
                    final float selectedPlaybackSpeed = Float.parseFloat(playbackSpeedEntryValues[mIndex] + "f");
                    VideoInformation.overridePlaybackSpeed(selectedPlaybackSpeed);
                    userSelectedPlaybackSpeed(selectedPlaybackSpeed);
                    mDialog.dismiss();
                })
                .show();
    }

    public static void showFlyoutMenu() {
        if (Settings.APPEND_TIME_STAMP_INFORMATION_TYPE.get()) {
            showVideoQualityFlyoutMenu();
        } else {
            showPlaybackSpeedFlyoutMenu();
        }
    }

    public static String getFormattedTimeStamp(long videoTime) {
        return "'" + videoTime +
                "' (" +
                getTimeStamp(videoTime, false) +
                ")\n";
    }

    @TargetApi(26)
    @SuppressLint("DefaultLocale")
    public static String getTimeStamp(long time, boolean mills) {
        final Duration duration = Duration.ofMillis(time);

        final long hours = duration.toHours();
        final long minutes = duration.toMinutes() % 60;
        final long seconds = duration.getSeconds() % 60;
        final long millis = duration.toMillis() % 1000;

        if (mills) {
            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
        } else {
            if (hours > 0) {
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format("%02d:%02d", minutes, seconds);
            }
        }
    }

    public static long getVideoTime(String str) {
        if (str == null || str.isEmpty())
            return 0;

        String[] timeFormat = str.split(":");
        String[] secondAndMills = timeFormat[2].split("\\.");

        String hours = timeFormat[0];
        String minutes = timeFormat[1];
        String second = secondAndMills[0];
        String mills = secondAndMills[1];

        return Long.parseLong(hours) * 60 * 60 * 1000
                + Long.parseLong(minutes) * 60 * 1000
                + Long.parseLong(second) * 1000
                + Long.parseLong(mills);
    }

    public static String getFormattedQualityString(@Nullable String prefix) {
        final String qualityString = VideoInformation.getVideoQualityString();

        return prefix == null ? qualityString : String.format("%s\u2009•\u2009%s", prefix, qualityString);
    }

    public static String getFormattedSpeedString(@Nullable String prefix) {
        final float playbackSpeed = VideoInformation.getPlaybackSpeed();

        final String playbackSpeedString = isRightToLeftTextLayout()
                ? "\u2066x\u2069" + playbackSpeed
                : playbackSpeed + "x";

        return prefix == null ? playbackSpeedString : String.format("%s\u2009•\u2009%s", prefix, playbackSpeedString);
    }

    /**
     * Injection point.
     * Disable PiP mode when an external downloader Intent is started.
     */
    public static boolean getExternalDownloaderLaunchedState(boolean original) {
        return !isExternalDownloaderLaunched.get() && original;
    }

    /**
     * Rest of the implementation added by patch.
     */
    public static void showPlaybackSpeedFlyoutMenu() {
        Logger.printDebug(() -> "Playback speed flyout menu opened");
    }

    /**
     * Rest of the implementation added by patch.
     */
    public static void showVideoQualityFlyoutMenu() {
        // These instructions are ignored by patch.
        Log.d("Extended: VideoUtils", "Video quality flyout menu opened");
    }
}
