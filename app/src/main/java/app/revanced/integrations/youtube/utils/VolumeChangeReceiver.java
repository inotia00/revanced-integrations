package app.revanced.integrations.youtube.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import app.revanced.integrations.youtube.patches.overlaybutton.MuteVolume;

/**
 * Receiver to notify the MuteVolume button when the volume is changed
 */
public class VolumeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if ("android.media.VOLUME_CHANGED_ACTION".equals(action)) {
            MuteVolume.notifyVolumeChange();
        }
    }
}