package app.revanced.integrations.youtube.patches.misc;

import app.revanced.integrations.youtube.shared.PlayerType;

@SuppressWarnings("unused")
public class BackgroundPlaybackPatch {

    public static boolean playbackIsNotShort() {
        return !PlayerType.getCurrent().isNoneHiddenOrSlidingMinimized();
    }

}
