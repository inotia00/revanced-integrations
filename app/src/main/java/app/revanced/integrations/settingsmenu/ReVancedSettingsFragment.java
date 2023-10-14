package app.revanced.integrations.settingsmenu;

import static app.revanced.integrations.utils.ReVancedHelper.getStringArray;
import static app.revanced.integrations.utils.ReVancedHelper.isPackageEnabled;
import static app.revanced.integrations.utils.ReVancedUtils.runOnMainThreadDelayed;
import static app.revanced.integrations.utils.ReVancedUtils.showToastShort;
import static app.revanced.integrations.utils.ResourceUtils.identifier;
import static app.revanced.integrations.utils.SharedPrefHelper.SharedPrefNames.REVANCED;
import static app.revanced.integrations.utils.StringRef.str;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import app.revanced.integrations.BuildConfig;
import app.revanced.integrations.patches.button.AlwaysRepeat;
import app.revanced.integrations.patches.button.CopyVideoUrl;
import app.revanced.integrations.patches.button.CopyVideoUrlTimestamp;
import app.revanced.integrations.patches.button.ExternalDownload;
import app.revanced.integrations.patches.button.SpeedDialog;
import app.revanced.integrations.patches.video.CustomPlaybackSpeedPatch;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.utils.ReVancedHelper;
import app.revanced.integrations.utils.ResourceType;
import app.revanced.integrations.utils.SharedPrefHelper;

/**
 * @noinspection ALL
 */
public class ReVancedSettingsFragment extends PreferenceFragment {
    private final int READ_REQUEST_CODE = 42;
    private final int WRITE_REQUEST_CODE = 43;
    @SuppressLint("SuspiciousIndentation")
    SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, str) -> {
        for (SettingsEnum setting : SettingsEnum.values()) {
            if (!setting.path.equals(str)) continue;
            Preference pref = findPreference(str);
            if (pref == null)
                return;

            if (pref instanceof SwitchPreference) {
                SwitchPreference switchPref = (SwitchPreference) pref;
                SettingsEnum.setValue(setting, switchPref.isChecked());

                switch (setting) {
                    case OVERLAY_BUTTON_ALWAYS_REPEAT -> AlwaysRepeat.refreshVisibility();
                    case OVERLAY_BUTTON_COPY_VIDEO_URL -> CopyVideoUrl.refreshVisibility();
                    case OVERLAY_BUTTON_COPY_VIDEO_URL_TIMESTAMP ->
                            CopyVideoUrlTimestamp.refreshVisibility();
                    case OVERLAY_BUTTON_EXTERNAL_DOWNLOADER -> ExternalDownload.refreshVisibility();
                    case OVERLAY_BUTTON_SPEED_DIALOG -> SpeedDialog.refreshVisibility();
                }

            } else if (pref instanceof EditTextPreference) {
                EditTextPreference editPreference = (EditTextPreference) pref;
                SettingsEnum.setValue(setting, editPreference.getText());
            } else if (pref instanceof ListPreference) {
                switch (setting) {
                    case DEFAULT_PLAYBACK_SPEED -> setPlaybackSpeed();
                    case DEFAULT_VIDEO_QUALITY_WIFI -> setVideoQuality(true);
                    case DEFAULT_VIDEO_QUALITY_MOBILE -> setVideoQuality(false);
                    case SPOOF_APP_VERSION_TARGET -> setSpoofAppVersionTarget();
                }
            }

            enableDisablePreferences();

            if (setting.userDialogMessage != null)
                rebootDialogWarning(setting);
            else if (setting.rebootApp)
                rebootDialog();
        }
    };
    private PreferenceScreen externalDownloaderPreferenceScreen;

    public ReVancedSettingsFragment() {
    }

    public static void reboot(Activity activity) {
        Intent restartIntent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());

        activity.finishAffinity();
        activity.startActivity(restartIntent);
        Runtime.getRuntime().exit(0);
    }

    @SuppressLint({"ResourceType", "CommitPrefEdits"})
    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getPreferenceManager().setSharedPreferencesName(REVANCED.getName());
        try {
            addPreferencesFromResource(identifier("revanced_prefs", ResourceType.XML));
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

            enableDisablePreferences();

            externalDownloaderPreferenceScreen = (PreferenceScreen) getPreferenceScreen().findPreference("external_downloader");

            initializeReVancedSettings();
        } catch (Throwable th) {
            LogHelper.printException(ReVancedSettingsFragment.class, "Error during onCreate()", th);
        }
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
        super.onDestroy();
    }

    private void enableDisablePreferences() {
        for (SettingsEnum setting : SettingsEnum.values()) {
            Preference preference = findPreference(setting.path);
            if (preference != null)
                preference.setEnabled(setting.isAvailable());
        }
    }

    private void enableDisablePreferences(final boolean isAvailable, final SettingsEnum... unavailableEnum) {
        if (!isAvailable) return;
        for (SettingsEnum setting : unavailableEnum) {
            Preference preference = findPreference(setting.path);
            if (preference != null)
                preference.setEnabled(false);
        }
    }

    private void initializeReVancedSettings() {
        EnableHDRCodecPreferenceLinks();
        FullScreenPanelPreferenceLinks();
        LayoutOverrideLinks();
        NavigationPreferenceLinks();
        QuickActionsPreferenceLinks();
        SpeedOverlayPreferenceLinks();
        TabletLayoutLinks();
        setBackupRestorePreference();
        setExternalDownloaderPreference();
        setOpenSettingsPreference();
        setPatchesInformation();
        setPlaybackSpeed();
        setSpoofAppVersionTarget();
        setVideoQuality(false);
        setVideoQuality(true);
    }

    /**
     * Enable/Disable Layout Override Preference
     */
    public void LayoutOverrideLinks() {
        enableDisablePreferences(
                ReVancedHelper.isTablet,
                SettingsEnum.ENABLE_TABLET_LAYOUT
        );
        enableDisablePreferences(
                !ReVancedHelper.isTablet,
                SettingsEnum.ENABLE_PHONE_LAYOUT
        );
    }

    /**
     * Enable/Disable Preferences not working in tablet layout
     */
    public void TabletLayoutLinks() {
        final boolean isTabletDevice = ReVancedHelper.isTablet &&
                !SettingsEnum.ENABLE_PHONE_LAYOUT.getBoolean();
        final boolean isEnabledTabletLayout = SettingsEnum.ENABLE_TABLET_LAYOUT.getBoolean();

        final boolean isTablet = isTabletDevice || isEnabledTabletLayout;

        enableDisablePreferences(
                isTablet,
                SettingsEnum.HIDE_CHANNEL_LIST_SUBMENU,
                SettingsEnum.HIDE_COMMUNITY_POSTS_HOME,
                SettingsEnum.HIDE_COMMUNITY_POSTS_SUBSCRIPTIONS,
                SettingsEnum.HIDE_END_SCREEN_OVERLAY,
                SettingsEnum.HIDE_FULLSCREEN_PANELS,
                SettingsEnum.HIDE_MIX_PLAYLISTS,
                SettingsEnum.HIDE_QUICK_ACTIONS,
                SettingsEnum.HIDE_QUICK_ACTIONS_COMMENT_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_DISLIKE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_LIKE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_LIVE_CHAT_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_MORE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_PLAYLIST_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_RELATED_VIDEO,
                SettingsEnum.HIDE_QUICK_ACTIONS_SHARE_BUTTON,
                SettingsEnum.SHOW_FULLSCREEN_TITLE
        );
    }

    /**
     * Enable/Disable Preference related to Enable HDR Codec
     */
    public void EnableHDRCodecPreferenceLinks() {
        enableDisablePreferences(
                SettingsEnum.ENABLE_HDR_CODEC.getBoolean(),
                SettingsEnum.ENABLE_VP9_CODEC,
                SettingsEnum.DISABLE_HDR_VIDEO
        );
    }

    /**
     * Enable/Disable Preference related to Hide Fullscreen Panel
     */
    public void FullScreenPanelPreferenceLinks() {
        enableDisablePreferences(
                SettingsEnum.HIDE_FULLSCREEN_PANELS.getBoolean(),
                SettingsEnum.HIDE_END_SCREEN_OVERLAY,
                SettingsEnum.HIDE_QUICK_ACTIONS,
                SettingsEnum.HIDE_QUICK_ACTIONS_COMMENT_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_DISLIKE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_LIKE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_LIVE_CHAT_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_MORE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_PLAYLIST_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_RELATED_VIDEO,
                SettingsEnum.HIDE_QUICK_ACTIONS_SHARE_BUTTON
        );
    }

    /**
     * Enable/Disable Preference related to Speed Overlay
     */
    public void SpeedOverlayPreferenceLinks() {
        enableDisablePreferences(
                SettingsEnum.DISABLE_SPEED_OVERLAY.getBoolean(),
                SettingsEnum.EDIT_SPEED_OVERLAY_VALUE
        );
    }

    /**
     * Enable/Disable Preference related to Hide Quick Actions
     */
    public void QuickActionsPreferenceLinks() {
        final boolean isEnabled = SettingsEnum.HIDE_FULLSCREEN_PANELS.getBoolean() || SettingsEnum.HIDE_QUICK_ACTIONS.getBoolean();

        enableDisablePreferences(
                isEnabled,
                SettingsEnum.HIDE_QUICK_ACTIONS_COMMENT_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_DISLIKE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_LIKE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_LIVE_CHAT_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_MORE_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_PLAYLIST_BUTTON,
                SettingsEnum.HIDE_QUICK_ACTIONS_RELATED_VIDEO,
                SettingsEnum.HIDE_QUICK_ACTIONS_SHARE_BUTTON
        );
    }

    /**
     * Enable/Disable Preference related to Navigation settings
     */
    public void NavigationPreferenceLinks() {
        enableDisablePreferences(
                SettingsEnum.SWITCH_CREATE_NOTIFICATION.getBoolean(),
                SettingsEnum.HIDE_CREATE_BUTTON
        );
    }

    /**
     * Set interaction for default video speed ListPreference
     */
    private void setPlaybackSpeed() {
        try {
            SettingsEnum speedSetting = SettingsEnum.DEFAULT_PLAYBACK_SPEED;

            var value = SharedPrefHelper.getString(REVANCED, speedSetting.path, "-2.0");
            speedSetting.saveValue(Float.valueOf(value));

            ListPreference speedListPreference = (ListPreference) findPreference(speedSetting.path);

            if (speedListPreference == null)
                return;

            speedListPreference.setEntries(CustomPlaybackSpeedPatch.getListEntries());
            speedListPreference.setEntryValues(CustomPlaybackSpeedPatch.getListEntryValues());

            CharSequence[] entries = speedListPreference.getEntries();
            int entryIndex = speedListPreference.findIndexOfValue(value);
            speedListPreference.setSummary(entryIndex < 0 ? null : entries[entryIndex]);
        } catch (Throwable th) {
            LogHelper.printException(ReVancedSettingsFragment.class, "Error setting setPlaybackSpeed" + th);
        }
    }

    /**
     * Set interaction for default video quality ListPreference
     */
    private void setVideoQuality(boolean isQualityWiFi) {
        try {
            SettingsEnum qualitySetting = isQualityWiFi ? SettingsEnum.DEFAULT_VIDEO_QUALITY_WIFI : SettingsEnum.DEFAULT_VIDEO_QUALITY_MOBILE;

            var value = SharedPrefHelper.getString(REVANCED, qualitySetting.path, "-2");
            qualitySetting.saveValue(Integer.parseInt(value));

            ListPreference qualityListPreference = (ListPreference) findPreference(qualitySetting.path);

            if (qualityListPreference == null)
                return;

            CharSequence[] entries = qualityListPreference.getEntries();
            int entryIndex = qualityListPreference.findIndexOfValue(value);
            qualityListPreference.setSummary(entryIndex < 0 ? null : entries[entryIndex]);
        } catch (Throwable th) {
            LogHelper.printException(ReVancedSettingsFragment.class, "Error setting setVideoQuality" + th);
        }
    }

    /**
     * Set interaction for spoof app version ListPreference
     */
    private void setSpoofAppVersionTarget() {
        try {
            SettingsEnum settingsEnum = SettingsEnum.SPOOF_APP_VERSION_TARGET;

            var value = SharedPrefHelper.getString(REVANCED, settingsEnum.path, settingsEnum.defaultValue.toString());
            settingsEnum.saveValue(value);

            Preference preference = findPreference(settingsEnum.path);

            if (preference == null)
                return;

            if (!(preference instanceof ListPreference listPreference))
                return;

            CharSequence[] entries = listPreference.getEntries();
            int entryIndex = listPreference.findIndexOfValue(value);
            listPreference.setSummary(entryIndex < 0 ? str("pref_offline_smart_downloads_custom_storage_title") : entries[entryIndex]);
        } catch (Throwable th) {
            LogHelper.printException(ReVancedSettingsFragment.class, "Error setting setSpoofAppVersionTarget" + th);
        }
    }

    /**
     * Add Preference for github support
     * Also setSummary integrations version (BuildConfig.VERSION_NAME)
     */
    private void setPatchesInformation() {
        Preference integrations = findPreference("revanced-integrations");
        if (integrations != null)
            integrations.setSummary(BuildConfig.VERSION_NAME);
    }

    /**
     * Add Preference to External downloader settings submenu
     */
    private void setExternalDownloaderPreference() {
        try {
            final var EXTERNAL_DOWNLOADER_LABEL_PREFERENCE_KEY = "revanced_external_downloader_label";
            final var EXTERNAL_DOWNLOADER_PACKAGE_NAME_PREFERENCE_KEY = "revanced_external_downloader_package_name";
            final var EXTERNAL_DOWNLOADER_WEBSITE_PREFERENCE_KEY = "revanced_external_downloader_website";

            Activity activity = getActivity();

            String[] labelArray = getStringArray(activity, EXTERNAL_DOWNLOADER_LABEL_PREFERENCE_KEY);
            String[] packageNameArray = getStringArray(activity, EXTERNAL_DOWNLOADER_PACKAGE_NAME_PREFERENCE_KEY);
            String[] websiteArray = getStringArray(activity, EXTERNAL_DOWNLOADER_WEBSITE_PREFERENCE_KEY);

            for (int index = 0; index < labelArray.length; index++) {
                final int finalIndex = index;
                final String label = labelArray[finalIndex].toString();
                final String packageName = packageNameArray[finalIndex].toString();
                final Uri uri = Uri.parse(websiteArray[finalIndex]);
                final boolean isInstalled = isPackageEnabled(activity, packageName);

                final var msg = isInstalled
                        ? str("revanced_external_downloader_installed")
                        : str("revanced_external_downloader_not_installed");

                Preference externalDownloaderPreference = new Preference(activity);

                externalDownloaderPreference.setTitle(label);
                externalDownloaderPreference.setSummary(packageName);
                externalDownloaderPreference.setOnPreferenceClickListener(preference -> {
                    new AlertDialog.Builder(activity)
                            .setTitle(label)
                            .setMessage(msg)
                            .setNegativeButton(str("playback_control_close"), null)
                            .setNeutralButton(str("common_google_play_services_install_button"), (dialog, id) -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                activity.startActivity(intent);
                            })
                            .setPositiveButton(str("save_metadata_menu"), (dialog, id) -> {
                                SettingsEnum.EXTERNAL_DOWNLOADER_PACKAGE_NAME.saveValue(packageName);
                            })
                            .show();

                    return false;
                });
                externalDownloaderPreferenceScreen.addPreference(externalDownloaderPreference);
            }

            Preference experimentalPreference = new Preference(activity);
            experimentalPreference.setTitle(" ");
            experimentalPreference.setSummary(str("revanced_experimental_flag"));

            SwitchPreference hookDownloadButtonPreference = new SwitchPreference(activity);
            hookDownloadButtonPreference.setTitle(str("revanced_hook_download_button_title"));
            hookDownloadButtonPreference.setSummary(str("revanced_hook_download_button_summary"));
            hookDownloadButtonPreference.setKey("revanced_hook_download_button");
            hookDownloadButtonPreference.setDefaultValue(false);

            externalDownloaderPreferenceScreen.addPreference(experimentalPreference);
            externalDownloaderPreferenceScreen.addPreference(hookDownloadButtonPreference);
        } catch (Throwable th) {
            LogHelper.printException(ReVancedSettingsFragment.class, "Error setting setExternalDownloaderPreference" + th);
        }
    }

    /**
     * Set Open External Link Preference onClickListener
     */
    private void setOpenSettingsPreference() {
        Preference preference = findPreference("revanced_default_app_settings");
        if (preference == null)
            return;

        Activity activity = getActivity();
        if (activity == null)
            return;

        var uri = Uri.parse("package:" + activity.getPackageName());

        try {
            preference.setOnPreferenceClickListener(pref -> {
                Intent intent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        ? new Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS, uri)
                        : new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);

                activity.startActivity(intent);
                return false;
            });
        } catch (Throwable th) {
            LogHelper.printException(ReVancedSettingsFragment.class, "Error setting setOpenSettingsPreference" + th);
        }
    }

    /**
     * Add Preference to Import/Export settings submenu
     */
    private void setBackupRestorePreference() {
        Preference importPreference = findPreference("revanced_import_settings");
        Preference exportPreference = findPreference("revanced_export_settings");
        if (importPreference == null || exportPreference == null)
            return;

        importPreference.setOnPreferenceClickListener(pref -> {
            importActivity();
            return false;
        });

        exportPreference.setOnPreferenceClickListener(pref -> {
            exportActivity();
            return false;
        });
    }

    /**
     * Invoke the SAF(Storage Access Framework) to export settings
     */
    private void exportActivity() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Activity activity = getActivity();

        var appName = ReVancedHelper.applicationLabel;
        var versionName = ReVancedHelper.versionName;
        var formatDate = dateFormat.format(new Date(System.currentTimeMillis()));
        var fileName = String.format("%s_v%s_%s.json", appName, versionName, formatDate);

        var intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    /**
     * Invoke the SAF(Storage Access Framework) to import settings
     */
    private void importActivity() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(Build.VERSION.SDK_INT <= 28 ? "*/*" : "application/json");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     * Activity should be done within the lifecycle of PreferenceFragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            exportJson(data.getData());
        } else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            importJson(data.getData());
        }
    }

    /**
     * TODO: Implemented as a more ideal serialize method
     */
    private void exportJson(Uri uri) {
        Context context = this.getContext();
        SharedPreferences prefs = context.getSharedPreferences(REVANCED.getName(), Context.MODE_PRIVATE);

        try {
            @SuppressLint("Recycle")
            FileWriter fileWriter = new FileWriter(
                    context.getApplicationContext()
                            .getContentResolver()
                            .openFileDescriptor(uri, "w")
                            .getFileDescriptor()
            );
            PrintWriter printWriter = new PrintWriter(fileWriter);
            JSONObject settingsJson = new JSONObject();

            var prefsMap = prefs.getAll();
            for (Map.Entry entry : prefsMap.entrySet()) {
                settingsJson.put(entry.getKey().toString(), entry.getValue());
            }
            printWriter.write(settingsJson.toString());
            printWriter.close();
            fileWriter.close();

            showToastShort(context, str("settings_export_successful"));
        } catch (IOException | JSONException e) {
            showToastShort(context, str("settings_export_failed"));
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO: Implemented as a more ideal serialize method
     */
    private void importJson(Uri uri) {
        Context context = this.getContext();
        SharedPreferences prefs = context.getSharedPreferences(REVANCED.getName(), Context.MODE_PRIVATE);
        String json;

        try {
            @SuppressLint("Recycle")
            FileReader fileReader = new FileReader(
                    context.getApplicationContext()
                            .getContentResolver()
                            .openFileDescriptor(uri, "r")
                            .getFileDescriptor()
            );
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            SharedPreferences.Editor editor = SharedPrefHelper.getPreferences(context, SharedPrefHelper.SharedPrefNames.REVANCED).edit();

            while ((json = bufferedReader.readLine()) != null) {
                JSONObject settingsJson = new JSONObject(json);

                var prefsMap = prefs.getAll();
                for (Map.Entry entry : prefsMap.entrySet()) {
                    String key = entry.getKey().toString().trim();
                    Object value = entry.getValue();

                    if (value instanceof Boolean) {
                        editor.putBoolean(key, settingsJson.optBoolean(key, (boolean) value));
                    } else {
                        editor.putString(key, settingsJson.optString(key, value.toString()));
                    }
                }
            }
            editor.apply();
            bufferedReader.close();
            fileReader.close();

            showToastShort(context, str("settings_import_successful"));
            runOnMainThreadDelayed(() -> reboot(getActivity()), 1000L);
        } catch (IOException | JSONException e) {
            showToastShort(context, str("settings_import_failed"));
            throw new RuntimeException(e);
        }
    }

    void rebootDialog() {
        Activity activity = getActivity();

        new AlertDialog.Builder(activity)
                .setMessage(str("pref_refresh_config"))
                .setPositiveButton(str("in_app_update_restart_button"), (dialog, id) -> reboot(activity))
                .setNegativeButton(str("sign_in_cancel"), null)
                .show();
    }

    void rebootDialogWarning(SettingsEnum setting) {
        if (setting.getBoolean()) {
            Activity activity = getActivity();

            new AlertDialog.Builder(activity)
                    .setMessage(setting.userDialogMessage.toString() + "\n\n" + str("revanced_reboot_warning_general"))
                    .setPositiveButton(str("in_app_update_restart_button"), (dialog, id) -> reboot(activity))
                    .setNegativeButton(str("offline_undo_snackbar_button_text"), (dialog, id) -> {
                        SwitchPreference switchPref = (SwitchPreference) findPreference(setting.path);
                        switchPref.setChecked(false);
                        setting.saveValue(false);
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            rebootDialog();
        }
    }
}
