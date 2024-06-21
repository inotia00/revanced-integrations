package app.revanced.integrations.youtube.patches.utils;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class ReturnYouTubeChannelNamePatch {

    /**
     * Last unique channel name's loaded.  Value is ignored and Map is treated as a Set.
     * Cannot use {@link LinkedHashSet} because it's missing #removeEldestEntry().
     */
    private static final Map<String, String> channelNameMap = new LinkedHashMap<>() {
        /**
         * Number of channel name's to keep track of for searching thru the buffer.
         * A minimum value of 20 should be sufficient, but check a few more just in case.
         */
        private static final int NUMBER_OF_LAST_CHANNEL_NAME_TO_TRACK = 20;

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > NUMBER_OF_LAST_CHANNEL_NAME_TO_TRACK;
        }
    };

    /**
     * The last character of some handles is an official channel certification mark.
     * This was in the form of nonBreakSpaceCharacter before SpannableString was made.
     */
    private static final String NON_BREAK_SPACE_CHARACTER = "\u00A0";
    private static final String YT_FEED_API_URL = "https://www.youtube.com/feeds/videos.xml?channel_id=%s";
    private volatile static String channelName = "";

    /**
     * This method is only invoked on Shorts and is updated whenever the user swipes up or down on the Shorts.
     */
    public static void newShortsVideoStarted(@NonNull String newlyLoadedChannelId, @NonNull String newlyLoadedChannelName,
                                             @NonNull String newlyLoadedVideoId, @NonNull String newlyLoadedVideoTitle,
                                             final long newlyLoadedVideoLength, boolean newlyLoadedLiveStreamValue) {
        if (newlyLoadedChannelName.equals(channelName))
            return;

        Logger.printDebug(() -> "New channel name loaded: " + newlyLoadedChannelName);

        channelName = newlyLoadedChannelName;
    }

    /**
     * Injection point.
     */
    public static CharSequence onCharSequenceLoaded(@NonNull Object conversionContext,
                                                    @NonNull CharSequence charSequence) {
        try {
            if (!Settings.REPLACE_CHANNEL_HANDLE.get())
                return charSequence;

            final String conversionContextString = conversionContext.toString();
            final String originalString = charSequence.toString();

            if (!conversionContextString.contains("|reel_channel_bar_inner.eml|"))
                return charSequence;
            if (!originalString.startsWith("@"))
                return charSequence;

            return getChannelName(originalString);
        } catch (Exception ex) {
            Logger.printException(() -> "onCharSequenceLoaded failed", ex);
        }
        return charSequence;
    }

    private static CharSequence getChannelName(String handle) {
        final String trimmedHandle = handle.replaceAll(NON_BREAK_SPACE_CHARACTER, "");
        String replacedChannelName;

        // Priority: Prefetch channel name via api -> Channel name via hook -> Fallback to the original handle
        String cachedChannelName = channelNameMap.get(trimmedHandle);
        if (cachedChannelName != null) {
            replacedChannelName = cachedChannelName;
        } else if (!channelName.isEmpty()) {
            replacedChannelName = channelName;
        } else return handle;

        if (handle.contains(NON_BREAK_SPACE_CHARACTER)) {
            replacedChannelName += NON_BREAK_SPACE_CHARACTER;
        }
        String finalReplacedChannelName = replacedChannelName;
        Logger.printDebug(() -> "Replace Handle " + handle + " to " + finalReplacedChannelName);
        return replacedChannelName;
    }

    public synchronized static void setLastShortsChannelId(String handle, String channelId) {
        try {
            Objects.requireNonNull(handle);
            Objects.requireNonNull(channelId);

            if (channelNameMap.get(handle) != null) {
                return;
            }
            String xml = fetchChannelName(handle, channelId);
            if (xml.isEmpty()) return;
            final String regex = "<title>([^<].*)</title>";
            final Matcher matcher = Pattern.compile(regex).matcher(xml);
            if (!matcher.find()) return;
            //noinspection deprecation
            String channelName = StringEscapeUtils.unescapeXml(Objects.requireNonNull(matcher.group(1)).split("</title>")[0]);
            // Caching channel names are retrieved through the API only, to ensure accuracy
            if (channelNameMap.put(handle, channelName) == null) {
                Logger.printDebug(() -> "Set Handle: " + handle + ", Channel Name: " + channelName);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "Future failure ", ex); // will never happen
        }
    }

    @NonNull
    private synchronized static String fetchChannelName(String handle, String channelId) {
        String url = String.format(YT_FEED_API_URL, channelId);
        StringBuilder sBuffer = new StringBuilder();
        Logger.printDebug(() -> "Fetch Handle: " + handle + ", Channel Id: " + channelId);

        boolean isConnected;
        try {
            isConnected = Utils.submitOnBackgroundThread(() -> {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                connection.setRequestMethod("GET");
                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    connection.disconnect();
                    BufferedReader br = new BufferedReader(isr);
                    int lineCount = 0;
                    while (lineCount < 8) {
                        String line = br.readLine();
                        if (line == null) break;
                        sBuffer.append(line);
                        lineCount++;
                    }
                    return true;
                }
                connection.disconnect();
                Logger.printDebug(() -> "Unexpected response code: " + responseCode + " for url: " + url);
                return false;
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.printException(() -> "fetchChannelName failed", e);
            isConnected = false;
        }
        if (!isConnected) return "";

        return sBuffer.toString();
    }

}
