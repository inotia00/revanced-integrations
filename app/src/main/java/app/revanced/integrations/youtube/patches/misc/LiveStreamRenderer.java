package app.revanced.integrations.youtube.patches.misc;

import org.jetbrains.annotations.NotNull;

/**
 * @noinspection ALL
 */
public final class LiveStreamRenderer {
    public final String videoId;
    public final String client;
    public final boolean playabilityOk;
    public final boolean isLiveStream;

    public LiveStreamRenderer(String videoId, String client, boolean playabilityOk, boolean isLiveStream) {
        this.videoId = videoId;
        this.client = client;
        this.playabilityOk = playabilityOk;
        this.isLiveStream = isLiveStream;
    }

    @NotNull
    @Override
    public String toString() {
        return "LiveStreamRenderer{" +
                "videoId=" + videoId +
                ", client=" + client +
                ", playabilityOk=" + playabilityOk +
                ", isLiveStream=" + isLiveStream +
                '}';
    }
}
