package app.revanced.integrations.youtube.patches.video;

import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class VP9CodecPatch {

    public static boolean disableVP9Codec() {
        return !Settings.DISABLE_VP9_CODEC.get();
    }
}
