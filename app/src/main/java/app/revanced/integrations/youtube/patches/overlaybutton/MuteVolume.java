package app.revanced.integrations.youtube.patches.overlaybutton;

import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.settings.Settings;
import app.revanced.integrations.youtube.utils.VideoUtils;
import app.revanced.integrations.youtube.utils.VolumeChangeReceiver;

import static app.revanced.integrations.shared.utils.Utils.getContext;
import static app.revanced.integrations.youtube.utils.VideoUtils.isAudioMuted;

@SuppressWarnings("unused")
public class MuteVolume extends BottomControlButton {
    private static MuteVolume instance;
    static VolumeChangeReceiver volumeChangeReceiver = new VolumeChangeReceiver();

    public MuteVolume(ViewGroup bottomControlsViewGroup) {
        super(bottomControlsViewGroup,
                "mute_volume_button",
                Settings.OVERLAY_BUTTON_MUTE_VOLUME,
                view -> {
                    VideoUtils.toggleMuteVolume();
                    if (instance != null)
                        instance.changeActivated(!isAudioMuted());
                },
                null
        );
        // Set the initial state of the button
        this.changeActivated(!isAudioMuted());

        // Register the volume change receiver to update the button state when the volume is changed
        IntentFilter filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
        getContext().registerReceiver(volumeChangeReceiver, filter);
    }

    public static void initialize(View ViewGroup) {
        try {
            if (ViewGroup instanceof ViewGroup bottomControlsViewGroup) {
                instance = new MuteVolume(bottomControlsViewGroup);
            }
        } catch (Exception e) {
            Logger.printException(() -> "initialize failure", e);
        }
    }

    public static void changeVisibility(boolean visible, boolean animation) {
        MuteVolume muteVolume = instance;
        if (muteVolume != null)
            muteVolume.setVisibility(visible, animation);
    }

    public static void changeVisibilityNegatedImmediate() {
        MuteVolume muteVolume = instance;
        if (muteVolume != null)
            muteVolume.setVisibilityNegatedImmediate();
    }

    // not used
    public static void notifyVolumeChange() {
        // TODO: not sure if this is implementable
        //  ideally we would want to change the button state when the volume is changed by the user
        //  by calling this method on VolumeKeysController.handleVolumeKeyEvent(). However, that method
        //  is not run if the volume is changed by the user in the YouTube app. A possible solution
        //  would be to use a global listener to detect volume changes, but I'm not sure if that's possible.
        Logger.printInfo(() -> "Volume changed");
        if (instance != null)
            instance.changeActivated(!isAudioMuted());
    }

    public static void destroy() {
        Logger.printInfo(() -> "Destroying MuteVolume");
        getContext().unregisterReceiver(volumeChangeReceiver);
    }
}
