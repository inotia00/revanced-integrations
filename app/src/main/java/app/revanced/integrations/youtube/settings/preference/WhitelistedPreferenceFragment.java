package app.revanced.integrations.youtube.settings.preference;

import static app.revanced.integrations.shared.utils.ResourceUtils.getIdIdentifier;
import static app.revanced.integrations.shared.utils.ResourceUtils.getLayoutIdentifier;
import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.getChildView;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.Objects;

import app.revanced.integrations.shared.settings.Setting;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.ResourceUtils;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.settingsmenu.WhitelistedChannelsDialogPreference;
import app.revanced.integrations.youtube.patches.utils.PatchStatus;
import app.revanced.integrations.youtube.whitelist.Whitelist;
import app.revanced.integrations.youtube.whitelist.WhitelistType;

@SuppressWarnings("deprecation")
public class WhitelistedPreferenceFragment extends PreferenceFragment {

    private final int preferencesCategoryLayout = getLayoutIdentifier("revanced_settings_preferences_category");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            boolean isIncludedSB = PatchStatus.SponsorBlock();
            boolean isIncludedSPEED = PatchStatus.PlaybackSpeed();

            if (isIncludedSB || isIncludedSPEED) {
                Activity context = getActivity();
                PreferenceManager manager = getPreferenceManager();
                manager.setSharedPreferencesName(Setting.preferences.name);
                PreferenceScreen preferenceScreen = manager.createPreferenceScreen(context);
                setPreferenceScreen(preferenceScreen);

                // Sponsorblock
                if (isIncludedSB) {
                    Whitelist.setEnabled(WhitelistType.SPONSORBLOCK, Settings.SB_WHITELIST.get());

                    WhitelistedChannelsDialogPreference WhitelistSB = new WhitelistedChannelsDialogPreference(context);
                    WhitelistSB.setTitle(str("revanced_whitelisting_sponsorblock"));
                    WhitelistSB.setWhitelistType(WhitelistType.SPONSORBLOCK);
                    preferenceScreen.addPreference(WhitelistSB);
                }

                // Video Speed
                if (isIncludedSPEED) {
                    Whitelist.setEnabled(WhitelistType.SPEED, Settings.SPEED_WHITELIST.get());

                    WhitelistedChannelsDialogPreference WhitelistSPEED = new WhitelistedChannelsDialogPreference(context);
                    WhitelistSPEED.setTitle(str("revanced_whitelisting_speed"));
                    WhitelistSPEED.setWhitelistType(WhitelistType.SPEED);
                    preferenceScreen.addPreference(WhitelistSPEED);
                }
            }
        } catch (Exception ex) {
            Logger.printException(() -> "onCreate failure", ex);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        final ViewGroup toolBarParent = Objects.requireNonNull(getActivity().findViewById(getIdIdentifier("revanced_toolbar_parent")));
        Toolbar toolbar = (Toolbar) toolBarParent.getChildAt(0);
        TextView toolbarTextView = Objects.requireNonNull(getChildView(toolbar, view -> view instanceof TextView));
        toolbarTextView.setText(ResourceUtils.getString("revanced_extended_settings_title"));
    }
}
