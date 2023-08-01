package app.revanced.integrations.whitelist;

import static app.revanced.integrations.utils.StringRef.str;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.SharedPrefHelper;

public enum WhitelistType {
    ADS(SharedPrefHelper.SharedPrefNames.REVANCED, SettingsEnum.ADS_WHITELIST.path),
    SPEED(SharedPrefHelper.SharedPrefNames.REVANCED, SettingsEnum.SPEED_WHITELIST.path),
    SPONSORBLOCK(SharedPrefHelper.SharedPrefNames.REVANCED, SettingsEnum.SB_WHITELIST.path);

    private final String friendlyName;
    private final String preferencesName;
    private final String preferenceEnabledName;
    private final SharedPrefHelper.SharedPrefNames name;

    WhitelistType(SharedPrefHelper.SharedPrefNames name, String preferenceEnabledName) {
        this.friendlyName = str("revanced_whitelisting_" + name().toLowerCase());
        this.name = name;
        this.preferencesName = "whitelist_" + name();
        this.preferenceEnabledName = preferenceEnabledName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public SharedPrefHelper.SharedPrefNames getSharedPreferencesName() {
        return name;
    }

    public String getPreferencesName() {
        return preferencesName;
    }

    public String getPreferenceEnabledName() {
        return preferenceEnabledName;
    }
}
