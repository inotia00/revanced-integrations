package app.revanced.integrations.youtube.whitelist;

import static app.revanced.integrations.shared.utils.Utils.showToastShort;
import static app.revanced.integrations.shared.utils.StringRef.str;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import app.revanced.integrations.youtube.shared.VideoChannel;
import app.revanced.integrations.youtube.shared.VideoInformation;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;

public class Whitelist {

    private static final Map<WhitelistType, ArrayList<VideoChannel>> whitelistMap = parseWhitelist(Utils.getContext());
    private static final Map<WhitelistType, Boolean> enabledMap = parseEnabledMap(Utils.getContext());

    private Whitelist() {
    }

    private static SharedPreferences getPreferences(@NonNull Context context, @NonNull String prefName) {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
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
            SharedPreferences preferences = getPreferences(context, whitelistType.getPreferencesName());
            String serializedChannels = preferences.getString("channels", null);
            if (serializedChannels == null) {
                whitelistMap.put(whitelistType, new ArrayList<>());
                continue;
            }
            try {
                ArrayList<VideoChannel> deserializedChannels = (ArrayList<VideoChannel>) ObjectSerializer.deserialize(serializedChannels);
                whitelistMap.put(whitelistType, deserializedChannels);
            } catch (Exception ex) {
                Logger.printException(() -> "parseWhitelist failure", ex);
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
            enabledMap.put(whitelistType, whitelistType.getSharedPreferencesName().getBoolean(whitelistType.getPreferenceEnabledName(), false));
        }
        return enabledMap;
    }

    private static boolean isWhitelisted(WhitelistType whitelistType) {
        final String channelId = VideoInformation.getChannelId();
        if (channelId.equals("")) return false;
        for (VideoChannel channel : getWhitelistedChannels(whitelistType)) {
            if (channel.getChannelId().equals(channelId)) {
                return true;
            }
        }
        return false;
    }

    public static void addToWhitelist(WhitelistType whitelistType) {
        final VideoChannel channel = new VideoChannel(VideoInformation.getChannelName(), VideoInformation.getChannelId());
        ArrayList<VideoChannel> whitelisted = getWhitelistedChannels(whitelistType);
        for (VideoChannel whitelistedChannel : whitelisted) {
            String channelId = channel.getChannelId();
            if (whitelistedChannel.getChannelId().equals(channelId))
                return;
        }
        whitelisted.add(channel);
        boolean success = updateWhitelist(whitelistType, whitelisted, Utils.getContext());
        String friendlyName = whitelistType.getFriendlyName();
        if (success) {
            showToastShort(str("revanced_whitelisting_added", channel.getChannelName(), friendlyName));
        } else {
            showToastShort(str("revanced_whitelisting_add_failed", channel.getChannelName(), friendlyName));
        }
    }

    public static void removeFromWhitelist(WhitelistType whitelistType, String channelId) {
        ArrayList<VideoChannel> whitelisted = getWhitelistedChannels(whitelistType);
        Iterator<VideoChannel> iterator = whitelisted.iterator();
        String channelName = "";
        while (iterator.hasNext()) {
            VideoChannel channel = iterator.next();
            if (channel.getChannelId().equals(channelId)) {
                channelName = channel.getChannelName();
                iterator.remove();
                break;
            }
        }
        boolean success = updateWhitelist(whitelistType, whitelisted, Utils.getContext());
        String friendlyName = whitelistType.getFriendlyName();
        if (success) {
            showToastShort(str("revanced_whitelisting_removed", channelName, friendlyName));
        } else {
            showToastShort(str("revanced_whitelisting_remove_failed", channelName, friendlyName));
        }
    }

    public static boolean updateWhitelist(WhitelistType whitelistType, ArrayList<VideoChannel> channels, Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences preferences = getPreferences(context, whitelistType.getPreferencesName());
        SharedPreferences.Editor editor = preferences.edit();

        try {
            editor.putString("channels", ObjectSerializer.serialize(channels));
            editor.apply();
            return true;
        } catch (IOException ex) {
            Logger.printException(() -> "updateWhitelist failure", ex);
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
