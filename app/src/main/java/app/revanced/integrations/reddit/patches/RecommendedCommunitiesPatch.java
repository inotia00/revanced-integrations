package app.revanced.integrations.reddit.patches;

import app.revanced.integrations.reddit.settings.Settings;

@SuppressWarnings("unused")
public final class RecommendedCommunitiesPatch {

    public static boolean hideRecommendedCommunitiesShelf() {
        return Settings.HIDE_RECOMMENDED_COMMUNITIES_SHELF.get();
    }

}
