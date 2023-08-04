package app.revanced.integrations.whitelist.requests;

import static app.revanced.integrations.requests.Route.Method.GET;
import app.revanced.integrations.settings.SettingsEnum;

import app.revanced.integrations.requests.Route;

public class WhitelistRoutes {
    private static final String api_key = SettingsEnum.WHITELIST_API_KEY.getString();
    
    public static final Route GET_CHANNEL_DETAILS = new Route(GET, "videos?part=snippet&id={video_id}&key=" + api_key);

    private WhitelistRoutes() {
    }
}
