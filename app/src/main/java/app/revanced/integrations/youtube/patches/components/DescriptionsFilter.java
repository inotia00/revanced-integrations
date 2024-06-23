package app.revanced.integrations.youtube.patches.components;

import androidx.annotation.Nullable;

import app.revanced.integrations.shared.patches.components.Filter;
import app.revanced.integrations.shared.patches.components.StringFilterGroup;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class DescriptionsFilter extends Filter {
    private final StringFilterGroup chapterSection;
    private final StringFilterGroup shoppingLinks;

    public DescriptionsFilter() {
        final StringFilterGroup infoCardsSection = new StringFilterGroup(
                Settings.HIDE_INFO_CARDS_SECTION,
                "infocards_section.eml"
        );

        final StringFilterGroup podcastSection = new StringFilterGroup(
                Settings.HIDE_PODCAST_SECTION,
                "playlist_section.eml"
        );

        // game section, music section and places section now use the same identifier in the latest version.
        final StringFilterGroup attributesSection = new StringFilterGroup(
                Settings.HIDE_ATTRIBUTES_SECTION,
                "gaming_section.eml",
                "music_section.eml",
                "place_section.eml",
                "video_attributes_section.eml"
        );

        final StringFilterGroup transcriptSection = new StringFilterGroup(
                Settings.HIDE_TRANSCRIPT_SECTION,
                "transcript_section.eml"
        );

        addIdentifierCallbacks(
                attributesSection,
                infoCardsSection,
                podcastSection,
                transcriptSection
        );

        chapterSection = new StringFilterGroup(
                Settings.HIDE_CHAPTERS_SECTION,
                "macro_markers_carousel."
        );

        shoppingLinks = new StringFilterGroup(
                Settings.HIDE_SHOPPING_LINKS,
                "expandable_list.",
                "shopping_description_shelf"
        );


        addPathCallbacks(
                chapterSection,
                shoppingLinks
        );
    }

    @Override
    public boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       StringFilterGroup matchedGroup, FilterContentType contentType, int contentIndex) {
        // Check for the index because of likelihood of false positives.
        if (matchedGroup == chapterSection || matchedGroup == shoppingLinks) {
            if (contentIndex != 0) {
                return false;
            }
        }

        return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
    }
}
