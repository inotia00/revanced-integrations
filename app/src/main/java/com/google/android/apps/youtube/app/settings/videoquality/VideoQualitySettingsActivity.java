package com.google.android.apps.youtube.app.settings.videoquality;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toolbar;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.ResourceUtils;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.patches.utils.DrawableColorPatch;
import app.revanced.integrations.youtube.settings.preference.ReVancedPreferenceFragment;
import app.revanced.integrations.youtube.utils.ThemeUtils;

import java.lang.ref.WeakReference;
import java.util.Objects;

import static app.revanced.integrations.shared.utils.StringRef.str;

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

            // region compose search hint

            String revancedSettingsName = str("revanced_extended_settings_title");
            String searchHint = str("revanced_extended_settings_search_title");

            // if the translation is missing the %s, then it
            // will use the default search hint for that language
            String finalSearchHint = String.format(searchHint, revancedSettingsName);

            searchView.setQueryHint(finalSearchHint);

            // endregion

            // region SearchView dimensions

            // Get the current layout parameters of the SearchView
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) searchView.getLayoutParams();

            // Set the margins (in pixels)
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()); // for example, 10dp
            layoutParams.setMargins(margin, layoutParams.topMargin, margin, layoutParams.bottomMargin);

            // Apply the layout parameters to the SearchView
            searchView.setLayoutParams(layoutParams);

            // endregion

            // region SearchView color

            GradientDrawable shape = new GradientDrawable();

            String currentDarkHex = DrawableColorPatch.getDarkBackgroundHexValue();
            String currentLightHex = DrawableColorPatch.getLightBackgroundHexValue();

            String currentHex = ThemeUtils.isDarkTheme() ? currentDarkHex : currentLightHex;
            String defaultHex = ThemeUtils.isDarkTheme() ? "#1A1A1A" : "#E5E5E5";

            String finalHex;
            if (currentHex.equals(ThemeUtils.isDarkTheme() ? "#000000" : "#FFFFFF")) {
                shape.setColor(Color.parseColor(defaultHex)); // stock black/white color
                finalHex = defaultHex;
            } else {
                // custom color theme
                String adjustedColor = ThemeUtils.isDarkTheme()
                        ? ThemeUtils.lightenColor(currentHex, 15)
                        : ThemeUtils.darkenColor(currentHex, 15);
                shape.setColor(Color.parseColor(adjustedColor));
                finalHex = adjustedColor;
            }
            Logger.printInfo(() -> "searchbar color: " + finalHex);

            shape.setCornerRadius(30 * getResources().getDisplayMetrics().density);
            searchView.setBackground(shape);

            /* TODO:
                in order to match the original app's search bar, we'd need to change the searchbar cursor color
                to white (#FFFFFF) if ThemeUtils.isDarkTheme(), and black (#000000) if not.
                Currently it's always blue.
             */

            // endregion

            int leftPaddingDp = 5;
            int topPaddingDp = 5;
            int rightPaddingDp = 5;
            int bottomPaddingDp = 5;

            int leftPaddingPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, leftPaddingDp, getResources().getDisplayMetrics());
            int topPaddingPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, topPaddingDp, getResources().getDisplayMetrics());
            int rightPaddingPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, rightPaddingDp, getResources().getDisplayMetrics());
            int bottomPaddingPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, bottomPaddingDp, getResources().getDisplayMetrics());

            searchView.setPadding(leftPaddingPx, topPaddingPx, rightPaddingPx, bottomPaddingPx);

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
