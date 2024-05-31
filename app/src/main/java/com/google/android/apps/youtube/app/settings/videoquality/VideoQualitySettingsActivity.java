package com.google.android.apps.youtube.app.settings.videoquality;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toolbar;
import android.view.ViewGroup;
import android.util.TypedValue;
import android.widget.TextView;

import java.util.Objects;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.ResourceUtils;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.preference.ReVancedPreferenceFragment;
import app.revanced.integrations.youtube.utils.ThemeUtils;

/**
 * @noinspection ALL
 */
public class VideoQualitySettingsActivity extends Activity {

    private ReVancedPreferenceFragment settingsFragment;
    private SearchView searchView;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Utils.getLocalizedContextAndSetResources(base));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            setTheme(ThemeUtils.getThemeId());
            setContentView(ResourceUtils.getLayoutIdentifier("revanced_settings_with_toolbar"));

            String toolbarTitleResourceName;
            String dataString = Objects.requireNonNull(getIntent().getDataString());
            switch (dataString) {
                case "revanced_extended_settings_intent" -> {
                    settingsFragment = new ReVancedPreferenceFragment();
                    toolbarTitleResourceName = "revanced_extended_settings_title";
                }
                default -> {
                    Logger.printException(() -> "Unknown setting: " + dataString);
                    return;
                }
            }

            setToolbar(toolbarTitleResourceName);

            final int fragmentId = ResourceUtils.getIdIdentifier("revanced_settings_fragments");
            getFragmentManager()
                    .beginTransaction()
                    .replace(fragmentId, settingsFragment)
                    .commit();

            // Search view
            SearchView searchView = findViewById(ResourceUtils.getIdIdentifier("search_view"));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterPreferences(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterPreferences(newText);
                    return true;
                }
            });
        } catch (Exception ex) {
            Logger.printException(() -> "onCreate failure", ex);
        }
    }

    private void filterPreferences(String query) {
        if (settingsFragment != null) {
            settingsFragment.filterPreferences(query);
        }
    }

    private void setToolbar(String toolbarTitleResourceName) {
        final ViewGroup toolBarParent = Objects.requireNonNull(findViewById(ResourceUtils.getIdIdentifier("revanced_toolbar_parent")));
        Toolbar toolbar = new Toolbar(toolBarParent.getContext());
        toolbar.setBackgroundColor(ThemeUtils.getToolbarBackgroundColor());
        toolbar.setNavigationIcon(ThemeUtils.getBackButtonDrawable());
        toolbar.setNavigationOnClickListener(view -> VideoQualitySettingsActivity.this.onBackPressed());
        toolbar.setTitle(ResourceUtils.getString(toolbarTitleResourceName));
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        toolbar.setTitleMargin(margin, 0, margin, 0);
        TextView toolbarTextView = Utils.getChildView(toolbar, view -> view instanceof TextView);
        toolbarTextView.setTextColor(ThemeUtils.getTextColor());
        toolBarParent.addView(toolbar, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
