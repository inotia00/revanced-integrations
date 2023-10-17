package app.revanced.integrations.patches.misc;

import app.revanced.integrations.settings.SettingsEnum;

public class LanguageSelectorPatch {

    public static boolean enableLanguageSwitch() {
        return SettingsEnum.ENABLE_LANGUAGE_SWITCH.getBoolean();
    }
}
