package app.revanced.integrations.youtube.patches.components;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import app.revanced.integrations.shared.patches.components.ByteArrayFilterGroup;
import app.revanced.integrations.shared.patches.components.Filter;
import app.revanced.integrations.shared.patches.components.StringFilterGroup;
import app.revanced.integrations.shared.patches.components.StringFilterGroupList;
import app.revanced.integrations.shared.utils.StringTrieSearch;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.RootView;

@SuppressWarnings("unused")
public final class FeedComponentsFilter extends Filter {
    private static final String BROWSE_STORE_BUTTON_PATH = "|ContainerType|button.eml|";
    private static final String CONVERSATION_CONTEXT_FEED_IDENTIFIER =
            "horizontalCollectionSwipeProtector=null";
    private static final String CONVERSATION_CONTEXT_SUBSCRIPTIONS_IDENTIFIER =
            "heightConstraint=null";
    private static final ByteArrayFilterGroup browseStoreButton =
            new ByteArrayFilterGroup(
                    null,
                    "header_store_button"
            );
    private static final ByteArrayFilterGroup mixPlaylists =
            new ByteArrayFilterGroup(
                    Settings.HIDE_MIX_PLAYLISTS,
                    "&list="
            );
    private static final ByteArrayFilterGroup mixPlaylistsBufferExceptions =
            new ByteArrayFilterGroup(
                    null,
                    "cell_description_body",
                    "channel_profile"
            );
    private static final StringTrieSearch mixPlaylistsContextExceptions = new StringTrieSearch();

    public final StringFilterGroup carouselShelf;
    private final StringFilterGroup channelProfileButtonRule;
    private final StringFilterGroup communityPosts;

    private static final StringTrieSearch communityPostsFeedGroupSearch = new StringTrieSearch();
    private final StringFilterGroupList communityPostsFeedGroup = new StringFilterGroupList();


    public FeedComponentsFilter() {
        communityPostsFeedGroupSearch.addPatterns(
                CONVERSATION_CONTEXT_FEED_IDENTIFIER,
                CONVERSATION_CONTEXT_SUBSCRIPTIONS_IDENTIFIER
        );
        mixPlaylistsContextExceptions.addPatterns(
                "V.ED", // playlist browse id
                "java.lang.ref.WeakReference"
        );

        // Identifiers.

        carouselShelf = new StringFilterGroup(
                Settings.HIDE_CAROUSEL_SHELF,
                "horizontal_shelf.eml",
                "horizontal_tile_shelf.eml",
                "horizontal_video_shelf.eml"
        );

        final StringFilterGroup chipsShelf = new StringFilterGroup(
                Settings.HIDE_CHIPS_SHELF,
                "chips_shelf"
        );

        final StringFilterGroup feedSearchBar = new StringFilterGroup(
                Settings.HIDE_FEED_SEARCH_BAR,
                "search_bar_entry_point"
        );

        addIdentifierCallbacks(
                carouselShelf,
                chipsShelf,
                feedSearchBar
        );

        // Paths.
        final StringFilterGroup albumCard = new StringFilterGroup(
                Settings.HIDE_ALBUM_CARDS,
                "browsy_bar",
                "official_card"
        );

        channelProfileButtonRule = new StringFilterGroup(
                Settings.HIDE_BROWSE_STORE_BUTTON,
                "|channel_profile_"
        );

        final StringFilterGroup channelMemberShelf = new StringFilterGroup(
                Settings.HIDE_CHANNEL_MEMBER_SHELF,
                "member_recognition_shelf"
        );

        final StringFilterGroup channelProfileLinks = new StringFilterGroup(
                Settings.HIDE_CHANNEL_PROFILE_LINKS,
                "channel_header_links"
        );

        communityPosts = new StringFilterGroup(
                null,
                "post_base_wrapper",
                "image_post_root"
        );

        final StringFilterGroup expandableChip = new StringFilterGroup(
                Settings.HIDE_EXPANDABLE_CHIP,
                "inline_expansion"
        );

        final StringFilterGroup feedSurvey = new StringFilterGroup(
                Settings.HIDE_FEED_SURVEY,
                "feed_nudge",
                "_survey"
        );

        final StringFilterGroup forYouShelf = new StringFilterGroup(
                Settings.HIDE_FOR_YOU_SHELF,
                "mixed_content_shelf"
        );

        final StringFilterGroup imageShelf = new StringFilterGroup(
                Settings.HIDE_IMAGE_SHELF,
                "image_shelf"
        );

        final StringFilterGroup latestPosts = new StringFilterGroup(
                Settings.HIDE_LATEST_POSTS,
                "post_shelf"
        );

        final StringFilterGroup movieShelf = new StringFilterGroup(
                Settings.HIDE_MOVIE_SHELF,
                "compact_movie",
                "horizontal_movie_shelf",
                "movie_and_show_upsell_card",
                "compact_tvfilm_item",
                "offer_module"
        );

        final StringFilterGroup notifyMe = new StringFilterGroup(
                Settings.HIDE_NOTIFY_ME_BUTTON,
                "set_reminder_button"
        );

        final StringFilterGroup playables = new StringFilterGroup(
                Settings.HIDE_PLAYABLES,
                "horizontal_gaming_shelf.eml"
        );

        final StringFilterGroup subscriptionsChannelBar = new StringFilterGroup(
                Settings.HIDE_SUBSCRIPTIONS_CHANNEL_SECTION,
                "subscriptions_channel_bar"
        );

        final StringFilterGroup ticketShelf = new StringFilterGroup(
                Settings.HIDE_TICKET_SHELF,
                "ticket_horizontal_shelf",
                "ticket_shelf"
        );

        addPathCallbacks(
                albumCard,
                channelProfileButtonRule,
                channelMemberShelf,
                channelProfileLinks,
                communityPosts,
                expandableChip,
                feedSurvey,
                forYouShelf,
                imageShelf,
                latestPosts,
                movieShelf,
                notifyMe,
                playables,
                subscriptionsChannelBar,
                ticketShelf
        );

        final StringFilterGroup communityPostsHomeAndRelatedVideos =
                new StringFilterGroup(
                        Settings.HIDE_COMMUNITY_POSTS_HOME_RELATED_VIDEOS,
                        CONVERSATION_CONTEXT_FEED_IDENTIFIER
                );

        final StringFilterGroup communityPostsSubscriptions =
                new StringFilterGroup(
                        Settings.HIDE_COMMUNITY_POSTS_SUBSCRIPTIONS,
                        CONVERSATION_CONTEXT_SUBSCRIPTIONS_IDENTIFIER
                );

        communityPostsFeedGroup.addAll(communityPostsHomeAndRelatedVideos, communityPostsSubscriptions);
    }

    /**
     * Injection point.
     * <p>
     * Called from a different place then the other filters.
     */
    public static boolean filterMixPlaylists(final Object conversionContext, final byte[] bytes) {
        return bytes != null
                && mixPlaylists.check(bytes).isFiltered()
                && !mixPlaylistsBufferExceptions.check(bytes).isFiltered()
                && !mixPlaylistsContextExceptions.matches(conversionContext.toString());
    }

    private static final String BROWSE_ID_DEFAULT = "FEwhat_to_watch";
    private static final String BROWSE_ID_PLAYLIST = "VLPL";

    private static boolean hideShelves() {
        // If the search is active while library is selected, then filter.
        // Carousel shelf is not visible within the player, therefore does not check the player type.
        if (RootView.isSearchBarActive()) {
            return true;
        }

        // Check browseId last.
        // Only filter in home feed, search results, playlist.
        final String browseId = RootView.getBrowseId();
        return browseId.isEmpty() || StringUtils.startsWithAny(browseId, BROWSE_ID_DEFAULT, BROWSE_ID_PLAYLIST);
    }

    @Override
    public boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       StringFilterGroup matchedGroup, FilterContentType contentType, int contentIndex) {
        if (matchedGroup == carouselShelf) {
            if (hideShelves()) {
                return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
            }
            return false;
        } else if (matchedGroup == channelProfileButtonRule) {
            if (browseStoreButton.check(protobufBufferArray).isFiltered()) {
                return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
            }
            return false;
        } else if (matchedGroup == communityPosts) {
            if (!communityPostsFeedGroupSearch.matches(allValue) && Settings.HIDE_COMMUNITY_POSTS_CHANNEL.get()) {
                return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
            }
            if (!communityPostsFeedGroup.check(allValue).isFiltered()) {
                return false;
            }
        }

        return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
    }
}
