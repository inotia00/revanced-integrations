package app.revanced.integrations.youtube.patches.misc;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.components.ShareSheetMenuFilter;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class ShareSheetPatch {
    private static final boolean changeShareSheetEnabled = Settings.CHANGE_SHARE_SHEET.get();

    /**
     * Injection point.
     */
    public static void onShareSheetMenuCreate(final RecyclerView recyclerView) {
        if (!changeShareSheetEnabled)
            return;

        recyclerView.getViewTreeObserver().addOnDrawListener(() -> {
            try {
                if (ShareSheetMenuFilter.isShareSheetMenuVisible &&
                        recyclerView.getChildAt(0) instanceof ViewGroup parentView4th &&
                        parentView4th.getChildAt(0) instanceof ViewGroup parentView3rd &&
                        parentView3rd.getChildAt(0) instanceof ViewGroup parentView2nd &&
                        parentView2nd.getChildAt(parentView2nd.getChildCount() - 1) instanceof ViewGroup parentView &&
                        parentView.getChildAt(0) instanceof ViewGroup shareWithOtherAppsView
                ) {
                    ShareSheetMenuFilter.isShareSheetMenuVisible = false;

                    recyclerView.setVisibility(View.GONE);
                    Utils.clickView(shareWithOtherAppsView);
                }
            } catch (Exception ex) {
                Logger.printException(() -> "onShareSheetMenuCreate failure", ex);
            }
        });
    }

    /**
     * Injection point.
     */
    public static String overridePackageName(String original) {
        return changeShareSheetEnabled ? "" : original;
    }

}
