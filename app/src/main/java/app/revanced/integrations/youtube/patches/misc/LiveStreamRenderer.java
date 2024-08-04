package app.revanced.integrations.youtube.patches.misc;

import org.jetbrains.annotations.NotNull;

/**
 * @noinspection ALL
 */
public final class LiveStreamRenderer {
    public final String videoId;
    public final String client;
    public final boolean playabilityOk;
    public final boolean isLiveContent;

    public LiveStreamRenderer(String videoId, String client, boolean playabilityOk, boolean isLiveContent) {
        this.videoId = videoId;
        this.client = client;
        this.playabilityOk = playabilityOk;
        this.isLiveContent = isLiveContent;
    }

    @NotNull
    @Override
    public String toString() {
        return "LiveStreamRenderer{" +
                "videoId=" + videoId +
                ", client=" + client +
                ", playabilityOk=" + playabilityOk +
                ", isLiveContent=" + isLiveContent +
                '}';
    }
}
