package app.revanced.integrations.youtube.patches.video;

import static app.revanced.integrations.shared.utils.ResourceUtils.getString;
import static app.revanced.integrations.shared.utils.StringRef.str;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Arrays;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.components.PlaybackSpeedMenuFilter;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.utils.VideoUtils;

@SuppressWarnings("unused")
public class CustomPlaybackSpeedPatch {
    /**
     * Maximum playback speed, exclusive value.  Custom speeds must be less than this value.
     */
    private static final float MAXIMUM_PLAYBACK_SPEED = 8;
    private static final String[] defaultSpeedEntries;
    private static final String[] defaultSpeedEntryValues;
    /**
     * Custom playback speeds.
     */
    private static float[] playbackSpeeds;
    private static String[] customSpeedEntries;
    private static String[] customSpeedEntryValues;

    private static String[] playbackSpeedEntries;
    private static String[] playbackSpeedEntryValues;

    /**
     * The last time the old playback menu was forcefully called.
     */
    private static long lastTimeOldPlaybackMenuInvoked;

    static {
        defaultSpeedEntries = new String[]{getString("quality_auto"), "0.25x", "0.5x", "0.75x", getString("revanced_playback_speed_normal"), "1.25x", "1.5x", "1.75x", "2.0x"};
        defaultSpeedEntryValues = new String[]{"-2.0", "0.25", "0.5", "0.75", "1.0", "1.25", "1.5", "1.75", "2.0"};

        loadSpeeds();
    }

    /**
     * Injection point.
     */
    public static float[] getArray(float[] original) {
        return isCustomPlaybackSpeedEnabled() ? playbackSpeeds : original;
    }

    /**
     * Injection point.
     */
    public static int getLength(int original) {
        return isCustomPlaybackSpeedEnabled() ? playbackSpeeds.length : original;
    }

    /**
     * Injection point.
     */
    public static int getSize(int original) {
        return isCustomPlaybackSpeedEnabled() ? 0 : original;
    }

    public static String[] getListEntries() {
        return isCustomPlaybackSpeedEnabled()
                ? customSpeedEntries
                : defaultSpeedEntries;
    }

    public static String[] getListEntryValues() {
        return isCustomPlaybackSpeedEnabled()
                ? customSpeedEntryValues
                : defaultSpeedEntryValues;
    }

    public static String[] getTrimmedListEntries() {
        if (playbackSpeedEntries == null) {
            final String[] playbackSpeedWithAutoEntries = getListEntries();
            playbackSpeedEntries = Arrays.copyOfRange(playbackSpeedWithAutoEntries, 1, playbackSpeedWithAutoEntries.length);
        }

        return playbackSpeedEntries;
    }

    public static String[] getTrimmedListEntryValues() {
        if (playbackSpeedEntryValues == null) {
            final String[] playbackSpeedWithAutoEntryValues = getListEntryValues();
            playbackSpeedEntryValues = Arrays.copyOfRange(playbackSpeedWithAutoEntryValues, 1, playbackSpeedWithAutoEntryValues.length);
        }

        return playbackSpeedEntryValues;
    }

    private static void resetCustomSpeeds(@NonNull String toastMessage) {
        Utils.showToastLong(toastMessage);
        Settings.CUSTOM_PLAYBACK_SPEEDS.resetToDefault();
    }

    private static void loadSpeeds() {
        try {
            if (!Settings.ENABLE_CUSTOM_PLAYBACK_SPEED.get()) return;

            String[] speedStrings = Settings.CUSTOM_PLAYBACK_SPEEDS.get().split("\\s+");
            Arrays.sort(speedStrings);
            if (speedStrings.length == 0) {
                throw new IllegalArgumentException();
            }
            playbackSpeeds = new float[speedStrings.length];
            for (int i = 0, length = speedStrings.length; i < length; i++) {
                final float speed = Float.parseFloat(speedStrings[i]);
                if (speed <= 0 || arrayContains(playbackSpeeds, speed)) {
                    throw new IllegalArgumentException();
                }
                if (speed > MAXIMUM_PLAYBACK_SPEED) {
                    resetCustomSpeeds(str("revanced_custom_playback_speeds_invalid", MAXIMUM_PLAYBACK_SPEED + ""));
                    loadSpeeds();
                    return;
                }
                playbackSpeeds[i] = speed;
            }

            if (customSpeedEntries != null) return;

            customSpeedEntries = new String[playbackSpeeds.length + 1];
            customSpeedEntryValues = new String[playbackSpeeds.length + 1];
            customSpeedEntries[0] = getString("quality_auto");
            customSpeedEntryValues[0] = "-2.0";

            int i = 1;
            for (float speed : playbackSpeeds) {
                String speedString = String.valueOf(speed);
                customSpeedEntries[i] = speed != 1.0f
                        ? speedString + "x"
                        : getString("revanced_playback_speed_normal");
                customSpeedEntryValues[i] = speedString;
                i++;
            }
        } catch (Exception ex) {
            Logger.printInfo(() -> "parse error", ex);
            resetCustomSpeeds(str("revanced_custom_playback_speeds_parse_exception"));
            loadSpeeds();
        }
    }

    private static boolean arrayContains(float[] array, float value) {
        for (float arrayValue : array) {
            if (arrayValue == value) return true;
        }
        return false;
    }

    private static boolean isCustomPlaybackSpeedEnabled() {
        return Settings.ENABLE_CUSTOM_PLAYBACK_SPEED.get() && playbackSpeeds != null;
    }

    /**
     * Injection point.
     */
    public static void onFlyoutMenuCreate(final RecyclerView recyclerView) {
        if (!Settings.ENABLE_CUSTOM_PLAYBACK_SPEED.get())
            return;

        recyclerView.getViewTreeObserver().addOnDrawListener(() -> {
            try {
                // Check if the current view is the playback speed menu.
                if (!PlaybackSpeedMenuFilter.isPlaybackSpeedMenuVisible || recyclerView.getChildCount() == 0) {
                    return;
                }

                if (!(recyclerView.getChildAt(0) instanceof ViewGroup playbackSpeedParentView)) {
                    return;
                }

                // For some reason, the custom playback speed flyout panel is activated when the user opens the share panel. (A/B tests)
                // Check the child count of playback speed flyout panel to prevent this issue.
                // Child count of playback speed flyout panel is always 8.
                if (playbackSpeedParentView.getChildCount() != 8) {
                    return;
                }

                PlaybackSpeedMenuFilter.isPlaybackSpeedMenuVisible = false;

                if (!(Utils.getParentView(recyclerView, 3) instanceof ViewGroup parentView3rd)) {
                    return;
                }

                if (!(parentView3rd.getParent() instanceof ViewGroup parentView4th)) {
                    return;
                }

                // Dismiss View [R.id.touch_outside] is the 1st ChildView of the 4th ParentView.
                // This only shows in phone layout
                Utils.clickView(parentView4th.getChildAt(0));

                // In tablet layout, there is no Dismiss View, instead we just hide all two parent views.
                parentView3rd.setVisibility(View.GONE);
                parentView4th.setVisibility(View.GONE);

                // Show custom playback speed menu.
                showCustomPlaybackSpeedMenu(recyclerView.getContext());
            } catch (Exception ex) {
                Logger.printException(() -> "onFlyoutMenuCreate failure", ex);
            }
        });
    }

    /**
     * This method is sometimes used multiple times
     * To prevent this, ignore method reuse within 1 second.
     *
     * @param context Context for [playbackSpeedDialogListener]
     */
    private static void showCustomPlaybackSpeedMenu(@NonNull Context context) {
        // This method is sometimes used multiple times.
        // To prevent this, ignore method reuse within 1 second.
        final long now = System.currentTimeMillis();
        if (now - lastTimeOldPlaybackMenuInvoked < 1000) {
            return;
        }
        lastTimeOldPlaybackMenuInvoked = now;

        if (Settings.CUSTOM_PLAYBACK_SPEED_MENU_TYPE.get()) {
            // Open playback speed dialog
            VideoUtils.showPlaybackSpeedDialog(context);
        } else {
            // Open old style flyout menu
            VideoUtils.showPlaybackSpeedFlyoutMenu();
        }
    }

}
