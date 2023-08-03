package app.revanced.integrations.patches.ads;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.whitelist.Whitelist;

public class HideVideoAdsPatch {

    public static boolean hideVideoAds() {
        return !SettingsEnum.HIDE_VIDEO_ADS.getBoolean() || Whitelist.isChannelADSWhitelisted();
    }
}
