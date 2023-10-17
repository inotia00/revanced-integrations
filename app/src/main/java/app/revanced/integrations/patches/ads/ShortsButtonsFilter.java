package app.revanced.integrations.patches.ads;

import app.revanced.integrations.settings.SettingsEnum;

final class ShortsButtonsFilter extends Filter {

    public ShortsButtonsFilter() {
        final var shortsDislikeButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_DISLIKE_BUTTON,
                "shorts_dislike_button"
        );


        final var shortsLikeButton = new StringFilterGroup(
                SettingsEnum.HIDE_SHORTS_PLAYER_LIKE_BUTTON,
                "shorts_like_button"
        );

        this.pathFilterGroupList.addAll(
                shortsDislikeButton,
                shortsLikeButton
        );
    }
}
