package app.revanced.integrations.shared.patches;

import android.content.Context;
import android.content.pm.PackageManager;

import org.apache.commons.lang3.StringUtils;

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

        final PackageManager packageManager = context.getPackageManager();

        // Package name of YouTube or YouTube Music.
        String originalPackageName;

        try {
            originalPackageName = packageManager
                    .getPackageInfo(packageName, PackageManager.GET_META_DATA)
                    .applicationInfo
                    .metaData
                    .getString(META_SPOOF_PACKAGE_NAME);
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.printDebug(() -> "Failed to parsing metadata");
            return packageName;
        }

        if (StringUtils.isBlank(originalPackageName)) {
            Logger.printDebug(() -> "Failed to parsing spoofed package name");
            return packageName;
        }

        try {
            packageManager.getPackageInfo(originalPackageName, PackageManager.GET_ACTIVITIES);
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
}