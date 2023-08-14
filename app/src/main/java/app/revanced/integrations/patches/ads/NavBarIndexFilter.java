package app.revanced.integrations.patches.ads;

import androidx.annotation.Nullable;

import app.revanced.integrations.patches.utils.NavBarIndexPatch;
import app.revanced.integrations.settings.SettingsEnum;

public final class NavBarIndexFilter extends Filter {
    public NavBarIndexFilter() {
        pathFilterGroups.addAll(
                new StringFilterGroup(
                        SettingsEnum.HIDE_SUGGESTIONS_SHELF,
                        "horizontal_video_shelf",
                        "horizontal_tile_shelf"
                )
        );
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        final boolean isLibraryShelfShown = path.contains("library_recent_shelf");

        if (isLibraryShelfShown) {
            // If the library shelf is detected, set the current navbar index to 4
            NavBarIndexPatch.setCurrentNavBarIndex(4);
        } else if (super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex)) {
            // When the library shelf is not detected, but the suggestions shelf is detected
            // Block if the current navbar index is not 4
            return NavBarIndexPatch.isNotLibraryTab();
        }

        return false;
    }
}
