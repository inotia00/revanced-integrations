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
public class ViewCountAboutPreference extends Preference {

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, str) -> {
        // Because this listener may run before the ReVanced settings fragment updates Settings,
        // this could show the prior config and not the current.
        //
        // Push this call to the end of the main run queue,
        // so all other listeners are done and Settings is up to date.
        Utils.runOnMainThread(this::updateUI);
    };

    public ViewCountAboutPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ViewCountAboutPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewCountAboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewCountAboutPreference(Context context) {
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
        boolean viewCountFilterEnabled = false;

        final BooleanSetting[] viewCountFilterSettings = {
                Settings.HIDE_VIDEO_BY_VIEW_COUNTS_HOME,
                Settings.HIDE_VIDEO_BY_VIEW_COUNTS_SEARCH,
                Settings.HIDE_VIDEO_BY_VIEW_COUNTS_SUBSCRIPTIONS
        };
        for (BooleanSetting s : viewCountFilterSettings) {
            viewCountFilterEnabled |= s.get();
        }
        setEnabled(viewCountFilterEnabled);
    }
}