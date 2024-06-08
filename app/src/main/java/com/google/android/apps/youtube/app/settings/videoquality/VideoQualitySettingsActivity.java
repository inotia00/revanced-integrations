package com.google.android.apps.youtube.app.settings.videoquality;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toolbar;

import java.lang.ref.WeakReference;
import java.util.Objects;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.ResourceUtils;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.preference.ReVancedPreferenceFragment;
import app.revanced.integrations.youtube.utils.ThemeUtils;

@SuppressWarnings("deprecation")
public class VideoQualitySettingsActivity extends Activity {

    private static final String rvxSettingsLabel = ResourceUtils.getString("revanced_extended_settings_title");
    private static WeakReference<SearchView> searchViewRef = new WeakReference<>(null);
    private static WeakReference<TextView> textViewRef = new WeakReference<>(null);
    private ReVancedPreferenceFragment fragment;

    private final OnQueryTextListener onQueryTextListener = new OnQueryTextListener() {
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
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Utils.getLocalizedContextAndSetResources(base));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            // Set fragment theme
            setTheme(ThemeUtils.getThemeId());

            // Set content
            setContentView(ResourceUtils.getLayoutIdentifier("revanced_settings_with_toolbar"));

            String dataString = Objects.requireNonNull(getIntent().getDataString());
            if (dataString.equals("revanced_extended_settings_intent")) {
                fragment = new ReVancedPreferenceFragment();
            } else {
                Logger.printException(() -> "Unknown setting: " + dataString);
                return;
            }

            // Set toolbar
            setToolbar();

            getFragmentManager()
                    .beginTransaction()
                    .replace(ResourceUtils.getIdIdentifier("revanced_settings_fragments"), fragment)
                    .commit();

            // Set search view
            SearchView searchView = findViewById(ResourceUtils.getIdIdentifier("search_view"));
            searchView.setOnQueryTextListener(onQueryTextListener);
            searchViewRef = new WeakReference<>(searchView);
        } catch (Exception ex) {
            Logger.printException(() -> "onCreate failure", ex);
        }
    }

    public static void setSearchViewVisibility(boolean visible) {
        final SearchView searchView = searchViewRef.get();
        if (searchView == null) return;
        searchView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void setToolbarText() {
        setToolbarText(rvxSettingsLabel);
    }

    public static void setToolbarText(CharSequence title) {
        final TextView toolbarTextView = textViewRef.get();
        if (toolbarTextView == null) return;
        toolbarTextView.setText(title);
    }

    private void filterPreferences(String query) {
        if (fragment == null) return;
        fragment.filterPreferences(query);
    }

    private void setToolbar() {
        if (!(findViewById(ResourceUtils.getIdIdentifier("revanced_toolbar_parent")) instanceof ViewGroup toolBarParent))
            return;
        Toolbar toolbar = new Toolbar(toolBarParent.getContext());
        toolbar.setBackgroundColor(ThemeUtils.getToolbarBackgroundColor());
        toolbar.setNavigationIcon(ThemeUtils.getBackButtonDrawable());
        toolbar.setNavigationOnClickListener(view -> VideoQualitySettingsActivity.this.onBackPressed());
        toolbar.setTitle(rvxSettingsLabel);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        toolbar.setTitleMargin(margin, 0, margin, 0);
        TextView toolbarTextView = Utils.getChildView(toolbar, view -> view instanceof TextView);
        textViewRef = new WeakReference<>(toolbarTextView);
        if (toolbarTextView != null) {
            toolbarTextView.setTextColor(ThemeUtils.getTextColor());
        }
        toolBarParent.addView(toolbar, 0);
    }
}
