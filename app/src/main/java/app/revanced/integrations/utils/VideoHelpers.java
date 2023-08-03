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

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import app.revanced.integrations.patches.video.CustomPlaybackSpeedPatch;
import app.revanced.integrations.patches.video.VideoInformation;
import app.revanced.integrations.settings.SettingsEnum;

public class VideoHelpers {

    public static float currentSpeed;

    public static void copyUrl(Context context, Boolean withTimestamp) {
        try {
            String url = String.format("https://youtu.be/%s", VideoInformation.getVideoId());
            if (withTimestamp) {
                long seconds = VideoInformation.getVideoTime() / 1000;
                url += String.format("?t=%s", seconds);
            }

            setClipboard(context, url);
            showToastShort(context, str("share_copy_url_success"));
        } catch (Exception e) {
            LogHelper.printException(VideoHelpers.class, "Failed to generate video url", e);
        }
    }

    public static void copyTimeStamp(Context context) {
        try {
            long videoTime = VideoInformation.getVideoTime();

            Duration duration = Duration.ofMillis(videoTime);

            long h = duration.toHours();
            long m = duration.toMinutes() % 60;
            long s = duration.getSeconds() % 60;

            @SuppressLint("DefaultLocale") String timeStamp = h > 0 ? String.format("%02d:%02d:%02d", h, m, s) : String.format("%02d:%02d", m, s);

            setClipboard(context, timeStamp);
            showToastShort(context, str("revanced_copy_video_timestamp_success") + ": " + timeStamp);
        } catch (Exception ex) {
            LogHelper.printException(VideoHelpers.class, "Failed to generate video url", ex);
        }
    }

    public static void download(Context context) {
        try {
            var packageName = SettingsEnum.EXTERNAL_DOWNLOADER_PACKAGE_NAME.getString().trim();

            if (packageName.isEmpty()) {
                final String defaultValue = SettingsEnum.EXTERNAL_DOWNLOADER_PACKAGE_NAME.defaultValue.toString();
                SettingsEnum.EXTERNAL_DOWNLOADER_PACKAGE_NAME.saveValue(defaultValue);
                packageName = defaultValue;
            }

            if (!isPackageEnabled(context, packageName)) {
                showToastShort(str("revanced_external_downloader_not_installed_warning", getExternalDownloaderName(context, packageName)));
                return;
            }

            startDownloaderActivity(context, packageName, String.format("https://youtu.be/%s", VideoInformation.getVideoId()));
        } catch (Exception ex) {
            LogHelper.printException(VideoHelpers.class, "Failed to launch the intent: ", ex);
        }
    }

    private static String getExternalDownloaderName(Context context, String packageName) {
        try {
            final var EXTERNAL_DOWNLOADER_LABEL_PREFERENCE_KEY = "revanced_external_downloader_label";
            final var EXTERNAL_DOWNLOADER_PACKAGE_NAME_PREFERENCE_KEY = "revanced_external_downloader_package_name";

            String[] labelArray = getStringArray(context, EXTERNAL_DOWNLOADER_LABEL_PREFERENCE_KEY);
            String[] packageNameArray = getStringArray(context, EXTERNAL_DOWNLOADER_PACKAGE_NAME_PREFERENCE_KEY);

            int findIndex = Arrays.binarySearch(packageNameArray, packageName);

            return findIndex >= 0 ? labelArray[findIndex] : packageName;
        } catch (Exception e) {
            LogHelper.printException(VideoHelpers.class, "Failed to set ExternalDownloaderName", e);
        }
        return packageName;
    }

    public static void startDownloaderActivity(Context context, String downloaderPackageName, String content) {
        try {
            var intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.setPackage(downloaderPackageName);
            intent.putExtra("android.intent.extra.TEXT", content);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            LogHelper.printException(VideoHelpers.class, "Unable to start DownloaderActivity", e);
        }
    }

    public static void playbackSpeedDialogListener(Context context) {
        String[] speedEntries = CustomPlaybackSpeedPatch.getListEntries();
        String[] speedEntryValues = CustomPlaybackSpeedPatch.getListEntryValues();

        AlertDialog speedDialog = new AlertDialog.Builder(context)
                .setTitle(setTitle(str("revanced_whitelisting_speed_button")))
                .setItems(speedEntries, (dialog, index) -> overrideSpeedBridge(Float.parseFloat(speedEntryValues[index] + "f")))
                .show();

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        WindowManager.LayoutParams params = Objects.requireNonNull(speedDialog.getWindow()).getAttributes();
        params.width = (int) (size.x * 0.5);
        speedDialog.getWindow().setAttributes(params);
        speedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static String setTitle(String prefix) {
        String speed = ReVancedUtils.isRightToLeftTextLayout()
                ? "\u2066x\u2069" + currentSpeed  // u202E = right to left character
                : currentSpeed + "x"; // u202D = left to right character
        if (prefix == null) return speed;
        return String.format("%s\u2009•\u2009%s", prefix, speed);
    }

    private static void overrideSpeedBridge(final float speed) {
        overrideSpeed(speed);
        userChangedSpeed(speed);
    }

    private static void setClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("link", text);
        clipboard.setPrimaryClip(clip);
    }

    public static float getCurrentSpeed() {
        return currentSpeed;
    }
}
