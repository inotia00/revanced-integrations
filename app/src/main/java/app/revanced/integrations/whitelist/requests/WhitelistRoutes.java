package app.revanced.integrations.whitelist.requests;

import static app.revanced.integrations.requests.Route.Method.GET;

import app.revanced.integrations.requests.Route;

public class WhitelistRoutes {
    public static final Route GET_CHANNEL_DETAILS = new Route(GET, "videos?part=snippet&key=AIzaSyBYZEMh_dzM31b1zpiHdCRdZAHfUhYFjaE&id={video_id}");

    private WhitelistRoutes() {
    }
}
