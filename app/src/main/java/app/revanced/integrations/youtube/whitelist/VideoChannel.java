package app.revanced.integrations.youtube.whitelist;

import java.io.Serializable;

public final class VideoChannel implements Serializable {
    private String channelName;
    private String channelId;

    public VideoChannel(String channelName, String channelId) {
        this.channelName = channelName;
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelId() {
        return channelId;
    }
}
