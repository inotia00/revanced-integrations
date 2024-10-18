package app.revanced.integrations.youtube.patches.components;

import androidx.annotation.Nullable;

import app.revanced.integrations.shared.patches.components.ByteArrayFilterGroup;
import app.revanced.integrations.shared.patches.components.Filter;
import app.revanced.integrations.shared.patches.components.StringFilterGroup;
import app.revanced.integrations.shared.settings.BooleanSetting;
import app.revanced.integrations.shared.utils.StringTrieSearch;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.RootView;

@SuppressWarnings("unused")
public final class ShortsShelfFilter extends Filter {
    private static final String BROWSE_ID_HISTORY = "FEhistory";
    private static final String BROWSE_ID_LIBRARY = "FElibrary";
    private static final String BROWSE_ID_NOTIFICATION_INBOX = "FEnotifications_inbox";
    private static final String BROWSE_ID_SUBSCRIPTIONS = "FEsubscriptions";
    private static final String CONVERSATION_CONTEXT_FEED_IDENTIFIER =
            "horizontalCollectionSwipeProtector=null";
    private static final String SHELF_HEADER_PATH = "shelf_header.eml";
    private final StringFilterGroup compactFeedVideoPath;
    private final ByteArrayFilterGroup compactFeedVideoBuffer;
    private final StringFilterGroup shelfHeaderIdentifier;
    private final StringFilterGroup shelfHeaderPath;
    private static final StringTrieSearch feedGroup = new StringTrieSearch();
    private static final BooleanSetting hideShortsShelf = Settings.HIDE_SHORTS_SHELF;
    private static final BooleanSetting hideChannel = Settings.HIDE_SHORTS_SHELF_CHANNEL;
    private static final boolean hideHomeAndRelatedVideos = Settings.HIDE_SHORTS_SHELF_HOME_RELATED_VIDEOS.get();
    private static final boolean hideSubscriptions = Settings.HIDE_SHORTS_SHELF_SUBSCRIPTIONS.get();
    private static final boolean hideSearch = Settings.HIDE_SHORTS_SHELF_SEARCH.get();
    private static final boolean hideHistory = Settings.HIDE_SHORTS_SHELF_HISTORY.get();
    private final StringTrieSearch exceptions = new StringTrieSearch();
    private static final ByteArrayFilterGroup channelProfileShelfHeader =
            new ByteArrayFilterGroup(
                    hideChannel,
                    "Shorts"
            );

    public ShortsShelfFilter() {
        if (!hideHistory) {
            exceptions.addPattern("library_recent_shelf.eml");
        }
        feedGroup.addPattern(CONVERSATION_CONTEXT_FEED_IDENTIFIER);

        final StringFilterGroup channelProfile = new StringFilterGroup(
                hideChannel,
                "shorts_pivot_item"
        );

        shelfHeaderIdentifier = new StringFilterGroup(
                hideShortsShelf,
                SHELF_HEADER_PATH
        );

        addIdentifierCallbacks(channelProfile, shelfHeaderIdentifier);

        compactFeedVideoPath = new StringFilterGroup(
                hideShortsShelf,
                // Shorts that appear in the feed/search when the device is using tablet layout.
                "compact_video.eml",
                // Search results that appear in a horizontal shelf.
                "video_card.eml"
        );

        // Filter out items that use the 'frame0' thumbnail.
        // This is a valid thumbnail for both regular videos and Shorts,
        // but it appears these thumbnails are used only for Shorts.
        compactFeedVideoBuffer = new ByteArrayFilterGroup(
                hideShortsShelf,
                "/frame0.jpg"
        );

        // Feed Shorts shelf header.
        // Use a different filter group for this pattern, as it requires an additional check after matching.
        shelfHeaderPath = new StringFilterGroup(
                hideShortsShelf,
                SHELF_HEADER_PATH
        );

        final StringFilterGroup shorts = new StringFilterGroup(
                hideShortsShelf,
                "shorts_shelf",
                "inline_shorts",
                "shorts_grid",
                "shorts_video_cell"
        );

        addPathCallbacks(compactFeedVideoPath, shelfHeaderPath, shorts);
    }

    @Override
    public boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                              StringFilterGroup matchedGroup, FilterContentType contentType, int contentIndex) {
        if (exceptions.matches(path)) {
            return false;
        }
        // Check channel profile components first
        if (matchedGroup == shelfHeaderPath) {
            // Because the header is used in watch history and possibly other places, check for the index,
            // which is 0 when the shelf header is used for Shorts.
            if (contentIndex != 0) {
                return false;
            }
            if (!channelProfileShelfHeader.check(protobufBufferArray).isFiltered()) {
                return false;
            }
            if (feedGroup.matches(allValue)) {
                return false;
            }
            return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
        }
        // Check feed components
        if (!hideShelves()) {
            return false;
        }
        if (matchedGroup == compactFeedVideoPath) {
            if (compactFeedVideoBuffer.check(protobufBufferArray).isFiltered()) {
                return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
            }
            return false;
        } else if (matchedGroup == shelfHeaderIdentifier) {
            // Check ConversationContext to not hide shelf header in channel profile
            // This value does not exist in the shelf header in the channel profile
            if (!feedGroup.matches(allValue)) {
                return false;
            }
        }

        // Super class handles logging.
        return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
    }

    private static boolean hideShelves() {
        if (hideHomeAndRelatedVideos && hideSubscriptions && hideSearch && hideHistory) {
            // Shorts suggestions can load in the background if a video is opened and
            // then immediately minimized before any suggestions are loaded.
            // In this state the player type will show minimized, which makes it not possible to
            // distinguish between Shorts suggestions loading in the player and between
            // scrolling thru search/home/subscription tabs while a player is minimized.
            //
            // To avoid this situation for users that never want to show Shorts (all hide Shorts options are enabled)
            // then hide all Shorts everywhere including the Library history and Library playlists.
            return true;
        }

        // Must check player type first, as search bar can be active behind the player.
        if (RootView.isPlayerActive()) {
            // For now, consider the under video results the same as the home feed.
            return hideHomeAndRelatedVideos;
        }

        // Must check second, as search can be from any tab.
        if (RootView.isSearchBarActive()) {
            return hideSearch;
        }

        // Avoid checking navigation button status if all other Shorts should show.
        if (!hideHomeAndRelatedVideos && !hideSubscriptions && !hideHistory) {
            return false;
        }

        final String browseId = RootView.getBrowseId();
        switch (browseId) {
            case BROWSE_ID_HISTORY, BROWSE_ID_LIBRARY, BROWSE_ID_NOTIFICATION_INBOX -> {
                return hideHistory;
            }
            case BROWSE_ID_SUBSCRIPTIONS -> {
                return hideSubscriptions;
            }
            default -> {
                return hideHomeAndRelatedVideos;
            }
        }
    }
}
