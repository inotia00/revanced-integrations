package app.revanced.integrations.youtube.patches.overlaybutton;

import static app.revanced.integrations.shared.utils.StringRef.str;

import android.app.AlertDialog;
import android.content.Context;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import app.revanced.integrations.youtube.patches.utils.PatchStatus;
import app.revanced.integrations.youtube.shared.VideoInformation;
import app.revanced.integrations.youtube.whitelist.Whitelist;
import app.revanced.integrations.youtube.whitelist.WhitelistType;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class Whitelists extends BottomControlButton {
    @Nullable
    private static Whitelists instance;

    static boolean isSBIncluded;
    static boolean isSPEEDIncluded;

    public Whitelists(ViewGroup bottomControlsViewGroup) {
        super(
                bottomControlsViewGroup,
                "whitelist_button",
                Settings.OVERLAY_BUTTON_WHITELIST,
                view -> Whitelists.OpenDialog(view.getContext()),
                null
        );
    }

    /**
     * Injection point.
     */
    public static void initialize(View bottomControlsViewGroup) {
        try {
            if (bottomControlsViewGroup instanceof ViewGroup viewGroup) {
                isSBIncluded = PatchStatus.SponsorBlock();
                isSPEEDIncluded = PatchStatus.PlaybackSpeed();
                if (!isSBIncluded && !isSPEEDIncluded)
                    Settings.OVERLAY_BUTTON_WHITELIST.save(false);

                instance = new Whitelists(viewGroup);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "initialize failure", ex);
        }
    }

    /**
     * Injection point.
     */
    public static void changeVisibility(boolean showing, boolean animation) {
        if (instance != null) instance.setVisibility(showing, animation);
    }

    public static void changeVisibilityNegatedImmediate() {
        if (instance != null) instance.setVisibilityNegatedImmediate();
    }

    public static void OpenDialog(Context context) {
        String included = str("revanced_whitelisting_included");
        String excluded = str("revanced_whitelisting_excluded");

        final Boolean isSBWhitelisted = Whitelist.isChannelSBWhitelisted();
        final Boolean isSPEEDWhitelisted = Whitelist.isChannelSPEEDWhitelisted();

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);

        builder.setTitle(str("revanced_whitelisting_title"));

        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(str("revanced_whitelisting_channel_name"));
        msgBuilder.append(":\n");
        msgBuilder.append(VideoInformation.getChannelName());
        msgBuilder.append("\n\n");

        if (isSPEEDIncluded) {
            msgBuilder.append(str("revanced_whitelisting_speed"));
            msgBuilder.append(":\n");
            msgBuilder.append(isSPEEDWhitelisted ? included : excluded);
            msgBuilder.append("\n\n");
            builder.setNeutralButton(str("revanced_whitelisting_speed_button"),
                (dialog, id) -> {
                    WhitelistListener(WhitelistType.SPEED, isSPEEDWhitelisted);
                    dialog.dismiss();
                }
            );
        }

        if (isSBIncluded) {
            msgBuilder.append(str("revanced_whitelisting_sponsorblock"));
            msgBuilder.append(":\n");
            msgBuilder.append(isSBWhitelisted ? included : excluded);
            builder.setPositiveButton(str("revanced_whitelisting_sponsorblock_button"),
                (dialog, id) -> {
                    WhitelistListener(WhitelistType.SPONSORBLOCK, isSBWhitelisted);
                    dialog.dismiss();
                }
            );
        }

        builder.setMessage(msgBuilder.toString());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void WhitelistListener(WhitelistType whitelistType, boolean isChannelWhitelisted) {
        try {
            if (isChannelWhitelisted) {
                Whitelist.removeFromWhitelist(whitelistType, VideoInformation.getChannelId());
            } else {
                Whitelist.addToWhitelist(whitelistType);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "Failed to perform action", ex);
        }
    }
}
