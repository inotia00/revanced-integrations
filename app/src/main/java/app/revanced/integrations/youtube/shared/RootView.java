package app.revanced.integrations.youtube.shared;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * @noinspection ALL
 */
public final class RootView {
    private static volatile WeakReference<View> searchBarResultsRef = new WeakReference<>(null);

    /**
     * Injection point.
     */
    public static void searchBarResultsViewLoaded(View searchbarResults) {
        searchBarResultsRef = new WeakReference<>(searchbarResults);
    }

    /**
     * @return If the search bar is on screen.  This includes if the player
     * is on screen and the search results are behind the player (and not visible).
     * Detecting the search is covered by the player can be done by checking {@link RootView#isPlayerActive()}.
     */
    public static boolean isSearchBarActive() {
        View searchbarResults = searchBarResultsRef.get();
        return searchbarResults != null && searchbarResults.getParent() != null;
    }

    public static boolean isPlayerActive() {
        return PlayerType.getCurrent().isMaximizedOrFullscreenOrSliding();
    }

    /**
     * Get current BrowseId.
     * Rest of the implementation added by patch.
     */
    public static String getBrowseId() {
        return "";
    }
}
