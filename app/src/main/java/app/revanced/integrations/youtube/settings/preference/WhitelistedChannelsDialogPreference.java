package app.revanced.integrations.youtube.settingsmenu;

import static app.revanced.integrations.shared.utils.StringRef.str;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.revanced.integrations.youtube.shared.VideoChannel;
import app.revanced.integrations.youtube.utils.ThemeUtils;
import app.revanced.integrations.youtube.whitelist.Whitelist;
import app.revanced.integrations.youtube.whitelist.WhitelistType;

public class WhitelistedChannelsDialogPreference extends DialogPreference {

    private ArrayList<VideoChannel> mEntries;
    private WhitelistType whitelistType;

    public WhitelistedChannelsDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WhitelistedChannelsDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WhitelistedChannelsDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WhitelistedChannelsDialogPreference(Context context) {
        super(context);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        mEntries = Whitelist.getWhitelistedChannels(whitelistType);

        Context context = getContext();
        if (mEntries.isEmpty()) {
            TextView emptyView = new TextView(context);
            emptyView.setText("\n" + str("revanced_whitelisting_empty"));
            emptyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            emptyView.setTextSize(18);
            builder.setView(emptyView);
        } else {
            builder.setView(getEntriesListView(context));
        }
    }

    private View getEntriesListView(Context context) {
        LinearLayout entriesContainer = new LinearLayout(context);
        entriesContainer.setOrientation(LinearLayout.VERTICAL);
        for (final VideoChannel entry : mEntries) {
            String channelName = entry.getChannelName();
            String channelId = entry.getChannelId();
            View entryView = getEntryView(context, channelName, v -> {
                entriesContainer.removeView(entriesContainer.findViewWithTag(channelId));
                mEntries.remove(entry);
            });
            entryView.setTag(channelId);
            entriesContainer.addView(entryView);
        }
        return entriesContainer;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && mEntries != null) {
            Whitelist.updateWhitelist(whitelistType, mEntries, getContext());
        }
    }

    private View getEntryView(Context context, CharSequence entry,
                              View.OnClickListener onDeleteClickListener) {
        int horizontalPadding = 80;

        Drawable deleteIcon = ThemeUtils.getTrashButtonDrawable();

        LinearLayout.LayoutParams entryContainerParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        entryContainerParams.setMargins(horizontalPadding, 0, horizontalPadding, 0);

        LinearLayout.LayoutParams entryLabelLayoutParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        entryLabelLayoutParams.gravity = Gravity.CENTER;

        LinearLayout entryContainer = new LinearLayout(context);
        entryContainer.setOrientation(LinearLayout.HORIZONTAL);
        entryContainer.setLayoutParams(entryContainerParams);

        TextView entryLabel = new TextView(context);
        entryLabel.setText(entry);
        entryLabel.setLayoutParams(entryLabelLayoutParams);
        entryLabel.setTextSize(18);

        ImageButton deleteButton = new ImageButton(context);
        deleteButton.setImageDrawable(deleteIcon);
        deleteButton.setOnClickListener(onDeleteClickListener);
        deleteButton.setBackground(null);

        entryContainer.addView(entryLabel);
        entryContainer.addView(deleteButton);
        return entryContainer;
    }

    public void setWhitelistType(WhitelistType whitelistType) {
        this.whitelistType = whitelistType;
    }
}
