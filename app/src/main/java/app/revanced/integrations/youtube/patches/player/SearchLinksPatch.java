package app.revanced.integrations.youtube.patches.player;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;

import androidx.annotation.NonNull;

import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.youtube.settings.Settings;

@SuppressWarnings("unused")
public class SearchLinksPatch {
    private static final boolean HIDE_COMMENT_HIGHLIGHTED_SEARCH_LINKS = Settings.HIDE_COMMENT_HIGHLIGHTED_SEARCH_LINKS.get();

    /**
     * Located in front of the search icon.
     */
    private static final String WORD_JOINER_CHARACTER = "\u2060";

    /**
     * It doesn't seem necessary to use ThreadLocal.
     */
    private static final ThreadLocal<String> conversionContextThreadLocal = new ThreadLocal<>();

    /**
     * RelativeSizeSpan to be applied to the font assigned for the search icon, with a size of 0.
     */
    private static final RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0f);


    /**
     * Injection point.
     *
     * @param conversionContext ConversionContext is used to identify whether it is a comment thread or not.
     */
    public static CharSequence setConversionContext(@NonNull Object conversionContext,
                                                 @NonNull CharSequence original) {
        conversionContextThreadLocal.set(conversionContext.toString());
        return original;
    }

    /**
     * Injection point.
     *
     * @param original  Original SpannableString.
     * @param object    Span such as {@link ClickableSpan}, {@link ForegroundColorSpan},
     *                  {@link AbsoluteSizeSpan}, {@link TypefaceSpan}, {@link ImageSpan}.
     * @param start     Start index of {@link Spannable#setSpan(Object, int, int, int)}.
     * @param end       End index of {@link Spannable#setSpan(Object, int, int, int)}.
     * @param flags     Flags of {@link Spannable#setSpan(Object, int, int, int)}.
     */
    public static void hideSearchLinks(SpannableString original, Object object,
                                       int start, int end, int flags) {
        try {
            if (HIDE_COMMENT_HIGHLIGHTED_SEARCH_LINKS &&
                    isCommentThread() &&
                    isWords(original, start, end) &&
                    isSearchLinks(original, end)) {
                if (object instanceof ImageSpan) {
                    Logger.printDebug(() -> "Hide the search icon by setting the font size to 0");
                    original.setSpan(relativeSizeSpan, start, end, flags);
                    return;
                }
                Logger.printDebug(() -> "Remove search link by skipping setSpan");
                return;
            }
        } catch (Exception ex) {
            Logger.printException(() -> "hideSearchLinks failure", ex);
        }
        original.setSpan(object, start, end, flags);
    }

    /**
     * @return Whether it was invoked in a comment thread or not.
     */
    private static boolean isCommentThread() {
        String conversionContext = conversionContextThreadLocal.get();
        return conversionContext != null &&
                conversionContext.contains("|comment.");
    }

    /**
     * @return Whether Span applies to a word or not.
     */
    private static boolean isWords(SpannableString original, int start, int end) {
        return start != 0 || end != original.length();
    }

    /**
     * @return Whether the word contains a search icon or not.
     */
    private static boolean isSearchLinks(SpannableString original, int end) {
        String originalString = original.toString();
        int wordJoinerIndex = originalString.indexOf(WORD_JOINER_CHARACTER);
        // There may be more than one highlight keyword in the comment.
        // Check the index of all highlight keywords.
        while (wordJoinerIndex != -1) {
            if (end - wordJoinerIndex == 2) return true;
            wordJoinerIndex = originalString.indexOf(WORD_JOINER_CHARACTER, wordJoinerIndex + 1);
        }
        return false;
    }
}
