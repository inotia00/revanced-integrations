package app.revanced.integrations.patches.utils;

import static app.revanced.integrations.sponsorblock.SegmentPlaybackController.initialize;
import static app.revanced.integrations.utils.ReVancedUtils.runOnMainThreadDelayed;
import static app.revanced.integrations.utils.SharedPrefHelper.SharedPrefNames.YOUTUBE;
import static app.revanced.integrations.utils.SharedPrefHelper.getString;
import static app.revanced.integrations.utils.SharedPrefHelper.saveString;
import static app.revanced.integrations.utils.StringRef.str;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.Random;

import app.revanced.integrations.BuildConfig;
import app.revanced.integrations.settings.SettingsEnum;

public class FirstRun {
    private static final String PREFERENCE_KEY = "integrations";

/*
I will not and never accept Stupid CUYNU VANCED+ use these key and copy the whitelist feature.
*/
    private static final String[] key = new String[] {"AIzaSyBieQxSpir6Y2-iYPokdu90UxqM_skzZFo", 
                                                      "AIzaSyDHzbmchOFVZx1hfqDyb2DQBXIy6udLkcU", 
                                                      "AIzaSyCHAzIGfl709SFlme_J1HXAkPyPKGF9hlQ",
                                                      "AIzaSyCUqxV-fNgpQZdpF_avB6-vzTgBafnhWwg",
                                                      "AIzaSyBJwlfXAwepLmrVj9GSPp1cYjAq5zcCdrk",
                                                      "AIzaSyBwKOhBps0NhNT6pAAwP2m5XdPgQUawcYo",
                                                      "AIzaSyAYsM7_05606VBLKYPfSeSHj81rvkbNN_U",
                                                      "AIzaSyC28h1S_kId35V6n0wR749yLrndF0yZyXM",
                                                      "AIzaSyD9SzQFnmOn08ESZC-7gIhnHWVn0asfrKQ",
                                                      "AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw",
                                                      "AIzaSyCl_5mEdSA7FZ4vY--lN2EiPx8rgah5yPQ",
                                                      "AIzaSyDCU8hByM-4DrUqRUYnGn-3llEO78bcxq8",
                                                      "AIzaSyDT6AVKwNjyWRWtVAdn86Q9I7HXJHG11iI",
                                                      "AIzaSyCIqxE86BawU33Um2HEGtX4PcrUWeCh_6o",
                                                      "AIzaSyA8Fsr_7_iPsQ8FcxBtv6WKoNpTVI9cE8E",
                                                      "AIzaSyC3iXWKvY6iOC3SoglRAekDXqutQiMuCxc",
                                                      "AIzaSyD3FPmFGDTyO_wHiUVMYSb7MQ4eFeveJw4",
                                                      "AIzaSyDY5KEE5eQbAxowrjoQY7L-N3wql81I45k",
                                                      "AIzaSyBYZEMh_dzM31b1zpiHdCRdZAHfUhYFjaE"};

    private static Random rand = new Random();

    private static final String GetPublicAPIKey(){
        return key[rand.nextInt(key.length)];
    }

    /**
     * For some reason, when I first install the app, my SponsorBlock settings are not initialized.
     * To solve this, forcibly initialize SponsorBlock.
     */
    public static void initializationSB(@NonNull Context context) {
        if (SettingsEnum.SB_FIRST_RUN.getBoolean()) return;
        initialize(null);
        SettingsEnum.SB_FIRST_RUN.saveValue(true);
    }

    /**
     * The new layout is not loaded when the app is first installed.
     * (Also reproduced on unPatched YouTube)
     * <p>
     * Side effects when new layout is not loaded:
     * - Button container's layout is broken
     * - Zoom to fill screen not working
     * - 8X zoom not working in fullscreen
     * <p>
     * To fix this, show the reboot dialog when the app is installed for the first time.
     * <p>
     * The version of the current integrations is saved to YouTube's SharedPreferences to identify if the app was first installed.
     */
    public static void initializationRVX(@NonNull Context context) {
        var integrationVersion = getString(YOUTUBE, PREFERENCE_KEY, null);

        if (!Objects.equals(integrationVersion, BuildConfig.VERSION_NAME))
            saveString(YOUTUBE, PREFERENCE_KEY, BuildConfig.VERSION_NAME);

        if (integrationVersion != null) return;

        // show dialog
        Activity activity = (Activity) context;

        new AlertDialog.Builder(activity)
                .setMessage(str("revanced_reboot_first_run"))
                .setPositiveButton(str("in_app_update_restart_button"), (dialog, id) ->
                        runOnMainThreadDelayed(() -> {
                            activity.finishAffinity();
                            activity.startActivity(activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName()));
                            System.exit(0);
                        }, 1000L)
                )
                .setNegativeButton(str("sign_in_cancel"), null)
                .setCancelable(false)
                .show();

        // set spoof player parameter default value
        SettingsEnum.SPOOF_PLAYER_PARAMETER.saveValue(!activity.getPackageName().equals("com.google.android.youtube"));
        
        // set public API Key Value
        SettingsEnum.WHITELIST_API_KEY.saveValue(GetPublicAPIKey());
    }
}
