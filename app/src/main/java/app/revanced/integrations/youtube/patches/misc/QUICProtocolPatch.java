package app.revanced.integrations.youtube.patches.misc;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class QUICProtocolPatch {

    public static boolean disableQUICProtocol(boolean original) {
        try {
            return !Settings.DISABLE_QUIC_PROTOCOL.get() && original;
        } catch (Exception ex) {
            Logger.printException(() -> "Failed to load disableQUICProtocol", ex);
        }
        return original;
    }
}
