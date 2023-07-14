package app.revanced.integrations.patches.layout;

import static app.revanced.integrations.patches.ads.LowLevelFilter.isPlaybackRateMenuLoaded;
import static app.revanced.integrations.patches.ads.LowLevelFilter.isVideoQualitiesQuickMenuLoaded;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.facebook.litho.ComponentHost;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.ReVancedUtils;

public class FlyoutPanelPatch {
    /**
     * Dummy field.
     */
    public static Object playbackRateBottomSheetClass;

    public static void enableOldQualityMenu(ListView listView) {
        if (!SettingsEnum.ENABLE_OLD_QUALITY_LAYOUT.getBoolean())
            return;

        listView.setVisibility(View.GONE);

        ReVancedUtils.runOnMainThreadDelayed(() ->
                        listView.performItemClick(null, 4, 0),
                1
        );
    }

    /**
     * Old Quality Layout patch for new player flyout panels.
     *
     * @param linearLayout [BOTTOM_SHEET_FRAGMENT], {@ComponentHost} is located under linearLayout.
     */
    public static void enableOldQualityMenu(LinearLayout linearLayout) {
        if (!SettingsEnum.ENABLE_OLD_QUALITY_LAYOUT.getBoolean())
            return;

        // The RecyclerView is placed on the 3rd ChildView.
        if (linearLayout.getChildCount() != 3)
            return;

        // Make sure that this ChildView is castable to the RecyclerView.
        if (!(linearLayout.getChildAt(2) instanceof RecyclerView recyclerView))
            return;

        // When method called from the abstract class, only the RecyclerView is defined.
        // ComponentHost is not defined at here.
        // So we need to add ViewTreeObserver to RecyclerView and check the moment ComponentHost is added to this RecyclerView.
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Check if [VIDEO_QUALITIES_QUICK_MENU_BOTTOM_SHEET_FRAGMENT] is loaded.
                        // Since ViewTreeObserver is activated even when [VIDEO_QUALITIES_MENU_BOTTOM_SHEET_FRAGMENT] or [SUBTITLE_MENU_BOTTOM_SHEET_FRAGMENT] are loaded,
                        // So this check process is absolutely necessary.
                        if (!isVideoQualitiesQuickMenuLoaded)
                            return;

                        // ComponentHost is placed on the 1st ChildView.
                        if (recyclerView.getChildCount() != 1)
                            return;

                        // Make sure that this ChildView is castable to the ComponentHost.
                        if (!(recyclerView.getChildAt(0) instanceof ComponentHost lithoView))
                            return;

                        // TargetView found, hide [BOTTOM_SHEET_FRAGMENT].
                        linearLayout.setVisibility(View.GONE);

                        // Click the TargetView.
                        lithoView.getChildAt(3).performClick();

                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    /**
     * Old Playback Speed Layout patch for new player flyout panels.
     *
     * Currently, there is no way to implement the [custom-video-speed] patch in the new player flyout panels,
     * So calls the old style player flyout panels.
     *
     * @param linearLayout [BOTTOM_SHEET_FRAGMENT], {@ComponentHost} is located under linearLayout.
     */
    public static void enableOldPlaybackRateMenu(LinearLayout linearLayout) {
        if (!SettingsEnum.ENABLE_CUSTOM_PLAYBACK_SPEED.getBoolean())
            return;

        // The RecyclerView is placed on the 1st ChildView.
        if (linearLayout.getChildCount() != 2)
            return;

        // Make sure that this ChildView is castable to the RecyclerView.
        if (!(linearLayout.getChildAt(1) instanceof RecyclerView recyclerView))
            return;

        // When method called from the abstract class, only the RecyclerView is defined.
        // ComponentHost is not defined at here.
        // So we need to add ViewTreeObserver to RecyclerView and check the moment ComponentHost is added to this RecyclerView.
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // ComponentHost is placed on the 1st ChildView.
                        if (recyclerView.getChildCount() != 1)
                            return;

                        // Make sure that this ChildView is castable to the ComponentHost.
                        if (!(recyclerView.getChildAt(0) instanceof ComponentHost))
                            return;

                        // Check if [PLAYBACK_RATE_MENU_BOTTOM_SHEET_FRAGMENT] is loaded.
                        // Since ViewTreeObserver is activated even when [SUBTITLE_MENU_BOTTOM_SHEET_FRAGMENT] are loaded,
                        // So this check process is absolutely necessary.
                        if (!isPlaybackRateMenuLoaded)
                            return;

                        // Hide [BOTTOM_SHEET_FRAGMENT].
                        linearLayout.setVisibility(View.GONE);

                        // Open old playback rate bottom sheet fragment.
                        openOldPlaybackRateBottomSheetFragment();

                        // DismissView [R.id.touch_outside] is the 1st ChildView of the 3rd ParentView.
                        // This is the easiest way to close the new player flyout panels
                        ((ViewGroup) linearLayout.getParent().getParent().getParent()).getChildAt(0).performClick();

                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    public static CharSequence hideFeedFlyoutPanel(CharSequence charSequence) {
        if (charSequence == null || !SettingsEnum.HIDE_FEED_FLYOUT_PANEL.getBoolean())
            return charSequence;

        String[] blockList = SettingsEnum.HIDE_FEED_FLYOUT_PANEL_FILTER_STRINGS.getString().split("\\n");
        String targetString = charSequence.toString();

        for (String filter : blockList) {
            if (targetString.equals(filter) && !filter.isEmpty())
                return null;
        }

        return charSequence;
    }

    /**
     * Add command to open Playback Speed Bottom Sheet Fragment in patch.
     */
    private static void openOldPlaybackRateBottomSheetFragment() {
        if (playbackRateBottomSheetClass == null)
            return;
        playbackRateBottomSheetClass.getClass();
    }
}
