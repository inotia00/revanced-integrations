package app.revanced.integrations.shared.patches;

import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.hideViewGroupByMarginLayoutParams;
import static app.revanced.integrations.shared.utils.Utils.isSDKAbove;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.revanced.integrations.shared.settings.BaseSettings;
import app.revanced.integrations.shared.utils.Logger;

@SuppressWarnings("unused")
public final class SettingsMenuPatch {
    private static String[] settingsMenuBlockList;

    static {
        settingsMenuBlockList = BaseSettings.HIDE_SETTINGS_MENU_FILTER_STRINGS.get().split("\\n");
        // Some settings should not be hidden.
        if (isSDKAbove(24)) {
            settingsMenuBlockList = Arrays.stream(settingsMenuBlockList)
                    .filter(item -> !StringUtils.equalsAny(item, str("revanced_hide_settings_menu_title"), str("revanced_extended_settings_title")))
                    .toArray(String[]::new);
        } else {
            final List<String> tmpList = new ArrayList<>(Arrays.asList(settingsMenuBlockList));
            tmpList.removeAll(Arrays.asList(str("revanced_hide_settings_menu_title"), str("revanced_extended_settings_title")));
            settingsMenuBlockList = tmpList.toArray(new String[0]);
        }
    }

    public static void hideSettingsMenu(RecyclerView recyclerView) {
        if (!BaseSettings.HIDE_SETTINGS_MENU.get())
            return;

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