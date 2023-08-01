package app.revanced.integrations.whitelist;

import static app.revanced.integrations.utils.ReVancedUtils.showToastShort;
import static app.revanced.integrations.utils.SharedPrefHelper.getBoolean;
import static app.revanced.integrations.utils.StringRef.str;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import app.revanced.integrations.patches.video.VideoChannel;
import app.revanced.integrations.patches.video.VideoInformation;
import app.revanced.integrations.settingsmenu.ReVancedSettingsFragment;
import app.revanced.integrations.utils.ReVancedUtils;
import app.revanced.integrations.utils.SharedPrefHelper;

public class Whitelist {

    private static final Map<WhitelistType, ArrayList<VideoChannel>> whitelistMap = parseWhitelist(ReVancedUtils.getContext());
    private static final Map<WhitelistType, Boolean> enabledMap = parseEnabledMap(ReVancedUtils.getContext());

    private Whitelist() {
    }

    public static boolean isChannelADSWhitelisted() {
        return isWhitelisted(WhitelistType.ADS);
    }

    public static boolean isChannelSBWhitelisted() {
        return isWhitelisted(WhitelistType.SPONSORBLOCK);
    }

    public static boolean isChannelSPEEDWhitelisted() {
        return isWhitelisted(WhitelistType.SPEED);
    }

    private static Map<WhitelistType, ArrayList<VideoChannel>> parseWhitelist(Context context) {
        if (context == null) {
            return Collections.emptyMap();
        }
        WhitelistType[] whitelistTypes = WhitelistType.values();
        Map<WhitelistType, ArrayList<VideoChannel>> whitelistMap = new EnumMap<>(WhitelistType.class);

        for (WhitelistType whitelistType : whitelistTypes) {
            SharedPreferences preferences = SharedPrefHelper.getPreferences(context, whitelistType.getPreferencesName());
            String serializedChannels = preferences.getString("channels", null);
            if (serializedChannels == null) {
                whitelistMap.put(whitelistType, new ArrayList<>());
                continue;
            }
            try {
                ArrayList<VideoChannel> deserializedChannels = (ArrayList<VideoChannel>) ObjectSerializer.deserialize(serializedChannels);
                whitelistMap.put(whitelistType, deserializedChannels);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return whitelistMap;
    }

    private static Map<WhitelistType, Boolean> parseEnabledMap(Context context) {
        if (context == null) {
            return Collections.emptyMap();
        }
        Map<WhitelistType, Boolean> enabledMap = new EnumMap<>(WhitelistType.class);
        for (WhitelistType whitelistType : WhitelistType.values()) {
            enabledMap.put(whitelistType, getBoolean(whitelistType.getSharedPreferencesName(), whitelistType.getPreferenceEnabledName(), false));
        }
        return enabledMap;
    }

    private static boolean isWhitelisted(WhitelistType whitelistType) {
        if (VideoInformation.getChannelName().equals("")) return false;
        for (VideoChannel channel : getWhitelistedChannels(whitelistType)) {
            if (channel.getAuthor().equals(VideoInformation.getChannelName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean addToWhitelist(WhitelistType whitelistType, Context context, VideoChannel channel) {
        ArrayList<VideoChannel> whitelisted = getWhitelistedChannels(whitelistType);
        for (VideoChannel whitelistedChannel : whitelisted) {
            String channelId = channel.getChannelId();
            if (whitelistedChannel.getChannelId().equals(channelId)) return true;
        }
        whitelisted.add(channel);
        return updateWhitelist(whitelistType, whitelisted, context);
    }

    public static void removeFromWhitelist(WhitelistType whitelistType, String channelName, Context context) {
        ArrayList<VideoChannel> channels = getWhitelistedChannels(whitelistType);
        Iterator<VideoChannel> iterator = channels.iterator();
        while (iterator.hasNext()) {
            VideoChannel channel = iterator.next();
            if (channel.getAuthor().equals(channelName)) {
                iterator.remove();
                break;
            }
        }
        boolean success = updateWhitelist(whitelistType, channels, ReVancedUtils.getContext());
        String friendlyName = whitelistType.getFriendlyName();
        if (success) {
            showToastShort(context, str("revanced_whitelisting_removed", channelName, friendlyName));
            ReVancedSettingsFragment.rebootDialogStatic(context, str("revanced_whitelisting_reboot"));
        } else {
            showToastShort(context, str("revanced_whitelisting_remove_failed", channelName, friendlyName));
        }
    }

    public static boolean updateWhitelist(WhitelistType whitelistType, ArrayList<VideoChannel> channels, Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences preferences = SharedPrefHelper.getPreferences(context, whitelistType.getPreferencesName());
        SharedPreferences.Editor editor = preferences.edit();

        try {
            editor.putString("channels", ObjectSerializer.serialize(channels));
            editor.apply();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setEnabled(WhitelistType whitelistType, boolean enabled) {
        enabledMap.put(whitelistType, enabled);
    }

    public static ArrayList<VideoChannel> getWhitelistedChannels(WhitelistType whitelistType) {
        return whitelistMap.get(whitelistType);
    }
}