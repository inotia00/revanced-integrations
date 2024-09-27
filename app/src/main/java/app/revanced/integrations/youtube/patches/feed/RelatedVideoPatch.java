package app.revanced.integrations.youtube.patches.feed;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.RootView;

@SuppressWarnings("unused")
public final class RelatedVideoPatch {
    private static final boolean HIDE_RELATED_VIDEOS = Settings.HIDE_RELATED_VIDEOS.get();

    // video title,channel bar, video action bar, comment
    private static final int MAX_ITEM_COUNT = 4;

    private static final AtomicBoolean engagementPanelOpen = new AtomicBoolean(false);

    public static void showEngagementPanel(@Nullable Object object) {
        engagementPanelOpen.set(object != null);
    }

    public static void hideEngagementPanel() {
        engagementPanelOpen.compareAndSet(true, false);
    }

    public static int overrideItemCounts(int itemCounts) {
        if (!HIDE_RELATED_VIDEOS) {
            return itemCounts;
        }
        if (!RootView.isPlayerActive()) {
            return itemCounts;
        }
        if (engagementPanelOpen.get()) {
            return itemCounts;
        }
        return Math.min(itemCounts, MAX_ITEM_COUNT);
    }

}
