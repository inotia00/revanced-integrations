package app.revanced.integrations.patches.ads;

import static app.revanced.integrations.utils.ReVancedHelper.isSpoofedTargetVersionLez;
import static app.revanced.integrations.utils.ReVancedUtils.hideViewBy0dpUnderCondition;

import android.view.View;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.ReVancedHelper;

public final class SuggestionsShelfFilter extends Filter {

    private static final StringFilterGroup horizontalShelf =
            new StringFilterGroup(
                    SettingsEnum.HIDE_SUGGESTIONS_SHELF,
                    "horizontal_tile_shelf.eml",
                    "horizontal_video_shelf.eml"
    );

    private static final List<String> horizontalShelfHeader = Arrays.asList(
            "horizontalCollectionSwipeProtector=null",
            "shelf_header.eml"
    );

    public SuggestionsShelfFilter() {
        pathFilterGroups.addAll(horizontalShelf);
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        if (ReVancedHelper.isTablet)
            return true;
        else
            return horizontalShelfHeader.stream().allMatch(allValue::contains);
    }

    /**
     * Injection point.
     * <p>
     * In this method, only subcomponents are created:
     * - horizontal video shelf in feed (horizontal_video_shelf.eml)
     * - video action bar (video_action_bar.eml)
     * <p>
     * Horizontal video shelf used in library tab is not used in this method
     * The header of the suggestion shelf cannot be removed here
     *
     * @param object allValue
     * @return whether horizontal video shelf contains
     */
    public static boolean filterSuggestionsShelfSubComponents(Object object) {
        return horizontalShelf.check(object.toString()).isFiltered();
    }

    /**
     * Injection point.
     * <p>
     * Only used to tablet layout and the old UI components.
     */
    public static void hideBreakingNewsShelf(View view) {
        hideViewBy0dpUnderCondition(
                SettingsEnum.HIDE_SUGGESTIONS_SHELF.getBoolean()
                        && !isSpoofedTargetVersionLez("17.31.00"),
                view
        );
    }
}
