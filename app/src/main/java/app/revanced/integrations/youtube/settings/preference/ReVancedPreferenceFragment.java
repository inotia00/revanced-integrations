package app.revanced.integrations.youtube.settings.preference;

import static com.google.android.apps.youtube.app.settings.videoquality.VideoQualitySettingsActivity.setSearchViewVisibility;
import static com.google.android.apps.youtube.app.settings.videoquality.VideoQualitySettingsActivity.setToolbarText;
import static app.revanced.integrations.shared.settings.preference.AbstractPreferenceFragment.showRestartDialog;
import static app.revanced.integrations.shared.settings.preference.AbstractPreferenceFragment.updateListPreferenceSummary;
import static app.revanced.integrations.shared.utils.ResourceUtils.getIdIdentifier;
import static app.revanced.integrations.shared.utils.ResourceUtils.getXmlIdentifier;
import static app.revanced.integrations.shared.utils.StringRef.str;
import static app.revanced.integrations.shared.utils.Utils.getChildView;
import static app.revanced.integrations.shared.utils.Utils.showToastShort;
import static app.revanced.integrations.youtube.settings.Settings.DEFAULT_PLAYBACK_SPEED;
import static app.revanced.integrations.youtube.settings.Settings.HIDE_PREVIEW_COMMENT;
import static app.revanced.integrations.youtube.settings.Settings.HIDE_PREVIEW_COMMENT_TYPE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import app.revanced.integrations.shared.settings.BooleanSetting;
import app.revanced.integrations.shared.settings.Setting;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.video.CustomPlaybackSpeedPatch;
import app.revanced.integrations.youtube.utils.ExtendedUtils;
import app.revanced.integrations.youtube.utils.ThemeUtils;

@SuppressWarnings("deprecation")
public class ReVancedPreferenceFragment extends PreferenceFragment {
    private static final int READ_REQUEST_CODE = 42;
    private static final int WRITE_REQUEST_CODE = 43;
    static boolean settingImportInProgress = false;
    static boolean showingUserDialogMessage;

    @SuppressLint("SuspiciousIndentation")
    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, str) -> {
        try {
            Setting<?> setting = Setting.getSettingFromPath(str);

            if (setting == null) return;

            Preference mPreference = findPreference(str);

            if (mPreference == null) return;

            if (mPreference instanceof SwitchPreference switchPreference) {
                BooleanSetting boolSetting = (BooleanSetting) setting;
                if (settingImportInProgress) {
                    switchPreference.setChecked(boolSetting.get());
                } else {
                    BooleanSetting.privateSetValue(boolSetting, switchPreference.isChecked());
                }

                if (ExtendedUtils.anyMatchSetting(setting)) {
                    ExtendedUtils.setPlayerFlyoutMenuAdditionalSettings();
                } else if (setting.equals(HIDE_PREVIEW_COMMENT) || setting.equals(HIDE_PREVIEW_COMMENT_TYPE)) {
                    ExtendedUtils.setCommentPreviewSettings();
                }
            } else if (mPreference instanceof EditTextPreference editTextPreference) {
                if (settingImportInProgress) {
                    editTextPreference.setText(setting.get().toString());
                } else {
                    Setting.privateSetValueFromString(setting, editTextPreference.getText());
                }
            } else if (mPreference instanceof ListPreference listPreference) {
                if (settingImportInProgress) {
                    listPreference.setValue(setting.get().toString());
                } else {
                    Setting.privateSetValueFromString(setting, listPreference.getValue());
                }

                if (setting.equals(DEFAULT_PLAYBACK_SPEED)) {
                    listPreference.setEntries(CustomPlaybackSpeedPatch.getListEntries());
                    listPreference.setEntryValues(CustomPlaybackSpeedPatch.getListEntryValues());
                }
                updateListPreferenceSummary(listPreference, setting);
            } else {
                Logger.printException(() -> "Setting cannot be handled: " + mPreference.getClass() + " " + mPreference);
                return;
            }

            final Activity mActivity = getActivity();
            ReVancedSettingsPreference.initializeReVancedSettings(mActivity);

            if (settingImportInProgress) {
                return;
            }

            if (!showingUserDialogMessage) {
                final Context context = getContext();

                if (setting.userDialogMessage != null
                        && mPreference instanceof SwitchPreference switchPreference
                        && setting.defaultValue instanceof Boolean defaultValue
                        && switchPreference.isChecked() != defaultValue) {
                    showSettingUserDialogConfirmation(context, switchPreference, (BooleanSetting) setting);
                } else if (setting.rebootApp) {
                    showRestartDialog(context);
                }
            }
        } catch (Exception ex) {
            Logger.printException(() -> "OnSharedPreferenceChangeListener failure", ex);
        }
    };

    private void showSettingUserDialogConfirmation(Context context, SwitchPreference switchPreference, BooleanSetting setting) {
        Utils.verifyOnMainThread();

        showingUserDialogMessage = true;
        assert setting.userDialogMessage != null;
        new AlertDialog.Builder(context)
                .setTitle(str("revanced_extended_confirm_user_dialog_title"))
                .setMessage(setting.userDialogMessage.toString())
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    if (setting.rebootApp) {
                        showRestartDialog(context);
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                    switchPreference.setChecked(setting.defaultValue); // Recursive call that resets the Setting value.
                })
                .setOnDismissListener(dialog -> showingUserDialogMessage = false)
                .setCancelable(false)
                .show();
    }

    static PreferenceManager mPreferenceManager;
    private SharedPreferences mSharedPreferences;

    private PreferenceScreen originalPreferenceScreen;

    public ReVancedPreferenceFragment() {
        // Required empty public constructor
    }

    @TargetApi(26)
    public void setPreferenceFragmentToolbar(final String key) {
        PreferenceFragment fragment;
        switch (key) {
            case "revanced_preference_screen_ryd" ->
                    fragment = new ReturnYouTubeDislikePreferenceFragment();
            case "revanced_preference_screen_sb" -> fragment = new SponsorBlockPreferenceFragment();
            default -> {
                Logger.printException(() -> "Unknown key: " + key);
                return;
            }
        }

        final Preference mPreference = mPreferenceManager.findPreference(key);
        if (mPreference == null) {
            return;
        }
        mPreference.setOnPreferenceClickListener(pref -> {
            // Set toolbar text
            setToolbarText(pref.getTitle());

            // Hide the search bar
            setSearchViewVisibility(false);

            getFragmentManager()
                    .beginTransaction()
                    .replace(getIdIdentifier("revanced_settings_fragments"), fragment)
                    .addToBackStack(null)
                    .setReorderingAllowed(true)
                    .commitAllowingStateLoss();
            return false;
        });
    }

    private void putPreferenceScreenMap(SortedMap<String, PreferenceScreen> preferenceScreenMap, PreferenceGroup preferenceGroup) {
        if (preferenceGroup instanceof PreferenceScreen mPreferenceScreen) {
            preferenceScreenMap.put(mPreferenceScreen.getKey(), mPreferenceScreen);
        }
    }

    private void setPreferenceScreenToolbar() {
        SortedMap<String, PreferenceScreen> preferenceScreenMap = new TreeMap<>();

        PreferenceScreen rootPreferenceScreen = getPreferenceScreen();
        for (Preference preference : getAllPreferencesBy(rootPreferenceScreen)) {
            if (!(preference instanceof PreferenceGroup preferenceGroup)) continue;
            putPreferenceScreenMap(preferenceScreenMap, preferenceGroup);
            for (Preference childPreference : getAllPreferencesBy(preferenceGroup)) {
                if (!(childPreference instanceof PreferenceGroup nestedPreferenceGroup)) continue;
                putPreferenceScreenMap(preferenceScreenMap, nestedPreferenceGroup);
                for (Preference nestedPreference : getAllPreferencesBy(nestedPreferenceGroup)) {
                    if (!(nestedPreference instanceof PreferenceGroup childPreferenceGroup))
                        continue;
                    putPreferenceScreenMap(preferenceScreenMap, childPreferenceGroup);
                }
            }
        }

        for (PreferenceScreen mPreferenceScreen : preferenceScreenMap.values()) {
            mPreferenceScreen.setOnPreferenceClickListener(
                    preferenceScreen -> {
                        Dialog preferenceScreenDialog = mPreferenceScreen.getDialog();
                        ViewGroup rootView = (ViewGroup) preferenceScreenDialog
                                .findViewById(android.R.id.content)
                                .getParent();

                        Toolbar toolbar = new Toolbar(preferenceScreen.getContext());

                        toolbar.setTitle(preferenceScreen.getTitle());
                        toolbar.setNavigationIcon(ThemeUtils.getBackButtonDrawable());
                        toolbar.setNavigationOnClickListener(view -> preferenceScreenDialog.dismiss());

                        int margin = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()
                        );

                        toolbar.setTitleMargin(margin, 0, margin, 0);

                        TextView toolbarTextView = getChildView(toolbar, TextView.class::isInstance);
                        if (toolbarTextView != null) {
                            toolbarTextView.setTextColor(ThemeUtils.getTextColor());
                        }

                        rootView.addView(toolbar, 0);
                        return false;
                    }
            );
        }
    }

    // TODO: SEARCH BAR
    //  0. Add ability to search for SB and RYD settings
    //  1. Structure settings
    //   1.1. Add each parent PreferenceScreen as PreferenceCategory while searching.
    //   or
    //   1.2. Clarify prefs descriptions, because there are duplicates
    //        (e.g. "Hide cast button" in "General" and "Player buttons").
    //  2. Make PreferenceCategory in search clickable to open according PreferenceScreen.
    //  3. Make search bar look more like YouTube search.

    // List to store all preferences
    private final List<Preference> allPreferences = new ArrayList<>();
    // Map to store dependencies: key is the preference key, value is a list of dependent preferences
    private final Map<String, List<Preference>> dependencyMap = new HashMap<>();
    // Set to track already added preferences to avoid duplicates
    private final Set<String> addedPreferences = new HashSet<>();

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            mPreferenceManager = getPreferenceManager();
            mPreferenceManager.setSharedPreferencesName(Setting.preferences.name);
            mSharedPreferences = mPreferenceManager.getSharedPreferences();
            addPreferencesFromResource(getXmlIdentifier("revanced_prefs"));

            // Initialize toolbars and other UI elements
            setPreferenceFragmentToolbar("revanced_preference_screen_ryd");
            setPreferenceFragmentToolbar("revanced_preference_screen_sb");
            setPreferenceScreenToolbar();

            // Initialize ReVanced settings
            ReVancedSettingsPreference.initializeReVancedSettings(getActivity());

            // Import/export
            setBackupRestorePreference();

            // Store all preferences and their dependencies
            storeAllPreferences(getPreferenceScreen());

            // Load and set initial preferences states
            for (Setting<?> setting : Setting.allLoadedSettings()) {
                final Preference preference = mPreferenceManager.findPreference(setting.key);

                if (preference instanceof SwitchPreference switchPreference) {
                    BooleanSetting boolSetting = (BooleanSetting) setting;
                    switchPreference.setChecked(boolSetting.get());
                } else if (preference instanceof EditTextPreference editTextPreference) {
                    editTextPreference.setText(setting.get().toString());
                } else if (preference instanceof ListPreference listPreference) {
                    if (setting.equals(DEFAULT_PLAYBACK_SPEED)) {
                        listPreference.setEntries(CustomPlaybackSpeedPatch.getListEntries());
                        listPreference.setEntryValues(CustomPlaybackSpeedPatch.getListEntryValues());
                    }
                    updateListPreferenceSummary(listPreference, setting);
                }
            }

            // Register preference change listener
            mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);

            originalPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
            copyPreferences(getPreferenceScreen(), originalPreferenceScreen);
        } catch (Exception th) {
            Logger.printException(() -> "Error during onCreate()", th);
        }
    }

    private void copyPreferences(PreferenceScreen source, PreferenceScreen destination) {
        for (Preference preference : getAllPreferencesBy(source)) {
            destination.addPreference(preference);
        }
    }

    @Override
    public void onDestroy() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        super.onDestroy();
    }

    /**
     * Recursively stores all preferences and their dependencies.
     *
     * @param preferenceGroup The preference group to scan.
     */
    private void storeAllPreferences(PreferenceGroup preferenceGroup) {
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            allPreferences.add(preference);
            Logger.printDebug(() -> "SearchFragment: Stored preference with key: " + preference.getKey());

            // Store dependencies
            if (preference.getDependency() != null) {
                String dependencyKey = preference.getDependency();
                dependencyMap.computeIfAbsent(dependencyKey, k -> new ArrayList<>()).add(preference);
                Logger.printDebug(() -> "SearchFragment: Added dependency for key: " + dependencyKey + " on preference: " + preference.getKey());
            }

            if (preference instanceof PreferenceGroup preferenceGroup1) {
                storeAllPreferences(preferenceGroup1);
            }
        }
    }

    /**
     * Filters preferences based on the search query.
     * <p>
     * This method searches within the preference's title, summary, entries, and values.
     *
     * @param query The search query.
     */
    public void filterPreferences(String query) {
        // If the query is null or empty, reset preferences to their default state
        if (query == null || query.isEmpty()) {
            Logger.printDebug(() -> "SearchFragment: Query is null or empty. Resetting preferences.");
            resetPreferences();
            return;
        }

        // Convert the query to lowercase for case-insensitive search
        query = query.toLowerCase();
        // String finalQuery = query;
        // Logger.printDebug(() -> "SearchFragment: Query after conversion to lowercase: " + finalQuery);

        // Get the preference screen to modify
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        // Remove all current preferences from the screen
        preferenceScreen.removeAll();
        // Clear the list of added preferences to start fresh
        addedPreferences.clear();

        // Loop through all available preferences
        for (Preference preference : allPreferences) {
            // Check if the title contains the query string
            boolean matches = preference.getTitle().toString().toLowerCase().contains(query);

            // Debugging title match
            if (matches) {
                Logger.printDebug(() -> "SearchFragment: Title matched: " + preference.getTitle());
            }

            // Check if the summary contains the query string
            CharSequence summary = preference.getSummary();
            if (!matches && summary != null && summary.toString().toLowerCase().contains(query)) {
                matches = true;
                Logger.printDebug(() -> "SearchFragment: Summary matched: " + summary);
            }

            // Additional check for SwitchPreference with summaryOn and summaryOff
            if (!matches && preference instanceof SwitchPreference switchPreference) {
                CharSequence summaryOn = switchPreference.getSummaryOn();
                CharSequence summaryOff = switchPreference.getSummaryOff();

                if (summaryOn != null && summaryOn.toString().toLowerCase().contains(query)) {
                    matches = true;
                    Logger.printDebug(() -> "SearchFragment: SummaryOn matched: " + summaryOn);
                }

                if (summaryOff != null && summaryOff.toString().toLowerCase().contains(query)) {
                    matches = true;
                    Logger.printDebug(() -> "SearchFragment: SummaryOff matched: " + summaryOff);
                }
            }

            // Check if the entries or values contain the query string (for ListPreference)
            if (!matches && preference instanceof ListPreference listPreference) {
                // Check entries
                CharSequence[] entries = listPreference.getEntries();
                if (entries != null) {
                    for (CharSequence entry : entries) {
                        if (entry.toString().toLowerCase().contains(query)) {
                            matches = true;
                            Logger.printDebug(() -> "SearchFragment: Entry matched: " + entry);
                            break;
                        }
                    }
                }

                // Check entry values
                if (!matches) {
                    CharSequence[] entryValues = listPreference.getEntryValues();
                    if (entryValues != null) {
                        for (CharSequence entryValue : entryValues) {
                            if (entryValue.toString().toLowerCase().contains(query)) {
                                matches = true;
                                Logger.printDebug(() -> "SearchFragment: EntryValue matched: " + entryValue);
                                break;
                            }
                        }
                    }
                }
            }

            // If the preference matches the query, add it to the preference screen
            if (matches) {
                Logger.printDebug(() -> "SearchFragment: Adding preference with title: " + preference.getTitle());
                addPreferenceWithDependencies(preferenceScreen, preference);
            }
        }
    }

    /**
     * Recursively adds a preference along with its dependencies
     * (android:dependency attibute in XML).
     *
     * @param preferenceGroup The preference group to add to.
     * @param preference      The preference to add.
     */
    private void addPreferenceWithDependencies(PreferenceGroup preferenceGroup, Preference preference) {
        String key = preference.getKey();
        if (key != null && !addedPreferences.contains(key)) {
            // Add dependencies first
            if (preference.getDependency() != null) {
                String dependencyKey = preference.getDependency();
                Logger.printDebug(() -> "SearchFragment: Adding preference dependency for key: " + dependencyKey);
                Preference dependency = mPreferenceManager.findPreference(dependencyKey);
                if (dependency != null) {
                    addPreferenceWithDependencies(preferenceGroup, dependency);
                } else {
                    Logger.printDebug(() -> "SearchFragment: Dependency not found for key: " + dependencyKey);
                    // Skip adding this preference as its dependency is not found
                    return;
                }
            }

            preferenceGroup.addPreference(preference);
            addedPreferences.add(key);
            Logger.printDebug(() -> "SearchFragment: Added preference with key: " + key);

            // Add dependent preferences
            if (dependencyMap.containsKey(key)) {
                Logger.printDebug(() -> "SearchFragment: Adding dependent preferences for key: " + key);
                for (Preference dependentPreference : Objects.requireNonNull(dependencyMap.get(key))) {
                    addPreferenceWithDependencies(preferenceGroup, dependentPreference);
                }
            }
        }
    }

    /**
     * Resets the preference screen to its original state.
     */
    private void resetPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        for (Preference preference : getAllPreferencesBy(originalPreferenceScreen))
            preferenceScreen.addPreference(preference);

        Logger.printDebug(() -> "SearchFragment: Reset preferences completed.");
    }

    private List<Preference> getAllPreferencesBy(PreferenceGroup preferenceGroup) {
        List<Preference> preferences = new ArrayList<>();
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++)
            preferences.add(preferenceGroup.getPreference(i));
        return preferences;
    }

    /**
     * Add Preference to Import/Export settings submenu
     */
    private void setBackupRestorePreference() {
        findPreference("revanced_extended_settings_import").setOnPreferenceClickListener(pref -> {
            importActivity();
            return false;
        });

        findPreference("revanced_extended_settings_export").setOnPreferenceClickListener(pref -> {
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

        var appName = ExtendedUtils.getApplicationLabel();
        var versionName = ExtendedUtils.getVersionName();
        var formatDate = dateFormat.format(new Date(System.currentTimeMillis()));
        var fileName = String.format("%s_v%s_%s.txt", appName, versionName, formatDate);

        var intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    /**
     * Invoke the SAF(Storage Access Framework) to import settings
     */
    private void importActivity() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(Build.VERSION.SDK_INT <= 28 ? "*/*" : "text/plain");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     * Activity should be done within the lifecycle of PreferenceFragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            exportText(data.getData());
        } else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            importText(data.getData());
        }
    }

    private void exportText(Uri uri) {
        final Context context = this.getContext();

        try {
            @SuppressLint("Recycle")
            FileWriter jsonFileWriter =
                    new FileWriter(
                            Objects.requireNonNull(context.getApplicationContext()
                                            .getContentResolver()
                                            .openFileDescriptor(uri, "w"))
                                    .getFileDescriptor()
                    );
            PrintWriter printWriter = new PrintWriter(jsonFileWriter);
            printWriter.write(Setting.exportToJson(context));
            printWriter.close();
            jsonFileWriter.close();

            showToastShort(str("revanced_extended_settings_export_success"));
        } catch (IOException e) {
            showToastShort(str("revanced_extended_settings_export_failed"));
        }
    }

    private void importText(Uri uri) {
        final Context context = this.getContext();
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            settingImportInProgress = true;

            @SuppressLint("Recycle")
            FileReader fileReader =
                    new FileReader(
                            Objects.requireNonNull(context.getApplicationContext()
                                            .getContentResolver()
                                            .openFileDescriptor(uri, "r"))
                                    .getFileDescriptor()
                    );
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            bufferedReader.close();
            fileReader.close();

            final boolean restartNeeded = Setting.importFromJSON(sb.toString(), true);
            if (restartNeeded) {
                showRestartDialog(getActivity());
            }
        } catch (IOException e) {
            showToastShort(str("revanced_extended_settings_import_failed"));
            throw new RuntimeException(e);
        } finally {
            settingImportInProgress = false;
        }
    }
}
