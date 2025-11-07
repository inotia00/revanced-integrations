package app.revanced.integrations.reddit.patches;

import java.util.Collections;
import java.util.List;

import app.revanced.integrations.reddit.settings.Settings;

@SuppressWarnings("unused")
@Deprecated(forRemoval = true)
public final class RecentlyVisitedShelfPatch {

    public static List<?> hideRecentlyVisitedShelf(List<?> list) {
        return Settings.HIDE_RECENTLY_VISITED_SHELF.get() ? Collections.emptyList() : list;
    }
}
