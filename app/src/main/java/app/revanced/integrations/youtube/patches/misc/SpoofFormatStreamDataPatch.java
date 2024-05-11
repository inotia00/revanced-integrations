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
            if (!(protobufList instanceof List<?> formatsList)) {
                return;
            }
            for (Object formatObject : formatsList) {
                Field field = formatObject.getClass().getDeclaredField("replaceMeWithFieldName");
                field.setAccessible(true);
                if (!(field.get(formatObject) instanceof String url)) continue;
                if (!url.contains("googlevideo")) continue;
                // Since I used a locally modified NewPipeExtractor - https://github.com/inotia00/NewPipeExtractor -
                // it is fetched as ANDROID_TESTSUITE.
                // If you use jitpack's NewPipeExtractor library (original), it will be fetched as WEB.
                if (url.contains("ANDROID_TESTSUITE")) continue;
                var itag = Uri.parse(url).getQueryParameter("itag");
                if (itag == null) {
                    Logger.printDebug(() -> "URL does not contain itag: " + url);
                    continue;
                }
                String replacement = formatStreamDataMap.get(Integer.parseInt(itag));
                if (replacement == null) {
                    // lowest quality
                    Logger.printDebug(() -> "Falling back to itag 133");
                    replacement = formatStreamDataMap.get(133);
                }
                if (replacement == null) {
                    Logger.printDebug(() -> "No replacement found for itag: " + itag);
                    continue;
                }
                String finalReplacement = replacement;
                Logger.printDebug(() -> "Original StreamData: " + url);
                Logger.printDebug(() -> "Hooked StreamData: " + finalReplacement);
                field.set(formatObject, replacement);
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
                for (AudioStream audioStream : extractor.getAudioStreams()) {
                    formatStreamMap.put(audioStream.getItag(), audioStream.getContent());
                }

                for (VideoStream videoOnlyStream : extractor.getVideoOnlyStreams()) {
                    formatStreamMap.put(videoOnlyStream.getItag(), videoOnlyStream.getContent());
                }

                for (VideoStream videoStream : extractor.getVideoStreams()) {
                    formatStreamMap.put(videoStream.getItag(), videoStream.getContent());
                }

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
            Logger.printDebug(() -> "Hooked request");

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
            Logger.printDebug(() -> "Hooked headers");

            final byte[] innerTubeBody = request.dataToSend();
            if (innerTubeBody != null) {
                connection.getOutputStream().write(innerTubeBody, 0, innerTubeBody.length);
            }
            Logger.printDebug(() -> "Hooked body");

            final int responseCode = connection.getResponseCode();
            if (responseCode == HTTP_STATUS_CODE_SUCCESS) {
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
