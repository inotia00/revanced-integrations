package app.revanced.integrations.patches.misc;

import app.revanced.integrations.shared.PlayerType;
import static app.revanced.integrations.utils.VideoHelpers.isDownloadBtnClicked;

public class MinimizedPlaybackPatch {

    public static boolean isPlaybackNotShort() {
        return !PlayerType.getCurrent().isNoneOrHidden() && !isDownloadBtnClicked();
    }

}
