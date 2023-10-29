package app.revanced.integrations.patches.layout;

import android.graphics.Color;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.VideoHelpers;

public class SeekBarPatch {
    /**
     * Default color of seekbar.
     */
    public static final int ORIGINAL_SEEKBAR_COLOR = 0xFFFF0000;

    public static String appendTimeStampInformation(String original) {
        if (!SettingsEnum.APPEND_TIME_STAMP_INFORMATION.getBoolean())
            return original;

        final String regex = "\\((.*?)\\)";
        final Matcher matcher = Pattern.compile(regex).matcher(original);

        if (matcher.find()) {
            String matcherGroup = matcher.group(1);
            String appendString = String.format(
                    "\u2009• %s",
                    SettingsEnum.APPEND_TIME_STAMP_INFORMATION_TYPE.getBoolean()
                            ? VideoHelpers.getFormattedQualityString(matcherGroup)
                            : VideoHelpers.getFormattedSpeedString(matcherGroup)
            );
            return original.replaceAll(regex, "") + appendString;
        } else {
            String appendString = String.format(
                    "\u2009• %s",
                    SettingsEnum.APPEND_TIME_STAMP_INFORMATION_TYPE.getBoolean()
                            ? VideoHelpers.getFormattedQualityString(null)
                            : VideoHelpers.getFormattedSpeedString(null)
            );
            return original + appendString;
        }
    }

    public static boolean enableNewThumbnailPreview() {
        return SettingsEnum.ENABLE_NEW_THUMBNAIL_PREVIEW.getBoolean();
    }

    public static boolean enableSeekbarTapping() {
        return SettingsEnum.ENABLE_SEEKBAR_TAPPING.getBoolean();
    }

    public static boolean hideTimeStamp() {
        return SettingsEnum.HIDE_TIME_STAMP.getBoolean();
    }

    public static boolean hideSeekbar() {
        return SettingsEnum.HIDE_SEEKBAR.getBoolean();
    }

    public static void setContainerClickListener(View view) {
        if (!SettingsEnum.APPEND_TIME_STAMP_INFORMATION.getBoolean())
            return;

        if (!(view.getParent() instanceof View containerView))
            return;

        final SettingsEnum appendTypeSetting = SettingsEnum.APPEND_TIME_STAMP_INFORMATION_TYPE;
        final boolean previousBoolean = appendTypeSetting.getBoolean();

        containerView.setOnLongClickListener(timeStampContainerView -> {
                    appendTypeSetting.saveValue(!previousBoolean);
                    return true;
                }
        );
    }

    /**
     * Injection point.
     */
    public static int getSeekbarClickedColorValue(final int colorValue) {
        return colorValue == ORIGINAL_SEEKBAR_COLOR
                ? overrideSeekbarColor(colorValue)
                : colorValue;
    }

    /**
     * Injection point.
     */
    public static int resumedProgressBarColor(final int colorValue) {
        return SettingsEnum.ENABLE_CUSTOM_SEEKBAR_COLOR.getBoolean()
                ? getSeekbarClickedColorValue(colorValue)
                : colorValue;
    }

    /**
     * Injection point.
     * <p>
     * Overrides all Litho components that use the YouTube seekbar color.
     * Used only for the video thumbnails seekbar.
     * <p>
     * If {@link SettingsEnum#HIDE_SEEKBAR_THUMBNAIL} is enabled, this returns a fully transparent color.
     */
    public static int getLithoColor(int colorValue) {
        if (colorValue == ORIGINAL_SEEKBAR_COLOR) {
            if (SettingsEnum.HIDE_SEEKBAR_THUMBNAIL.getBoolean()) {
                return 0x00000000;
            }
            return overrideSeekbarColor(ORIGINAL_SEEKBAR_COLOR);
        }
        return colorValue;
    }

    /**
     * Points where errors occur when playing videos on the PlayStore (ROOT Build)
     */
    public static int overrideSeekbarColor(final int colorValue) {
        try {
            return SettingsEnum.ENABLE_CUSTOM_SEEKBAR_COLOR.getBoolean()
                    ? Color.parseColor(SettingsEnum.ENABLE_CUSTOM_SEEKBAR_COLOR_VALUE.getString())
                    : colorValue;
        } catch (Exception ignored) {
        }
        return colorValue;
    }

}
