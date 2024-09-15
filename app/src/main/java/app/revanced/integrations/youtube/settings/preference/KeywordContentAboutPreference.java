package app.revanced.integrations.youtube.settings.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import app.revanced.integrations.shared.settings.BooleanSetting;
import app.revanced.integrations.shared.settings.Setting;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings({"deprecation", "unused"})
public class KeywordContentAboutPreference extends Preference {

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, str) -> {
        // Because this listener may run before the ReVanced settings fragment updates Settings,
        // this could show the prior config and not the current.
        //
        // Push this call to the end of the main run queue,
        // so all other listeners are done and Settings is up to date.
        Utils.runOnMainThread(this::updateUI);
    };

    public KeywordContentAboutPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public KeywordContentAboutPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KeywordContentAboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeywordContentAboutPreference(Context context) {
        super(context);
    }

    private void addChangeListener() {
        Setting.preferences.preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private void removeChangeListener() {
        Setting.preferences.preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        updateUI();
        addChangeListener();
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        removeChangeListener();
    }

    private void updateUI() {
        boolean keywordContentEnabled = false;

        final BooleanSetting[] keywordContentSettings = {
                Settings.HIDE_KEYWORD_CONTENT_HOME,
                Settings.HIDE_KEYWORD_CONTENT_SEARCH,
                Settings.HIDE_KEYWORD_CONTENT_SUBSCRIPTIONS,
                Settings.HIDE_KEYWORD_CONTENT_COMMENTS
        };
        for (BooleanSetting s : keywordContentSettings) {
            keywordContentEnabled |= s.get();
        }
        setEnabled(keywordContentEnabled);
    }
}