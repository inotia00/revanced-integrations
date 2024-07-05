package app.revanced.integrations.youtube.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.revanced.integrations.shared.settings.BooleanSetting;
import app.revanced.integrations.shared.settings.IntegerSetting;
import app.revanced.integrations.shared.settings.StringSetting;
import app.revanced.integrations.shared.utils.IntentUtils;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.patches.video.CustomPlaybackSpeedPatch;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.VideoInformation;
import app.revanced.integrations.youtube.swipecontrols.controller.AudioVolumeController;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static app.revanced.integrations.shared.utils.ResourceUtils.getStringArray;
import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.youtube.patches.video.PlaybackSpeedPatch.userSelectedPlaybackSpeed;
import static app.revanced.integrations.youtube.settings.preference.ExternalDownloaderPreference.checkPackageIsEnabled;

@SuppressWarnings("unused")
public class VideoUtils extends IntentUtils {
    private static final BooleanSetting externalDownloaderActionButton =
            Settings.EXTERNAL_DOWNLOADER_ACTION_BUTTON;
    private static final StringSetting externalDownloaderPackageName =
            Settings.EXTERNAL_DOWNLOADER_PACKAGE_NAME;
    private static final AtomicBoolean isExternalDownloaderLaunched = new AtomicBoolean(false);
    public static AudioVolumeController audioVolumeController = new AudioVolumeController(getContext(), AudioManager.STREAM_MUSIC);
    private static Integer previousVolumeLevel = 0;


    public static void toggleMuteVolume() {
        int currentVolume = audioVolumeController.getVolume();
        if (currentVolume > 0) {
            // Mute the volume
            audioVolumeController.setVolume(0);
            // save the current volume level to restore later
            previousVolumeLevel = currentVolume;
        } else {
            // Unmute the volume - restore the previous volume level
            audioVolumeController.setVolume(
                    previousVolumeLevel > 0 ? previousVolumeLevel : audioVolumeController.getMaxVolume()
            );
        }
    }

    public static boolean isAudioMuted() {
        return audioVolumeController.getVolume() == 0;
    }

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
        final String timeStamp = getTimeStamp(VideoInformation.getVideoTime());
        setClipboard(timeStamp, str("revanced_share_copy_timestamp_success", timeStamp));
    }

    /**
     * Injection point.
     * <p>
     * Called from the in-app download hook,
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
        final String videoId = VideoInformation.getVideoId();
        String baseUri = "vnd.youtube://" + videoId + "?start=" + VideoInformation.getVideoTime() / 1000;
        if (activated) {
            baseUri += "&list=UL" + videoId;
        }

        launchView(baseUri, getContext().getPackageName());
    }

    public static void showPlaybackSpeedDialog(@NonNull Context context) {
        final String[] playbackSpeedEntries = CustomPlaybackSpeedPatch.getTrimmedListEntries();
        final String[] playbackSpeedEntryValues = CustomPlaybackSpeedPatch.getTrimmedListEntryValues();

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

    private static int mClickedDialogEntryIndex;

    public static void showShortsRepeatDialog(@NonNull Context context) {
        final IntegerSetting setting = Settings.CHANGE_SHORTS_REPEAT_STATE;
        final String settingsKey = setting.key;

        final String entryKey = settingsKey + "_entries";
        final String entryValueKey = settingsKey + "_entry_values";
        final String[] mEntries = getStringArray(entryKey);
        final String[] mEntryValues = getStringArray(entryValueKey);

        final int findIndex = Arrays.binarySearch(mEntryValues, String.valueOf(setting.get()));
        mClickedDialogEntryIndex = findIndex >= 0 ? findIndex : setting.defaultValue;

        new AlertDialog.Builder(context)
                .setTitle(str(settingsKey + "_title"))
                .setSingleChoiceItems(mEntries, mClickedDialogEntryIndex, (dialog, id) -> {
                    mClickedDialogEntryIndex = id;
                    setting.save(id);
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, null)
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
                getTimeStamp(videoTime) +
                ")\n";
    }

    @TargetApi(26)
    @SuppressLint("DefaultLocale")
    public static String getTimeStamp(long time) {
        final Duration duration = Duration.ofMillis(time);

        final long hours = duration.toHours();
        final long minutes = duration.toMinutes() % 60;
        final long seconds = duration.getSeconds() % 60;
        final long millis = duration.toMillis() % 1000;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
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
