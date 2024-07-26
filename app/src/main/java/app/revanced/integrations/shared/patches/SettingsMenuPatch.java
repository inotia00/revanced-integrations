package app.revanced.integrations.shared.patches;

import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.hideViewGroupByMarginLayoutParams;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import app.revanced.integrations.shared.settings.BaseSettings;
import app.revanced.integrations.shared.utils.Logger;

@SuppressWarnings("unused")
public final class SettingsMenuPatch {

    private static String[] settingsMenuBlockList = BaseSettings.HIDE_SETTINGS_MENU_FILTER_STRINGS.get().split("\\n");
    private static final String hideSettingsLabel = str("revanced_hide_settings_menu_title");
    private static final String rvxSettingsLabel = str("revanced_extended_settings_title");

    public static void hideSettingsMenu(RecyclerView recyclerView) {
        if (!BaseSettings.HIDE_SETTINGS_MENU.get())
            return;

        // RVX Settings are not hidden.
        settingsMenuBlockList = Arrays.stream(settingsMenuBlockList)
                .filter(item -> !StringUtils.equalsAny(item, hideSettingsLabel, rvxSettingsLabel))
                .toArray(String[]::new);

        recyclerView.getViewTreeObserver().addOnDrawListener(() -> {
            final int childCount = recyclerView.getChildCount();
            if (childCount == 0)
                return;
            for (int i = 0; i <= childCount; i++) {
                if (recyclerView.getChildAt(i) instanceof ViewGroup linearLayout
                        && linearLayout.getChildCount() > 1
                        && linearLayout.getChildAt(1) instanceof ViewGroup relativeLayout
                        && relativeLayout.getChildAt(0) instanceof TextView textView
                ) {
                    final String title = textView.getText().toString();
                    Logger.printDebug(() -> title);
                    for (String filter : settingsMenuBlockList) {
                        if (!filter.isEmpty() && title.equals(filter)) {
                            hideViewGroupByMarginLayoutParams(linearLayout);
                        }
                    }
                }
            }
        });
    }
}