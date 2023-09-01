package app.revanced.integrations.patches.ads;

import androidx.annotation.Nullable;

import app.revanced.integrations.settings.SettingsEnum;

final class PlayerFlyoutPanelsFooterFilter extends Filter {

    public PlayerFlyoutPanelsFooterFilter() {

        pathFilterGroups.addAll(
            new StringFilterGroup(
                SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_QUALITY_FOOTER,
                "quality_sheet_footer",
                "|divider.eml|"
            ),
            new StringFilterGroup(
                SettingsEnum.HIDE_PLAYER_FLYOUT_PANEL_CAPTIONS_FOOTER,
                "|ContainerType|ContainerType|ContainerType|TextType|",
                "|divider.eml|"
            )
        );
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        // If the flyout is not caption or quality, skip
        if ( (path.contains("captions_sheet_content.eml-js") || path.contains("advanced_quality_sheet_content.eml-js")) && !path.contains("bottom_sheet_list_option"))
            return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedList, matchedGroup, matchedIndex);

        return false;
    }
}
