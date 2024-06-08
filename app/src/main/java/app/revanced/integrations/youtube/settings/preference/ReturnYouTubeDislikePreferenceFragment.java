package app.revanced.integrations.youtube.settings.preference;

import static com.google.android.apps.youtube.app.settings.videoquality.VideoQualitySettingsActivity.setSearchViewVisibility;
import static com.google.android.apps.youtube.app.settings.videoquality.VideoQualitySettingsActivity.setToolbarText;
import static app.revanced.integrations.shared.utils.ResourceUtils.getLayoutIdentifier;
import static app.revanced.integrations.shared.utils.StringRef.str;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import app.revanced.integrations.shared.settings.Setting;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.patches.utils.ReturnYouTubeDislikePatch;
import app.revanced.integrations.youtube.returnyoutubedislike.ReturnYouTubeDislike;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("deprecation")
public class ReturnYouTubeDislikePreferenceFragment extends PreferenceFragment {

    /**
     * If dislikes are shown on Shorts.
     */
    private SwitchPreference shortsPreference;

    /**
     * If dislikes are shown as percentage.
     */
    private SwitchPreference percentagePreference;

    /**
     * If segmented like/dislike button uses smaller compact layout.
     */
    private SwitchPreference compactLayoutPreference;

    /**
     * If segmented like/dislike button uses smaller compact layout.
     */
    private SwitchPreference toastOnRYDNotAvailable;

    /**
     * Styles for preference category.
     */
    private final int preferencesCategoryLayout = getLayoutIdentifier("revanced_settings_preferences_category");

    private void updateUIState() {
        shortsPreference.setEnabled(Settings.RYD_SHORTS.isAvailable());
        percentagePreference.setEnabled(Settings.RYD_DISLIKE_PERCENTAGE.isAvailable());
        compactLayoutPreference.setEnabled(Settings.RYD_COMPACT_LAYOUT.isAvailable());
        toastOnRYDNotAvailable.setEnabled(Settings.RYD_TOAST_ON_CONNECTION_ERROR.isAvailable());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Activity context = getActivity();
            PreferenceManager manager = getPreferenceManager();
            manager.setSharedPreferencesName(Setting.preferences.name);
            PreferenceScreen preferenceScreen = manager.createPreferenceScreen(context);
            setPreferenceScreen(preferenceScreen);

            SwitchPreference enabledPreference = new SwitchPreference(context);
            enabledPreference.setChecked(Settings.RYD_ENABLED.get());
            enabledPreference.setTitle(str("revanced_ryd_enable_title"));
            enabledPreference.setSummaryOn(str("revanced_ryd_enable_summary_on"));
            enabledPreference.setSummaryOff(str("revanced_ryd_enable_summary_off"));
            enabledPreference.setOnPreferenceChangeListener((pref, newValue) -> {
                final Boolean rydIsEnabled = (Boolean) newValue;
                Settings.RYD_ENABLED.save(rydIsEnabled);
                ReturnYouTubeDislikePatch.onRYDStatusChange(rydIsEnabled);

                updateUIState();
                return true;
            });
            preferenceScreen.addPreference(enabledPreference);

            shortsPreference = new SwitchPreference(context);
            shortsPreference.setChecked(Settings.RYD_SHORTS.get());
            shortsPreference.setTitle(str("revanced_ryd_shorts_title"));
            String shortsSummary = ReturnYouTubeDislikePatch.IS_SPOOFING_TO_NON_LITHO_SHORTS_PLAYER
                    ? str("revanced_ryd_shorts_summary_on")
                    : str("revanced_ryd_shorts_summary_on_disclaimer");
            shortsPreference.setSummaryOn(shortsSummary);
            shortsPreference.setSummaryOff(str("revanced_ryd_shorts_summary_off"));
            shortsPreference.setOnPreferenceChangeListener((pref, newValue) -> {
                Settings.RYD_SHORTS.save((Boolean) newValue);
                updateUIState();
                return true;
            });
            preferenceScreen.addPreference(shortsPreference);

            percentagePreference = new SwitchPreference(context);
            percentagePreference.setChecked(Settings.RYD_DISLIKE_PERCENTAGE.get());
            percentagePreference.setTitle(str("revanced_ryd_dislike_percentage_title"));
            percentagePreference.setSummaryOn(str("revanced_ryd_dislike_percentage_summary_on"));
            percentagePreference.setSummaryOff(str("revanced_ryd_dislike_percentage_summary_off"));
            percentagePreference.setOnPreferenceChangeListener((pref, newValue) -> {
                Settings.RYD_DISLIKE_PERCENTAGE.save((Boolean) newValue);
                ReturnYouTubeDislike.clearAllUICaches();
                updateUIState();
                return true;
            });
            preferenceScreen.addPreference(percentagePreference);

            compactLayoutPreference = new SwitchPreference(context);
            compactLayoutPreference.setChecked(Settings.RYD_COMPACT_LAYOUT.get());
            compactLayoutPreference.setTitle(str("revanced_ryd_compact_layout_title"));
            compactLayoutPreference.setSummaryOn(str("revanced_ryd_compact_layout_summary_on"));
            compactLayoutPreference.setSummaryOff(str("revanced_ryd_compact_layout_summary_off"));
            compactLayoutPreference.setOnPreferenceChangeListener((pref, newValue) -> {
                Settings.RYD_COMPACT_LAYOUT.save((Boolean) newValue);
                ReturnYouTubeDislike.clearAllUICaches();
                updateUIState();
                return true;
            });
            preferenceScreen.addPreference(compactLayoutPreference);

            toastOnRYDNotAvailable = new SwitchPreference(context);
            toastOnRYDNotAvailable.setChecked(Settings.RYD_TOAST_ON_CONNECTION_ERROR.get());
            toastOnRYDNotAvailable.setTitle(str("revanced_ryd_toast_on_connection_error_title"));
            toastOnRYDNotAvailable.setSummaryOn(str("revanced_ryd_toast_on_connection_error_summary_on"));
            toastOnRYDNotAvailable.setSummaryOff(str("revanced_ryd_toast_on_connection_error_summary_off"));
            toastOnRYDNotAvailable.setOnPreferenceChangeListener((pref, newValue) -> {
                Settings.RYD_TOAST_ON_CONNECTION_ERROR.save((Boolean) newValue);
                updateUIState();
                return true;
            });
            preferenceScreen.addPreference(toastOnRYDNotAvailable);

            updateUIState();


            // About category

            PreferenceCategory aboutCategory = new PreferenceCategory(context);
            aboutCategory.setLayoutResource(preferencesCategoryLayout);
            aboutCategory.setTitle(str("revanced_ryd_about"));
            preferenceScreen.addPreference(aboutCategory);

            // ReturnYouTubeDislike Website

            Preference aboutWebsitePreference = new Preference(context);
            aboutWebsitePreference.setTitle(str("revanced_ryd_attribution_title"));
            aboutWebsitePreference.setSummary(str("revanced_ryd_attribution_summary"));
            aboutWebsitePreference.setOnPreferenceClickListener(pref -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://returnyoutubedislike.com"));
                pref.getContext().startActivity(i);
                return false;
            });
            aboutCategory.addPreference(aboutWebsitePreference);
        } catch (Exception ex) {
            Logger.printException(() -> "onCreate failure", ex);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Restore toolbar text
        setToolbarText();

        // Show the search bar
        setSearchViewVisibility(true);
    }
}
