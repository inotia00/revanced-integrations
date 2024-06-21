package app.revanced.integrations.shared.patches;

import android.content.Context;
import android.content.pm.PackageManager;

import app.revanced.integrations.shared.settings.BaseSettings;
import app.revanced.integrations.shared.utils.Logger;

@SuppressWarnings("unused")
public class SpoofSignaturePatch extends GmsCoreSupport {

    /**
     * Injection point.
     */
    public static String spoofPackageName(Context context) {

        // Package name of ReVanced.
        final String packageName = context.getPackageName();

        if (!BaseSettings.SPOOF_SIGNATURE.get()) {
            return packageName;
        }

        // Package name of YouTube or YouTube Music.
        final String originalPackageName = getOriginalPackageName();

        try {
            PackageManager manager = context.getPackageManager();
            manager.getPackageInfo(originalPackageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.printDebug(() -> "Original app was not found");
            return packageName;
        }

        if (contentProviderClientUnAvailable(context)) {
            Logger.printDebug(() -> "GmsCore is not running in the background");
            return packageName;
        }

        if (batteryOptimizationsEnabled(context)) {
            Logger.printDebug(() -> "GmsCore is not whitelisted from battery optimizations");
            return packageName;
        }

        final String logMessage = "Package name of " + packageName + " spoofed to " + originalPackageName;
        Logger.printDebug(() -> logMessage);

        return originalPackageName;
    }


    // Modified by a patch. Do not touch.
    private static String getOriginalPackageName() {
        return "com.google.android.youtube";
    }
}