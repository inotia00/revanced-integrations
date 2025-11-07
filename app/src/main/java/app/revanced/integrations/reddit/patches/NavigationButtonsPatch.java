package app.revanced.integrations.reddit.patches;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.revanced.integrations.reddit.settings.Settings;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.ResourceUtils;

@SuppressWarnings("unused")
public final class NavigationButtonsPatch {
    private static Resources mResources;
    private static final Map<Object, String> navigationMap = new LinkedHashMap<>(NavigationButton.values().length);

    public static void setResources(Resources resources) {
        mResources = resources;
    }

    public static void setNavigationMap(Object object, String label) {
        for (NavigationButton button : NavigationButton.values()) {
            if (label.equals(mResources.getString(button.id)) && button.enabled) {
                navigationMap.putIfAbsent(object, label);
            }
        }
    }

    public static void hideNavigationButtons(List<Object> list, Object object) {
        if (list != null && !navigationMap.containsKey(object)) {
            list.add(object);
        }
    }

    public static List<Object> hideNavigationButtons(List<Object> list) {
        try {
            for (NavigationButton button : NavigationButton.values()) {
                if (button.enabled && list.size() > button.index) {
                    list.remove(button.index);
                }
            }
        } catch (Exception exception) {
            Logger.printException(() -> "Failed to remove button list", exception);
        }
        return list;
    }

    public static Object[] hideNavigationButtons(Object[] array) {
        try {
            for (NavigationButton button : NavigationButton.values()) {
                if (button.enabled && array.length > button.index) {
                    Object buttonObject = array[button.index];
                    array = Arrays.stream(array)
                            .filter(item -> !Objects.equals(item, buttonObject))
                            .toArray(Object[]::new);
                }
            }
        } catch (Exception exception) {
            Logger.printException(() -> "Failed to remove button array", exception);
        }
        return array;
    }

    private enum NavigationButton {
        CHAT(
                Settings.HIDE_CHAT_BUTTON.get(),
                3,
                "label_chat"
        ),
        CREATE(
                Settings.HIDE_CREATE_BUTTON.get(),
                2,
                "action_create"
        ),
        DISCOVER(
                Settings.HIDE_DISCOVER_BUTTON.get(),
                1,
                "communities_label"
        );
        private final boolean enabled;
        private final int index;
        private final int id;

        NavigationButton(boolean enabled, int index, String label) {
            this.enabled = enabled;
            this.index = index;
            this.id = ResourceUtils.getStringIdentifier(label);
        }
    }
}
