package app.revanced.integrations.utils;

import static app.revanced.integrations.patches.video.PlaybackSpeedPatch.overrideSpeed;
import static app.revanced.integrations.patches.video.PlaybackSpeedPatch.userChangedSpeed;
import static app.revanced.integrations.utils.ReVancedHelper.getStringArray;
import static app.revanced.integrations.utils.ReVancedHelper.isPackageEnabled;
import static app.revanced.integrations.utils.ReVancedUtils.showToastShort;
import static app.revanced.integrations.utils.StringRef.str;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import app.revanced.integrations.patches.video.CustomPlaybackSpeedPatch;
import app.revanced.integrations.patches.video.VideoInformation;
import app.revanced.integrations.patches.video.VideoQualityPatch;
import app.revanced.integrations.settings.SettingsEnum;

public class VideoHelpers {

    public static String currentQuality = "";
    public static float currentSpeed;
    public static String qualityAutoString = "Auto";
    public static boolean isDownloadBtnOnClick = false;

    public static void copyUrl(@NonNull Context context, Boolean withTimestamp) {
        String url = String.format("https://youtu.be/%s", VideoInformation.getVideoId());
        if (withTimestamp) {
            long seconds = VideoInformation.getVideoTime() / 1000;
            url += String.format("?t=%s", seconds);
        }

        ReVancedUtils.setClipboard(url);
        showToastShort(context, str("share_copy_url_success"));
    }

    @SuppressLint("DefaultLocale")
    public static void copyTimeStamp(@NonNull Context context) {
        final long videoTime = VideoInformation.getVideoTime();

        final Duration duration = Duration.ofMillis(videoTime);

        final long h = duration.toHours();
        final long m = duration.toMinutes() % 60;
        final long s = duration.getSeconds() % 60;

        final String timeStamp = h > 0
                ? String.format("%02d:%02d:%02d", h, m, s)
                : String.format("%02d:%02d", m, s);

        ReVancedUtils.setClipboard(timeStamp);
        showToastShort(context, str("revanced_copy_video_timestamp_success") + ":\u2009" + timeStamp);
    }

    public static void download(@NonNull Context context) {
        String downloaderPackageName = SettingsEnum.EXTERNAL_DOWNLOADER_PACKAGE_NAME.getString().trim();

        if (downloaderPackageName.isEmpty()) {
            final String defaultValue = SettingsEnum.EXTERNAL_DOWNLOADER_PACKAGE_NAME.defaultValue.toString();
            SettingsEnum.EXTERNAL_DOWNLOADER_PACKAGE_NAME.saveValue(defaultValue);
            downloaderPackageName = defaultValue;
        }

        if (!isPackageEnabled(context, downloaderPackageName)) {
            showToastShort(str("revanced_external_downloader_not_installed_warning", getExternalDownloaderName(context, downloaderPackageName)));
            return;
        }

        isDownloadBtnOnClick = true;

        startDownloaderActivity(context, downloaderPackageName, String.format("https://youtu.be/%s", VideoInformation.getVideoId()));

        ReVancedUtils.runOnMainThreadDelayed(() -> {
            isDownloadBtnOnClick = false;
        }, 500L);
    }

    @NonNull
    private static String getExternalDownloaderName(@NonNull Context context, @NonNull String packageName) {
        try {
            final String EXTERNAL_DOWNLOADER_LABEL_PREFERENCE_KEY = "revanced_external_downloader_label";
            final String EXTERNAL_DOWNLOADER_PACKAGE_NAME_PREFERENCE_KEY = "revanced_external_downloader_package_name";

            final String[] labelArray = getStringArray(context, EXTERNAL_DOWNLOADER_LABEL_PREFERENCE_KEY);
            final String[] packageNameArray = getStringArray(context, EXTERNAL_DOWNLOADER_PACKAGE_NAME_PREFERENCE_KEY);

            final int findIndex = Arrays.binarySearch(packageNameArray, packageName);

            return findIndex >= 0 ? labelArray[findIndex] : packageName;
        } catch (Exception e) {
            LogHelper.printException(VideoHelpers.class, "Failed to set ExternalDownloaderName", e);
        }
        return packageName;
    }

    public static void startDownloaderActivity(@NonNull Context context, @NonNull String downloaderPackageName, @NonNull String content) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.setPackage(downloaderPackageName);
        intent.putExtra("android.intent.extra.TEXT", content);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void playbackSpeedDialogListener(@NonNull Context context) {
        final String[] playbackSpeedWithAutoEntries = CustomPlaybackSpeedPatch.getListEntries();
        final String[] playbackSpeedWithAutoEntryValues = CustomPlaybackSpeedPatch.getListEntryValues();

        final String[] playbackSpeedEntries = Arrays.copyOfRange(playbackSpeedWithAutoEntries, 1, playbackSpeedWithAutoEntries.length);
        final String[] playbackSpeedEntryValues = Arrays.copyOfRange(playbackSpeedWithAutoEntryValues, 1, playbackSpeedWithAutoEntryValues.length);

        final int index = Arrays.binarySearch(playbackSpeedEntryValues, String.valueOf(currentSpeed));

        AlertDialog speedDialog = new AlertDialog.Builder(context)
                .setSingleChoiceItems(playbackSpeedEntries, index, (mDialog, mIndex) -> {
                    overrideSpeedBridge(Float.parseFloat(playbackSpeedEntryValues[mIndex] + "f"));
                    mDialog.dismiss();
                })
                .show();

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        WindowManager.LayoutParams params = Objects.requireNonNull(speedDialog.getWindow()).getAttributes();
        params.width = (int) (size.x * 0.45);
        speedDialog.getWindow().setAttributes(params);
        speedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static String getFormattedQualityString(@Nullable String prefix) {
        final String qualityString = getQualityString();

        return prefix == null ? qualityString : String.format("%s\u2009•\u2009%s", prefix, qualityString);
    }

    public static String getFormattedSpeedString(@Nullable String prefix) {
        final String speedString = ReVancedUtils.isRightToLeftTextLayout()
                ? "\u2066x\u2069" + currentSpeed
                : currentSpeed + "x";

        return prefix == null ? speedString : String.format("%s\u2009•\u2009%s", prefix, speedString);
    }

    private static void overrideSpeedBridge(final float speed) {
        overrideSpeed(speed);
        userChangedSpeed(speed);
    }

    public static float getCurrentSpeed() {
        return currentSpeed;
    }

    public static boolean isDownloadBtnClicked() {
        return isDownloadBtnOnClick;
    }

    public static String getQualityString() {
        if (currentQuality.isEmpty()) {
            VideoQualityPatch.overrideQuality(720);
            return qualityAutoString;
        } else if (currentQuality.equals(qualityAutoString)) {
            return qualityAutoString;
        }

        return currentQuality.split("p")[0] + "p";
    }
}
