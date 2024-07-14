package app.revanced.integrations.music.patches.misc;

import static app.revanced.integrations.music.settings.Settings.ALTERNATIVE_DOMAIN;
import static app.revanced.integrations.music.settings.Settings.USE_ALTERNATIVE_DOMAIN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.revanced.integrations.shared.utils.Logger;

@SuppressWarnings("unused")
public final class AlternativeDomainPatch {

    private static final String YOUTUBE_STATIC_THUMBNAILS_DOMAIN_REGEX = "(yt[3-4]|lh[3-6]|play-lh)\\.(ggpht|googleusercontent)\\.com";

    private static final Pattern YOUTUBE_STATIC_THUMBNAILS_DOMAIN_PATTERN = Pattern.compile(YOUTUBE_STATIC_THUMBNAILS_DOMAIN_REGEX);


    public static String overrideImageURL(String originalUrl) {
        try {
            if (USE_ALTERNATIVE_DOMAIN.get()) {
                final Matcher matcher = YOUTUBE_STATIC_THUMBNAILS_DOMAIN_PATTERN.matcher(originalUrl);
                if (matcher.find()) {
                    final String finalOriginalUrl = originalUrl;
                    final String finalReplacementUrl = originalUrl.replaceAll(
                            YOUTUBE_STATIC_THUMBNAILS_DOMAIN_REGEX,
                            ALTERNATIVE_DOMAIN.get()
                    );
                    Logger.printDebug(() -> "Replaced: '" + finalOriginalUrl + "' with: '" + finalReplacementUrl + "'");
                    return finalReplacementUrl;
                }
            }
        } catch (Exception ex) {
            Logger.printException(() -> "overrideImageURL failure", ex);
        }
        return originalUrl;
    }
}