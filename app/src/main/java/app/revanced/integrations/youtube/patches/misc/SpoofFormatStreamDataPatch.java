package app.revanced.integrations.youtube.patches.misc;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.services.youtube.YoutubeService;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import app.revanced.integrations.shared.requests.Requester;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.shared.VideoInformation;

@SuppressWarnings("unused")
@RequiresApi(26) // Some methods of NewPipeExtractor are only available in Android 8.0+.
public final class SpoofFormatStreamDataPatch {
    private static final boolean spoofFormatStreamData = Settings.SPOOF_FORMAT_STREAM_DATA.get();

    /**
     * Response code of a successful API call
     */
    private static final int HTTP_STATUS_CODE_SUCCESS = 200;

    /**
     * Indicates a client rate limit has been reached and the client must back off.
     */
    private static final int HTTP_STATUS_CODE_RATE_LIMIT = 429;

    private static final String USER_AGENT
            = "Mozilla/5.0 (Windows NT 10.0; rv:91.0) Gecko/20100101 Firefox/91.0";

    /**
     * Itags fallback list
     * <a href="https://gist.github.com/MartinEesmaa/2f4b261cb90a47e9c41ba115a011a4aa">YouTube Formats</a>
     * TODO: Check if there are any issues with falling back to these itags
     */
    private static final int [] AVAILABLE_ITAG_ARRAY = {
            251, // Opus
            250, // Opus
            249, // Opus
            313, // VP9, 2160p
            271, // VP9, 1440p
            248, // VP9, 1080p
            247, // VP9, 720p
            22,  // H.264 (High, L3.1), 720p
            136, // H.264, 720p
            135, // H.264, 480p
            134, // H.264, 360p
            133, // H.264, 240p
            160, // H.264, 144p
    };

    /**
     * Last video id loaded. Used to prevent reloading the same spec multiple times.
     */
    @NonNull
    private static volatile String lastPlayerResponseVideoId = "";

    private static volatile Map<Integer, String> formatStreamDataMap;

    private static Downloader instance = getDownloader();

    /**
     * Injection point.
     */
    public static void hookStreamData(Object protobufList) {
        try {
            if (!spoofFormatStreamData) {
                return;
            }
            if (!(protobufList instanceof List<?> formatsList)) {
                return;
            }
            for (Object formatObject : formatsList) {

                // Set Field
                Field urlField = formatObject.getClass().getDeclaredField("replaceMeWithUrlFieldName");
                Field itagField = formatObject.getClass().getDeclaredField("replaceMeWithITagFieldName");
                Field audioCodecParameterField = formatObject.getClass().getDeclaredField("replaceMeWithAudioCodecParameterFieldName");
                audioCodecParameterField.setAccessible(true);
                urlField.setAccessible(true);
                itagField.setAccessible(true);

                // Check Field
                if (!(urlField.get(formatObject) instanceof String url)) continue;
                if (!(itagField.get(formatObject) instanceof Integer itagInteger)) continue;
                if (!(audioCodecParameterField.get(formatObject) instanceof String audioCodecParameter)) continue;

                if (!url.contains("googlevideo")) continue;
                // Since I used a locally modified NewPipeExtractor - https://github.com/inotia00/NewPipeExtractor -
                // it is fetched as ANDROID_TESTSUITE.
                // If you use jitpack's NewPipeExtractor library (original), it will be fetched as WEB.
                if (url.contains("ANDROID_TESTSUITE")) continue;
                // ANDROID_TESTSUITE does not support live streams.
                if (VideoInformation.getLiveStreamState()) continue;

                Logger.printDebug(() -> "Original StreamData: " + url);
                String itag = Uri.parse(url).getQueryParameter("itag");
                if (itag == null) {
                    Logger.printDebug(() -> "URL does not contain itag: " + url);
                    continue;
                }
                Logger.printDebug(() -> "itag field value: " + itagInteger);
                if (!audioCodecParameter.isEmpty()) {
                    Logger.printDebug(() -> "audio codec parameter field value: " + audioCodecParameter);
                }

                String replacement = formatStreamDataMap.get(Integer.parseInt(itag));
                if (replacement == null) {
                    for (int itags : AVAILABLE_ITAG_ARRAY) {
                        String formatStreamUrl = formatStreamDataMap.get(itags);
                        if (formatStreamUrl != null) {
                            Logger.printDebug(() -> "Falling back to itag: " + itags);
                            replacement = formatStreamUrl;

                            itagField.set(formatObject, itags);
                            if (249 <= itags && itags <= 251) {
                                audioCodecParameterField.set(formatObject, "");
                            }
                            break;
                        }
                    }
                }
                if (replacement == null) {
                    Logger.printDebug(() -> "No replacement found for itag, ignoring");
                    continue;
                }
                String finalReplacement = replacement;
                Logger.printDebug(() -> "Hooked StreamData: " + finalReplacement);
                urlField.set(formatObject, replacement);
            }
        } catch (Exception e) {
            Logger.printException(() -> "Hooked Error: " + e.getMessage(), e);
        }
    }

    /**
     * Injection point.
     * TODO: Make sure there are no issues without checking if the current video is Shorts.
     */
    public static void newPlayerResponseVideoId(@NonNull String videoId, boolean isShortAndOpeningOrPlaying) {
        if (!spoofFormatStreamData) {
            return;
        }
        if (videoId.equals(lastPlayerResponseVideoId)) {
            return;
        }
        lastPlayerResponseVideoId = videoId;

        try {
            formatStreamDataMap = Utils.submitOnBackgroundThread(() -> {
                String url = String.format("https://www.youtube.com/watch?v=%s", videoId);
                HashMap<Integer, String> formatStreamMap = new HashMap<>();
                if (instance == null) {
                    instance = getDownloader();
                }
                NewPipe.init(instance);

                Logger.printDebug(() -> "Fetching urls: " + videoId);

                StreamExtractor extractor = new YoutubeService(1).getStreamExtractor(url);
                extractor.fetchPage();

                StringBuilder sb1 = new StringBuilder("Put audioStream:");
                for (AudioStream audioStream : extractor.getAudioStreams()) {
                    sb1.append(" ");
                    sb1.append(audioStream.getItag());
                    sb1.append(",");
                    formatStreamMap.put(audioStream.getItag(), audioStream.getContent());
                }
                Logger.printDebug(() -> sb1.toString().replaceFirst(".$", ""));

                StringBuilder sb2 = new StringBuilder("Put videoOnlyStream:");
                for (VideoStream videoOnlyStream : extractor.getVideoOnlyStreams()) {
                    sb2.append(" ");
                    sb2.append(videoOnlyStream.getItag());
                    sb2.append(",");
                    formatStreamMap.put(videoOnlyStream.getItag(), videoOnlyStream.getContent());
                }
                Logger.printDebug(() -> sb2.toString().replaceFirst(".$", ""));

                StringBuilder sb3 = new StringBuilder("Put videoStream:");
                for (VideoStream videoStream : extractor.getVideoStreams()) {
                    sb3.append(" ");
                    sb3.append(videoStream.getItag());
                    sb3.append(",");
                    formatStreamMap.put(videoStream.getItag(), videoStream.getContent());
                }
                Logger.printDebug(() -> sb3.toString().replaceFirst(".$", ""));

                return formatStreamMap;
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.printException(() -> "Hooked Error making request: " + ex.getMessage(), ex);
        }
    }

    private static Downloader getDownloader() {
        return new Downloader() {
            @Override
            public Response execute(@NonNull Request request) throws IOException {
                HttpURLConnection connection = makeRequest(request);
                Logger.printDebug(() -> "Hooked got response");
                boolean body = false;
                Response response;
                try {
                    assert connection != null;
                    body = connection.getInputStream() != null;
                } catch (Exception ex) {
                    Logger.printException(() -> "Hooked Error making request: " + ex.getMessage(), ex);
                }
                try {
                    response = new Response(
                            connection.getResponseCode(),
                            connection.getResponseMessage(),
                            connection.getHeaderFields(),
                            body ? Requester.parseString(connection) : null,
                            connection.getURL().toString()
                    );
                } catch (IOException ex) {
                    Logger.printException(() -> "Hooked Error making request: " + ex.getMessage(), ex);
                    throw ex;
                }
                connection.disconnect();
                return response;
            }
        };
    }

    // TODO: Adds a setting that allows users to choose whether or not to show toast messages.
    private static void handleConnectionError(@NonNull String toastMessage, @Nullable Exception ex) {
        Logger.printInfo(() -> toastMessage, ex);
    }

    @Nullable
    private static HttpURLConnection makeRequest(final Request request) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(request.url()).openConnection();
            connection.setRequestMethod(request.httpMethod());
            connection.setUseCaches(false);
            connection.setDoOutput(Objects.equals(request.httpMethod(), "POST"));
            connection.setRequestProperty("User-Agent", USER_AGENT);

            for (final Map.Entry<String, List<String>> pair : request.headers().entrySet()) {
                final String headerName = pair.getKey();
                final List<String> headerValueList = pair.getValue();

                if (headerValueList.size() > 1) {
                    for (final String headerValue : headerValueList) {
                        connection.addRequestProperty(headerName, headerValue);
                    }
                } else if (headerValueList.size() == 1) {
                    connection.addRequestProperty(headerName, headerValueList.get(0));
                }
            }

            final byte[] innerTubeBody = request.dataToSend();
            if (innerTubeBody != null) {
                connection.getOutputStream().write(innerTubeBody, 0, innerTubeBody.length);
            }

            final int responseCode = connection.getResponseCode();
            if (responseCode == HTTP_STATUS_CODE_SUCCESS) {
                Logger.printDebug(() -> "Fetch successed");
                return connection;
            } else if (responseCode == HTTP_STATUS_CODE_RATE_LIMIT) {
                handleConnectionError("Hooked reCaptcha Challenge requested", null);
            } else {
                handleConnectionError("Hooked Error making request: " + responseCode, null);
            }
        } catch (Exception exception) {
            handleConnectionError("Hooked Error making request: " + exception.getMessage(), exception);
        }

        return null;
    }
}
