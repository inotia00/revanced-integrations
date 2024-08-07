package app.revanced.integrations.youtube.patches.utils;

import android.content.Context;
import android.media.AudioManager;

import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.VideoInformation;

@SuppressWarnings("unused")
public class AlwaysRepeatPatch extends Utils {

    /**
     * Injection point.
     *
     * @return video is repeated.
     */
    public static boolean alwaysRepeat() {
        return alwaysRepeatEnabled() && VideoInformation.overrideVideoTime(0);
    }

    public static boolean alwaysRepeatEnabled() {
        final boolean alwaysRepeat = Settings.ALWAYS_REPEAT.get();
        final boolean alwaysRepeatPause = Settings.ALWAYS_REPEAT_PAUSE.get();

        if (alwaysRepeat && alwaysRepeatPause) pauseMedia();
        return alwaysRepeat;
    }

    /**
     * Pause the media by changing audio focus.
     */
    private static void pauseMedia() {
        if (context != null && context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE) instanceof AudioManager audioManager) {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

}
