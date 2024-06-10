package app.revanced.integrations.shared.patches.components;

import static app.revanced.integrations.shared.patches.FullscreenAdsPatch.closeFullscreenAds;

import androidx.annotation.Nullable;

import app.revanced.integrations.shared.settings.BaseSettings;

@SuppressWarnings("unused")
public final class FullscreenAdsFilter extends Filter {

    public FullscreenAdsFilter() {
        addPathCallbacks(
                new StringFilterGroup(
                        BaseSettings.HIDE_FULLSCREEN_ADS,
                        "_interstitial"
                )
        );
    }

    @Override
    public boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                              StringFilterGroup matchedGroup, FilterContentType contentType, int contentIndex) {
        if (path.contains("|ImageType|")) closeFullscreenAds();

        return false; // Do not actually filter the fullscreen ad otherwise it will leave a dimmed screen.
    }
}
