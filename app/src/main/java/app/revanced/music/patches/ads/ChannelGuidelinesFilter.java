package app.revanced.music.patches.ads;


import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import app.revanced.music.settings.SettingsEnum;
import app.revanced.music.utils.StringTrieSearch;


public final class ChannelGuidelinesFilter extends Filter {
    private final StringTrieSearch exceptions = new StringTrieSearch();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ChannelGuidelinesFilter() {
        exceptions.addPatterns(
                "comment_thread",
                "menu",
                "root",
                "-count",
                "-space",
                "-button"
        );

        final var channelGuidelines = new StringFilterGroup(
                SettingsEnum.HIDE_CHANNEL_GUIDELINES,
                "community_guidelines"
        );

        this.pathFilterGroups.addAll(
                channelGuidelines
        );
    }

    @Override
    public boolean isFiltered(String path, @Nullable String identifier,
                              FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        if (exceptions.matches(path))
            return false;

        return super.isFiltered(path, identifier, matchedList, matchedGroup, matchedIndex);
    }
}
