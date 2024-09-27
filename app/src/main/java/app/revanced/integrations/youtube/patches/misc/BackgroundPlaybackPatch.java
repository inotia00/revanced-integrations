package app.revanced.integrations.youtube.patches.misc;

import app.revanced.integrations.youtube.shared.ShortsPlayerState;

@SuppressWarnings("unused")
public class BackgroundPlaybackPatch {

    public static boolean allowBackgroundPlayback(boolean original) {
        return original || ShortsPlayerState.getCurrent().isClosed();
    }

}
