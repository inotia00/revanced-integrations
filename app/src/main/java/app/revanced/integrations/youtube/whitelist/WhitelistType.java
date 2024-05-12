package app.revanced.integrations.youtube.whitelist;

import static app.revanced.integrations.shared.utils.StringRef.str;

import app.revanced.integrations.shared.settings.Setting;
import app.revanced.integrations.shared.settings.preference.SharedPrefCategory;
import app.revanced.integrations.youtube.settings.Settings;

public enum WhitelistType {
    SPEED(Setting.preferences, Settings.SPEED_WHITELIST.key),
    SPONSORBLOCK(Setting.preferences, Settings.SB_WHITELIST.key);

    private final String friendlyName;
    private final String preferencesName;
    private final String preferenceEnabledName;
    private final SharedPrefCategory name;

    WhitelistType(SharedPrefCategory name, String preferenceEnabledName) {
        this.friendlyName = str("revanced_whitelisting_" + name().toLowerCase());
        this.name = name;
        this.preferencesName = "whitelist_" + name();
        this.preferenceEnabledName = preferenceEnabledName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public SharedPrefCategory getSharedPreferencesName() {
        return name;
    }

    public String getPreferencesName() {
        return preferencesName;
    }

    public String getPreferenceEnabledName() {
        return preferenceEnabledName;
    }
}
