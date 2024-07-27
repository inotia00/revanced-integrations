package app.revanced.integrations.youtube.patches.components;

import androidx.annotation.Nullable;

import app.revanced.integrations.shared.patches.components.ByteArrayFilterGroup;
import app.revanced.integrations.shared.patches.components.ByteArrayFilterGroupList;
import app.revanced.integrations.shared.patches.components.Filter;
import app.revanced.integrations.shared.patches.components.StringFilterGroup;
import app.revanced.integrations.shared.utils.StringTrieSearch;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.RootView;

/**
 * @noinspection ALL
 */
public final class FeedVideoFilter extends Filter {
    private static final String CONVERSATION_CONTEXT_FEED_IDENTIFIER =
            "horizontalCollectionSwipeProtector=null";

    private static final StringTrieSearch feedVideoPattern = new StringTrieSearch();

    // In search results, vertical video with shorts labels mostly include videos with gray descriptions.
    // Filters without check process.
    private final StringFilterGroup inlineShorts;
    private final StringFilterGroup videoLockup;
    private final ByteArrayFilterGroupList bufferGroupList = new ByteArrayFilterGroupList();

    public FeedVideoFilter() {
        feedVideoPattern.addPattern(CONVERSATION_CONTEXT_FEED_IDENTIFIER);

        final StringFilterGroup homeFeedVideo = new StringFilterGroup(
                null,
                "home_video_with_context.eml"
        );
        final StringFilterGroup feedVideo = new StringFilterGroup(
                Settings.HIDE_RECOMMENDED_VIDEO,
                "related_video_with_context.eml",
                "search_video_with_context.eml"
        );
        inlineShorts = new StringFilterGroup(
                Settings.HIDE_RECOMMENDED_VIDEO,
                "inline_shorts.eml" // vertical video with shorts label
        );

        // Used for home, related videos, subscriptions, and search results.
        videoLockup = new StringFilterGroup(
                null,
                "video_lockup_with_attachment.eml"
        );

        addPathCallbacks(
                homeFeedVideo,
                feedVideo,
                inlineShorts,
                videoLockup
        );

        bufferGroupList.addAll(
                new ByteArrayFilterGroup(
                        Settings.HIDE_RECOMMENDED_VIDEO,
                        "endorsement_header_footer" // videos with gray descriptions
                ),
                new ByteArrayFilterGroup(
                        Settings.HIDE_RECOMMENDED_VIDEO,
                        "high-ptsZ" // videos for membership only
                ),
                new ByteArrayFilterGroup(
                        Settings.HIDE_LOW_VIEWS_VIDEO,
                        "g-highZ"  // videos with less than 1000 views
                )
        );
    }

    @Override
    public boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                              StringFilterGroup matchedGroup, FilterContentType contentType, int contentIndex) {
        if (matchedGroup == inlineShorts) {
            if (!RootView.isSearchBarActive()) {
                return false;
            }
        } else {
            if (contentIndex != 0) {
                return false;
            }
            if (matchedGroup == videoLockup && !feedVideoPattern.matches(allValue)) {
                return false;
            }
            if (!bufferGroupList.check(protobufBufferArray).isFiltered()) {
                return false;
            }
        }

        return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
    }
}
