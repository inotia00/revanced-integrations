package app.revanced.music.patches.ads;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import app.revanced.music.settings.SettingsEnum;
import app.revanced.music.utils.StringTrieSearch;


public final class ButtonShelfFilter extends Filter {
    private final StringTrieSearch exceptions = new StringTrieSearch();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ButtonShelfFilter() {
        exceptions.addPatterns(
                "comment_thread",
                "menu",
                "root",
                "-count",
                "-space",
                "-button"
        );

        final var buttonShelf = new StringFilterGroup(
                SettingsEnum.HIDE_BUTTON_SHELF,
                "entry_point_button_shelf"
        );

        this.pathFilterGroups.addAll(
                buttonShelf
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
