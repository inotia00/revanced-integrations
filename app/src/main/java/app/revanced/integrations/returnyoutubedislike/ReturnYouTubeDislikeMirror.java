package app.revanced.integrations.returnyoutubedislike;

import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.addSpanStyling;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.formatDislikeCount;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.formatDislikePercentage;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.newSpanUsingStylingOfAnotherSpan;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.segmentedLeftSeparatorFontRatio;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.segmentedLeftSeparatorHorizontalScaleRatio;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.segmentedLeftSeparatorVerticalShiftRatio;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.segmentedVerticalShiftRatio;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.setSegmentedAdjustmentValues;
import static app.revanced.integrations.returnyoutubedislike.ReturnYouTubeDislike.stringContainsNumber;
import static app.revanced.integrations.utils.StringRef.str;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;

import java.util.concurrent.atomic.AtomicReference;

import app.revanced.integrations.returnyoutubedislike.requests.ReturnYouTubeDislikeMirrorApi;
import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.LogHelper;
import app.revanced.integrations.utils.ReVancedUtils;
import app.revanced.integrations.utils.ThemeHelper;

public class ReturnYouTubeDislikeMirror {
    private static String currentVideoId;
    public static Long likeCount;
    public static Long dislikeCount;
    public static Float dislikePercentage;

    private static Thread _dislikeFetchThread = null;

    public static void newVideoLoaded(String videoId) {
        if (videoId == null || videoId.equals(currentVideoId)) return;

        likeCount = null;
        dislikeCount = null;
        dislikePercentage = null;

        currentVideoId = videoId;

        try {
            if (_dislikeFetchThread != null && _dislikeFetchThread.getState() != Thread.State.TERMINATED) {
                _dislikeFetchThread.interrupt();
            }
        } catch (Exception ex) {
            LogHelper.printException(ReturnYouTubeDislikeMirror.class, "Error in the dislike fetch thread", ex);
        }

        _dislikeFetchThread = new Thread(() -> ReturnYouTubeDislikeMirrorApi.fetchDislikes(videoId));
        _dislikeFetchThread.start();
    }

    public static void onComponentCreated(Object conversionContext, AtomicReference<Object> textRef) {
        try {
            String conversionContextString = conversionContext.toString();

            final boolean isSegmentedButton;
            if (conversionContextString.contains("|segmented_like_dislike_button.eml|") &&
                    conversionContextString.contains("|TextType|"))
                isSegmentedButton = true;
            else if (conversionContextString.contains("|dislike_button.eml|"))
                isSegmentedButton = false;
            else
                return;

            updateDislike(textRef, isSegmentedButton);
        } catch (Exception ex) {
            LogHelper.printException(ReturnYouTubeDislikeMirror.class, "Error while trying to set dislikes text", ex);
        }
    }

    private static void updateDislike(AtomicReference<Object> textRef, boolean isSegmentedButton) {
        Spannable oldSpannable = (Spannable) textRef.get();
        String oldLikesString = oldSpannable.toString();
        Spannable replacementSpannable;

        if (!isSegmentedButton) {
            // simple replacement of 'dislike' with a number/percentage
            if (stringContainsNumber(oldLikesString)) {
                // already is a number, and was modified in a previous call to this method
                return;
            }
            replacementSpannable = newSpannableWithDislikes(oldSpannable);
        } else {
            String leftSegmentedSeparatorString = ReVancedUtils.isRightToLeftTextLayout() ? "\u200F|  " : "|  ";

            if (oldLikesString.contains(leftSegmentedSeparatorString)) {
                return; // dislikes was previously added
            }

            if (!stringContainsNumber(oldLikesString)) {

                String hiddenMessageString = str("revanced_ryd_video_likes_hidden_by_video_owner");
                if (hiddenMessageString.equals(oldLikesString)) {
                    return;
                }
                replacementSpannable = newSpanUsingStylingOfAnotherSpan(oldSpannable, hiddenMessageString);
            } else {
                Spannable likesSpan = newSpanUsingStylingOfAnotherSpan(oldSpannable, oldLikesString);

                // left and middle separator
                String middleSegmentedSeparatorString = "  •  ";
                Spannable leftSeparatorSpan = newSpanUsingStylingOfAnotherSpan(oldSpannable, leftSegmentedSeparatorString);
                Spannable middleSeparatorSpan = newSpanUsingStylingOfAnotherSpan(oldSpannable, middleSegmentedSeparatorString);
                final int separatorColor = ThemeHelper.getDayNightTheme()
                        ? 0x37A0A0A0  // transparent dark gray
                        : 0xFFD9D9D9; // light gray
                addSpanStyling(leftSeparatorSpan, new ForegroundColorSpan(separatorColor));
                addSpanStyling(middleSeparatorSpan, new ForegroundColorSpan(separatorColor));
                CharacterStyle noAntiAliasingStyle = new CharacterStyle() {
                    @Override
                    public void updateDrawState(TextPaint tp) {
                        tp.setAntiAlias(false); // draw without anti-aliasing, to give a sharper edge
                    }
                };
                addSpanStyling(leftSeparatorSpan, noAntiAliasingStyle);
                addSpanStyling(middleSeparatorSpan, noAntiAliasingStyle);

                Spannable dislikeSpan = newSpannableWithDislikes(oldSpannable);

                // Increase the size of the left separator, so it better matches the stock separator on the right.
                // But when using a larger font, the entire span (including the like/dislike text) becomes shifted downward.
                // To correct this, use additional spans to move the alignment back upward by a relative amount.
                setSegmentedAdjustmentValues();
                class RelativeVerticalOffsetSpan extends CharacterStyle {
                    final float relativeVerticalShiftRatio;

                    RelativeVerticalOffsetSpan(float relativeVerticalShiftRatio) {
                        this.relativeVerticalShiftRatio = relativeVerticalShiftRatio;
                    }

                    @Override
                    public void updateDrawState(TextPaint tp) {
                        tp.baselineShift -= (int) (relativeVerticalShiftRatio * tp.getFontMetrics().top);
                    }
                }

                // shift everything up, to compensate for the vertical movement caused by the font change below
                // each section needs it's own Relative span, otherwise alignment is wrong
                addSpanStyling(leftSeparatorSpan, new RelativeVerticalOffsetSpan(segmentedLeftSeparatorVerticalShiftRatio));

                addSpanStyling(likesSpan, new RelativeVerticalOffsetSpan(segmentedVerticalShiftRatio));
                addSpanStyling(middleSeparatorSpan, new RelativeVerticalOffsetSpan(segmentedVerticalShiftRatio));
                addSpanStyling(dislikeSpan, new RelativeVerticalOffsetSpan(segmentedVerticalShiftRatio));

                // important: must add size scaling after vertical offset (otherwise alignment gets off)
                addSpanStyling(leftSeparatorSpan, new RelativeSizeSpan(segmentedLeftSeparatorFontRatio));
                addSpanStyling(leftSeparatorSpan, new ScaleXSpan(segmentedLeftSeparatorHorizontalScaleRatio));
                // middle separator does not need resizing

                // put everything together
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(leftSeparatorSpan);
                builder.append(likesSpan);
                builder.append(middleSeparatorSpan);
                builder.append(dislikeSpan);
                replacementSpannable = new SpannableString(builder);
            }
        }

        textRef.set(replacementSpannable);
    }

    private static Spannable newSpannableWithDislikes(Spannable sourceStyling) {
        return newSpanUsingStylingOfAnotherSpan(sourceStyling,
                SettingsEnum.RYD_SHOW_DISLIKE_PERCENTAGE.getBoolean()
                        ? formatDislikePercentage(dislikePercentage)
                        : formatDislikeCount(dislikeCount));
    }

    public static void setValues(Long like, Long dislike, Float percentage) {
        likeCount = like;
        dislikeCount = dislike;
        dislikePercentage = percentage;
    }
}
