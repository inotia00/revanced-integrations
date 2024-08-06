package app.revanced.integrations.youtube.patches.misc;

import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.utils.VideoUtils;

@SuppressWarnings("unused")
public class HookDownloadAction {

    /*
     * Injection point
     */
    public static boolean isPlaylistDownloadButtonHooked() {
        return Settings.HOOK_PLAYLIST_DOWNLOAD_BUTTON.get();
    }

    /*
     * Injection point
     */
    public static String startPlaylistDownloadActivity(String playlistId) {
        if (!isPlaylistDownloadButtonHooked()) return playlistId;

        VideoUtils.launchPlaylistExternalDownloader(playlistId);

        // return an empty string to prevent
        // the original implementation from being called
        return "";
    }
}
