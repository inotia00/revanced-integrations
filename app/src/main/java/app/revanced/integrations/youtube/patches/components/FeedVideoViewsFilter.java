package app.revanced.integrations.youtube.patches.components;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.revanced.integrations.shared.patches.components.Filter;
import app.revanced.integrations.shared.patches.components.StringFilterGroup;
import app.revanced.integrations.youtube.settings.Settings;

/**
 * @noinspection ALL
 */
public final class FeedVideoViewsFilter extends Filter {
    private final StringFilterGroup feedVideoFilter;

    public FeedVideoViewsFilter() {
        feedVideoFilter = new StringFilterGroup(
                null, // Multiple settings are used and must be individually checked if active.
                "video_with_context.eml",
                "video_lockup_with_attachment.eml"
        );

        // Paths.
        addPathCallbacks(feedVideoFilter);
    }

    @Override
    public boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                              StringFilterGroup matchedGroup, FilterContentType contentType, int contentIndex) {
        if (filterVideos(protobufBufferArray)) {
            return super.isFiltered(path, identifier, allValue, protobufBufferArray, matchedGroup, contentType, contentIndex);
        }

        return false;
    }

    private final String ARROW = " -> ";
    private final String VIEWS = "views";
    private final String[] parts = Settings.HIDE_VIDEO_VIEW_COUNTS_MULTIPLIER.get().split("\\n");
    private Pattern[] viewCountPatterns = null;

    private boolean filterVideos(byte[] protobufBufferArray) {
        final String protobufString = new String(protobufBufferArray);

        boolean hideBasedOnDuration = false;
        boolean hideBasedOnViews = false;

        if (Settings.HIDE_VIDEO_BY_DURATION.get())
            hideBasedOnDuration = filterByDuration(protobufString);

        if (Settings.HIDE_VIDEO_BY_VIEW_COUNTS.get())
            hideBasedOnViews = filterByViews(protobufString);

        return hideBasedOnDuration || hideBasedOnViews;
    }

    /**
     * Hide videos based on duration
     */
    private boolean filterByDuration(String protobufString) {
        Pattern durationPattern = getDurationPattern();

        String shorterThanStr = Settings.HIDE_VIDEO_BY_DURATION_SHORTER_THAN.get();
        String longerThanStr = Settings.HIDE_VIDEO_BY_DURATION_LONGER_THAN.get();

        long shorterThan = parseDuration(shorterThanStr);
        long longerThan = parseDuration(longerThanStr);

        Matcher matcher = durationPattern.matcher(protobufString);
        if (matcher.find()) {
            String durationString = Objects.requireNonNull(matcher.group());
            long durationInSeconds = convertToSeconds(durationString);
            return checkDuration(durationInSeconds, shorterThan, longerThan);
        }

        return false;
    }

    private long parseDuration(String durationString) {
        if (durationString.contains(":")) {
            return convertToSeconds(durationString);
        } else {
            return Long.parseLong(durationString);
        }
    }

    private Pattern getDurationPattern() {
        // Pattern: hours? : minutes : seconds
        return Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+)");
    }

    private long convertToSeconds(String durationString) {
        String[] parts = durationString.split(":");
        int length = parts.length;
        long seconds = 0;
        for (int i = length - 1; i >= 0; i--) {
            long value = Long.parseLong(parts[i]);
            seconds += (long) (value * Math.pow(60, length - 1 - i));
        }
        return seconds;
    }

    private boolean checkDuration(long durationInSeconds, long shorterThan, long longerThan) {
        if (shorterThan < 0 || longerThan < 0)
            throw new IllegalArgumentException("Duration cannot be negative.");

        return durationInSeconds < shorterThan || durationInSeconds > longerThan;
    }

    /**
     * Hide videos badsed on views count
     */
    private synchronized boolean filterByViews(String protobufString) {
        final long lessThan = Settings.HIDE_VIDEO_VIEW_COUNTS_LESS_THAN.get();
        final long greaterThan = Settings.HIDE_VIDEO_VIEW_COUNTS_GREATER_THAN.get();

        if (viewCountPatterns == null) {
            viewCountPatterns = getViewCountPatterns(parts);
        }

        for (Pattern pattern : viewCountPatterns) {
            final Matcher matcher = pattern.matcher(protobufString);
            if (matcher.find()) {
                String numString = Objects.requireNonNull(matcher.group(1));
                double num = parseNumber(numString);
                String multiplierKey = matcher.group(2);
                long multiplierValue = getMultiplierValue(parts, multiplierKey);
                return num * multiplierValue < lessThan || num * multiplierValue > greaterThan;
            }
        }

        return false;
    }

    private synchronized double parseNumber(String numString) {
        /**
         * Some languages have comma (,) as a decimal separator.
         * In order to detect those numbers as doubles in Java
         * we convert commas (,) to dots (.).
         * Unless we find a language that has commas used in
         * a different manner, it should work.
         */
        numString = numString.replace(",", ".");

        /**
         * Some languages have dot (.) as a kilo separator.
         * So we check with regex if there is a number with 3+
         * digits after dot (.), we replace it with nothing
         * to make Java understand the number as a whole.
         */
        if (numString.matches("\\d+\\.\\d{3,}")) {
            numString = numString.replace(".", "");
        }

        return Double.parseDouble(numString);
    }

    private synchronized Pattern[] getViewCountPatterns(String[] parts) {
        StringBuilder prefixPatternBuilder = new StringBuilder("(\\d+(?:[.,]\\d+)?)\\s?("); // LTR layout
        StringBuilder secondPatternBuilder = new StringBuilder(); // RTL layout
        StringBuilder suffixBuilder = getSuffixBuilder(parts, prefixPatternBuilder, secondPatternBuilder);

        prefixPatternBuilder.deleteCharAt(prefixPatternBuilder.length() - 1); // Remove the trailing |
        prefixPatternBuilder.append(")?\\s*");
        prefixPatternBuilder.append(suffixBuilder.length() > 0 ? suffixBuilder.toString() : VIEWS);

        secondPatternBuilder.deleteCharAt(secondPatternBuilder.length() - 1); // Remove the trailing |
        secondPatternBuilder.append(")?");

        final Pattern[] patterns = new Pattern[2];
        patterns[0] = Pattern.compile(prefixPatternBuilder.toString());
        patterns[1] = Pattern.compile(secondPatternBuilder.toString());

        return patterns;
    }

    @NonNull
    private synchronized StringBuilder getSuffixBuilder(String[] parts, StringBuilder prefixPatternBuilder, StringBuilder secondPatternBuilder) {
        StringBuilder suffixBuilder = new StringBuilder();

        for (String part : parts) {
            final String[] pair = part.split(ARROW);
            final String pair0 = pair[0].trim();
            final String pair1 = pair[1].trim();

            if (pair.length == 2 && !pair1.equals(VIEWS)) {
                prefixPatternBuilder.append(pair0).append("|");
            }

            if (pair.length == 2 && pair1.equals(VIEWS)) {
                suffixBuilder.append(pair0);
                secondPatternBuilder.append(pair0).append("\\s*").append(prefixPatternBuilder);
            }
        }
        return suffixBuilder;
    }

    private synchronized long getMultiplierValue(String[] parts, String multiplier) {
        for (String part : parts) {
            final String[] pair = part.split(ARROW);
            final String pair0 = pair[0].trim();
            final String pair1 = pair[1].trim();

            if (pair.length == 2 && pair0.equals(multiplier) && !pair1.equals(VIEWS)) {
                return Long.parseLong(pair[1].replaceAll("[^\\d]", ""));
            }
        }

        return 1L; // Default value if not found
    }
}
