package app.revanced.integrations.patches.button;

import static app.revanced.integrations.utils.ResourceUtils.anim;
import static app.revanced.integrations.utils.ResourceUtils.findView;
import static app.revanced.integrations.utils.ResourceUtils.integer;
import static app.revanced.integrations.utils.StringRef.str;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import app.revanced.integrations.patches.utils.PatchStatus;
import app.revanced.integrations.patches.video.VideoInformation;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.whitelist.Whitelist;
import app.revanced.integrations.whitelist.WhitelistType;
import app.revanced.integrations.whitelist.requests.WhitelistRequester;

public class Whitelists {
    static WeakReference<ImageView> buttonView = new WeakReference<>(null);
    @SuppressLint("StaticFieldLeak")
    static ConstraintLayout constraintLayout;
    static int fadeDurationFast;
    static int fadeDurationScheduled;
    static Animation fadeIn;
    static Animation fadeOut;
    public static boolean isButtonEnabled;
    static boolean isShowing;
    static boolean isScrubbed;

    static boolean isSBWhitelisted;
    static boolean isSPEEDWhitelisted;

    static boolean isSBIncluded;
    static boolean isSPEEDIncluded;

    public static void initialize(Object obj) {
        try {
            constraintLayout = (ConstraintLayout) obj;

            isSBWhitelisted = Whitelist.isChannelSBWhitelisted();
            isSPEEDWhitelisted = Whitelist.isChannelSPEEDWhitelisted();

            isSBIncluded = PatchStatus.SponsorBlock();
            isSPEEDIncluded = PatchStatus.VideoSpeed();

            isButtonEnabled = setValue();

            ImageView imageView = findView(Whitelists.class, constraintLayout, "whitelist_button");
            imageView.setOnClickListener(view -> Whitelists.OpenDialog(view.getContext()));
            buttonView = new WeakReference<>(imageView);

            fadeDurationFast = integer("fade_duration_fast");
            fadeDurationScheduled = integer("fade_duration_scheduled");

            fadeIn = anim("fade_in");
            fadeIn.setDuration(fadeDurationFast);

            fadeOut = anim("fade_out");
            fadeOut.setDuration(fadeDurationScheduled);

            isShowing = true;
            isScrubbed = false;
            changeVisibility(false);

        } catch (Exception ex) {
            LogHelper.printException(Whitelists.class, "Unable to set FrameLayout", ex);
        }
    }

    public static void refreshVisibility() {
        isButtonEnabled = setValue();
    }

    private static boolean setValue() {
        boolean isEnabled = SettingsEnum.OVERLAY_BUTTON_WHITELIST.getBoolean();

        return isEnabled && (isSBIncluded || isSPEEDIncluded);
    }

    public static void changeVisibility(boolean currentVisibility) {
        ImageView imageView = buttonView.get();

        if (isShowing == currentVisibility || constraintLayout == null || imageView == null) return;

        isShowing = currentVisibility;

        if (isScrubbed && isButtonEnabled) {
            isScrubbed = false;
            imageView.setVisibility(View.VISIBLE);
            return;
        }

        if (currentVisibility && isButtonEnabled) {
            imageView.setVisibility(View.VISIBLE);
            imageView.startAnimation(fadeIn);
        } else if (imageView.getVisibility() == View.VISIBLE) {
            imageView.startAnimation(fadeOut);
            imageView.setVisibility(View.GONE);
        }
    }

    public static void changeVisibilityNegatedImmediate(boolean isUserScrubbing) {
        ImageView imageView = buttonView.get();

        if (constraintLayout == null || imageView == null || !isUserScrubbing) return;

        isShowing = false;
        isScrubbed = true;
        imageView.setVisibility(View.GONE);
    }

    public static void OpenDialog(Context context) {
        String included = str("revanced_whitelisting_included");
        String excluded = str("revanced_whitelisting_excluded");

        isSBWhitelisted = Whitelist.isChannelSBWhitelisted();
        isSPEEDWhitelisted = Whitelist.isChannelSPEEDWhitelisted();

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
                    WhitelistListener(WhitelistType.SPEED, isSPEEDWhitelisted, context);
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
                    WhitelistListener(WhitelistType.SPONSORBLOCK, isSBWhitelisted, context);
                    dialog.dismiss();
                }
            );
        }

        builder.setMessage(msgBuilder.toString());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void WhitelistListener(WhitelistType whitelistType, boolean status, Context context) {
        if (!status) {
            addToWhiteList(whitelistType);
        } else {
            removeFromWhitelist(whitelistType, context);
        }
    }

    private static void removeFromWhitelist(WhitelistType whitelistType, Context context) {
        ImageView imageView = buttonView.get();
        if (constraintLayout == null || imageView == null) return;

        try {
            Whitelist.removeFromWhitelist(whitelistType, VideoInformation.getChannelName(), context);
        } catch (Exception ex) {
            LogHelper.printException(Whitelists.class, "Failed to remove from whitelist", ex);
        }
    }

    private static void addToWhiteList(WhitelistType whitelistType) {
        new Thread(() -> WhitelistRequester.addChannelToWhitelist(whitelistType)).start();
    }
}
