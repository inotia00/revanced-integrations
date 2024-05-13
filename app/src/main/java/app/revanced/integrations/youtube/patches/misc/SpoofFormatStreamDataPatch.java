package app.revanced.integrations.youtube.patches.misc;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
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
     * Last endpoint video id loaded. Used to prevent reloading the same spec multiple times.
     */
    @NonNull
    private static volatile String lastEndpointVideoId = "";

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
            if (formatStreamDataMap == null || formatStreamDataMap.isEmpty()) {
                return;
            }
            if (!(protobufList instanceof List<?> formatsList)) {
                return;
            }
            for (Object formatObject : formatsList) {
                // Set Field
                Field urlField = formatObject.getClass().getDeclaredField("replaceMeWithUrlFieldName");
                Field itagField = formatObject.getClass().getDeclaredField("replaceMeWithITagFieldName");
                urlField.setAccessible(true);
                itagField.setAccessible(true);

                // Check Field
                if (!(urlField.get(formatObject) instanceof String url)) continue;
                if (!(itagField.get(formatObject) instanceof Integer itagInteger)) continue;

                if (!url.contains("googlevideo")) continue;
                // Since I used a locally modified NewPipeExtractor - https://github.com/inotia00/NewPipeExtractor -
                // it is fetched as ANDROID_TESTSUITE.
                // If you use jitpack's NewPipeExtractor library (original), it will be fetched as WEB.
                if (url.contains("ANDROID_TESTSUITE")) continue;

                Logger.printDebug(() -> "Original StreamData: " + url);
                String itag = Uri.parse(url).getQueryParameter("itag");
                if (itag == null) {
                    Logger.printDebug(() -> "URL does not contain itag: " + url);
                    continue;
                }
                Logger.printDebug(() -> "itag field value: " + itagInteger);

                String replacement = formatStreamDataMap.get(Integer.parseInt(itag));
                if (replacement == null) {
                    Logger.printDebug(() -> "No replacement found for itag, ignoring");
                    continue;
                }
                Logger.printDebug(() -> "Hooked StreamData: " + replacement);
                urlField.set(formatObject, replacement);
            }
        } catch (Exception e) {
            Logger.printException(() -> "Hooked Error: " + e.getMessage(), e);
        }
    }

    /**
     * PlayerResponse is made after StreamingData is invoked.
     * Therefore, we cannot use {@link VideoInformation#getPlayerResponseVideoId}.
     * Instead, use the videoId query parameter in EndpointUrl.
     *
     * @param endpointUrl   It has a similar format to the 'baseEndpointUrl' variable in the {@link YoutubeParsingHelper#getMobilePostResponse} method.
     */
    public static void newEndpointUrlResponse(@Nullable String endpointUrl) {
        // Example format for EndpointUrl:
        // https://youtubei.googleapis.com/youtubei/v1/player?key=AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w&t=J2aJjSG9n2pQ&id=dQw4w9WgXcQ
        String videoId = Uri.parse(endpointUrl).getQueryParameter("id");
        if (videoId != null) {
            Logger.printDebug(() -> "newEndpointUrlResponse: " + endpointUrl);
            setFormatStreamData(videoId);
        }
    }

    private static void setFormatStreamData(@NonNull String videoId) {
        if (!spoofFormatStreamData) {
            return;
        }
        if (videoId.equals(lastEndpointVideoId)) {
            return;
        }
        lastEndpointVideoId = videoId;

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

                StringBuilder audioStreamBuilder = new StringBuilder("Put audioStream:");
                for (AudioStream audioStream : extractor.getAudioStreams()) {
                    audioStreamBuilder.append(" ");
                    audioStreamBuilder.append(audioStream.getItag());
                    audioStreamBuilder.append(",");
                    formatStreamMap.put(audioStream.getItag(), audioStream.getContent());
                }
                handleStreamBuilder(audioStreamBuilder);

                StringBuilder videoOnlyStreamBuilder = new StringBuilder("Put videoOnlyStream:");
                for (VideoStream videoOnlyStream : extractor.getVideoOnlyStreams()) {
                    videoOnlyStreamBuilder.append(" ");
                    videoOnlyStreamBuilder.append(videoOnlyStream.getItag());
                    videoOnlyStreamBuilder.append(",");
                    formatStreamMap.put(videoOnlyStream.getItag(), videoOnlyStream.getContent());
                }
                handleStreamBuilder(videoOnlyStreamBuilder);

                StringBuilder videoStreamBuilder = new StringBuilder("Put videoStream:");
                for (VideoStream videoStream : extractor.getVideoStreams()) {
                    videoStreamBuilder.append(" ");
                    videoStreamBuilder.append(videoStream.getItag());
                    videoStreamBuilder.append(",");
                    formatStreamMap.put(videoStream.getItag(), videoStream.getContent());
                }
                handleStreamBuilder(videoStreamBuilder);

                return formatStreamMap;
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.printException(() -> "Hooked Error making request: " + ex.getMessage(), ex);
        }
    }

    private static void handleStreamBuilder(StringBuilder sb) {
        String message = sb.toString();
        if (message.charAt(message.length() - 1) == ',') {
            Logger.printDebug(() -> message.replaceFirst(".$", ""));
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
