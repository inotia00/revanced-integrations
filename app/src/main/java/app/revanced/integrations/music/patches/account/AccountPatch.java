package app.revanced.integrations.music.patches.account;

import static app.revanced.integrations.shared.utils.StringRef.str;

import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Objects;

import app.revanced.integrations.music.settings.Settings;

@SuppressWarnings("unused")
public class AccountPatch {

    private static String[] accountMenuBlockList;

    static {
        accountMenuBlockList = Settings.HIDE_ACCOUNT_MENU_FILTER_STRINGS.get().split("\\n");
        // Some settings should not be hidden.
        accountMenuBlockList = Arrays.stream(accountMenuBlockList)
                .filter(item -> !Objects.equals(item, str("settings")))
                .toArray(String[]::new);
    }

    public static void hideAccountMenu(CharSequence charSequence, View view) {
        if (!Settings.HIDE_ACCOUNT_MENU.get())
            return;

        if (charSequence == null) {
            if (Settings.HIDE_ACCOUNT_MENU_EMPTY_COMPONENT.get())
                view.setVisibility(View.GONE);

            return;
        }

        for (String filter : accountMenuBlockList) {
            if (!filter.isEmpty() && charSequence.toString().equals(filter))
                view.setVisibility(View.GONE);
        }
    }

    public static boolean hideHandle(boolean original) {
        return Settings.HIDE_HANDLE.get() || original;
    }

    public static void hideHandle(TextView textView, int visibility) {
        final int finalVisibility = Settings.HIDE_HANDLE.get()
                ? View.GONE
                : visibility;
        textView.setVisibility(finalVisibility);
    }

    public static int hideTermsContainer() {
        return Settings.HIDE_TERMS_CONTAINER.get() ? View.GONE : View.VISIBLE;
    }
}
