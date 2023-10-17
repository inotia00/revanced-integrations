package app.revanced.music.patches.player;

import static app.revanced.music.utils.ResourceUtils.identifier;
import static app.revanced.music.utils.StringRef.str;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import app.revanced.music.patches.utils.CheckMusicVideoPatch;
import app.revanced.music.settings.SettingsEnum;
import app.revanced.music.shared.VideoType;
import app.revanced.music.utils.ReVancedUtils;
import app.revanced.music.utils.ResourceType;
import app.revanced.music.utils.VideoHelpers;

public class PlayerPatch {

    public static boolean enableColorMatchPlayer() {
        return SettingsEnum.ENABLE_COLOR_MATCH_PLAYER.getBoolean();
    }

    public static boolean enableForceMinimizedPlayer(boolean original) {
        return SettingsEnum.ENABLE_FORCE_MINIMIZED_PLAYER.getBoolean() || original;
    }

    public static boolean enableNewPlayerBackground() {
        return SettingsEnum.ENABLE_NEW_PLAYER_BACKGROUND.getBoolean();
    }

    public static boolean enableOldPlayerLayout() {
        return !SettingsEnum.ENABLE_OLD_PLAYER_LAYOUT.getBoolean();
    }

    public static boolean enableOldStyleMiniPlayer(boolean original) {
        return !SettingsEnum.ENABLE_OLD_STYLE_MINI_PLAYER.getBoolean() && original;
    }

    public static boolean enableZenMode() {
        return SettingsEnum.ENABLE_ZEN_MODE.getBoolean();
    }

    public static boolean rememberRepeatState(boolean original) {
        return SettingsEnum.REMEMBER_REPEAT_SATE.getBoolean() || original;
    }

    public static boolean rememberShuffleState() {
        return SettingsEnum.REMEMBER_SHUFFLE_SATE.getBoolean();
    }

    private static void prepareOpenMusic(@NonNull Context context) {
        if (!VideoType.getCurrent().isMusicVideo()) {
            ReVancedUtils.showToastShort(str("revanced_playlist_dismiss"));
            return;
        }
        final String songId = CheckMusicVideoPatch.getSongId();
        if (songId.isEmpty()) {
            ReVancedUtils.showToastShort(str("revanced_playlist_error"));
            return;
        }
        VideoHelpers.openInMusic(context, songId);
    }

    public static void replaceCastButton(Activity activity, ViewGroup viewGroup, View originalView) {
        if (!SettingsEnum.REPLACE_PLAYER_CAST_BUTTON.getBoolean()) {
            viewGroup.addView(originalView);
            return;
        }

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(activity).inflate(identifier("open_music_button", ResourceType.LAYOUT), null);
        ImageView musicButtonView = (ImageView) linearLayout.getChildAt(0);

        musicButtonView.setOnClickListener(
                imageView -> prepareOpenMusic(imageView.getContext())
        );

        viewGroup.addView(linearLayout);
    }

    public static int getShuffleState() {
        return SettingsEnum.SHUFFLE_SATE.getInt();
    }

    public static void setShuffleState(int buttonState) {
        if (!SettingsEnum.REMEMBER_SHUFFLE_SATE.getBoolean())
            return;
        SettingsEnum.SHUFFLE_SATE.saveValue(buttonState);
    }
}
