package app.revanced.integrations.youtube.patches.shorts;

import static app.revanced.integrations.shared.utils.Utils.hideViewUnderCondition;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import app.revanced.integrations.shared.utils.ResourceUtils;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class ShortsPatch {
    public static Enum<?> repeat;
    public static Enum<?> singlePlay;
    public static Enum<?> endScreen;

    public static Enum<?> changeShortsRepeatState(Enum<?> currentState) {
        switch (Settings.CHANGE_SHORTS_REPEAT_STATE.get()) {
            case 1 -> currentState = repeat;
            case 2 -> currentState = singlePlay;
            case 3 -> currentState = endScreen;
        }

        return currentState;
    }

    public static boolean disableResumingStartupShortsPlayer() {
        return Settings.DISABLE_RESUMING_SHORTS_PLAYER.get();
    }

    public static void hideShortsCommentsButton(View view) {
        hideViewUnderCondition(Settings.HIDE_SHORTS_COMMENTS_BUTTON.get(), view);
    }

    public static boolean hideShortsDislikeButton() {
        return Settings.HIDE_SHORTS_DISLIKE_BUTTON.get();
    }

    public static ViewGroup hideShortsInfoPanel(ViewGroup viewGroup) {
        return Settings.HIDE_SHORTS_INFO_PANEL.get() ? null : viewGroup;
    }

    public static boolean hideShortsLikeButton() {
        return Settings.HIDE_SHORTS_LIKE_BUTTON.get();
    }

    public static boolean hideShortsPaidPromotionLabel() {
        return Settings.HIDE_SHORTS_PAID_PROMOTION_LABEL.get();
    }

    public static void hideShortsPaidPromotionLabel(TextView textView) {
        hideViewUnderCondition(Settings.HIDE_SHORTS_PAID_PROMOTION_LABEL.get(), textView);
    }

    public static void hideShortsRemixButton(View view) {
        hideViewUnderCondition(Settings.HIDE_SHORTS_REMIX_BUTTON.get(), view);
    }

    public static void hideShortsShareButton(View view) {
        hideViewUnderCondition(Settings.HIDE_SHORTS_SHARE_BUTTON.get(), view);
    }

    public static boolean hideShortsSoundButton() {
        return Settings.HIDE_SHORTS_SOUND_BUTTON.get();
    }

    private static final int zeroPaddingDimenId = ResourceUtils.getDimenIdentifier("revanced_zero_padding");

    public static int getShortsSoundButtonDimenId(int dimenId) {
        return Settings.HIDE_SHORTS_SOUND_BUTTON.get()
                ? zeroPaddingDimenId
                : dimenId;
    }

    public static int hideShortsSubscribeButton(int original) {
        return Settings.HIDE_SHORTS_SUBSCRIBE_BUTTON.get() ? 0 : original;
    }

    public static boolean hideShortsToolBar(boolean original) {
        return !Settings.HIDE_SHORTS_TOOLBAR.get() && original;
    }

    private static WeakReference<View> pivotBarViewRef = new WeakReference<>(null);

    public static void setNavigationBar(Object pivotBar) {
        if (pivotBar instanceof View pivotBarView) {
            pivotBarViewRef = new WeakReference<>(pivotBarView);
        }
    }

    public static View hideShortsNavigationBar(View view) {
        return Settings.HIDE_SHORTS_NAVIGATION_BAR.get() ? null : view;
    }

    public static void hideShortsNavigationBar() {
        final View pivotBarView = pivotBarViewRef.get();
        if (pivotBarView != null) {
            hideViewUnderCondition(Settings.HIDE_SHORTS_NAVIGATION_BAR.get(), pivotBarView);
        }
    }

}
