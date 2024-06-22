package app.revanced.integrations.youtube.patches.misc;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

/**
 * @noinspection ALL
 */
public final class PlayerResponseRenderer {
    public final String videoId;
    public final String client;
    public final boolean playabilityOk;
    public final boolean isLive;
    @Nullable
    public final String storyBoardSpec;
    /**
     * Recommended image quality level, or NULL if no recommendation exists.
     */
    @Nullable
    public final Integer storyBoardRecommendedLevel;

    public PlayerResponseRenderer(String videoId, String client, boolean playabilityOk, boolean isLive,
                                  @Nullable String storyBoardSpec, @Nullable Integer storyBoardRecommendedLevel) {
        this.videoId = videoId;
        this.client = client;
        this.playabilityOk = playabilityOk;
        this.isLive = isLive;
        this.storyBoardSpec = storyBoardSpec;
        this.storyBoardRecommendedLevel = storyBoardRecommendedLevel;
    }

    @NotNull
    @Override
    public String toString() {
        return "PlayerResponseRenderer{" +
                ", client=" + client +
                ", playabilityOk=" + playabilityOk +
                ", isLive=" + isLive +
                ", storyBoardSpec='" + storyBoardSpec + '\'' +
                ", storyBoardRecommendedLevel=" + storyBoardRecommendedLevel +
                '}';
    }
}
