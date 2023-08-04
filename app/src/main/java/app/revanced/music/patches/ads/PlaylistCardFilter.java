package app.revanced.music.patches.ads;


import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import app.revanced.music.settings.SettingsEnum;
import app.revanced.music.utils.StringTrieSearch;


public final class PlaylistCardFilter extends Filter {
    private final StringTrieSearch exceptions = new StringTrieSearch();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PlaylistCardFilter() {
        exceptions.addPatterns(
                "comment_thread",
                "menu",
                "root",
                "-count",
                "-space",
                "-button"
        );

        final var playlistCard = new StringFilterGroup(
                SettingsEnum.HIDE_PLAYLIST_CARD,
                "music_container_card_shelf"
        );

        this.pathFilterGroups.addAll(
                playlistCard
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
