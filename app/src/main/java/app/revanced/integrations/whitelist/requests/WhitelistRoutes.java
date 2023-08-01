package app.revanced.integrations.whitelist.requests;

import static app.revanced.integrations.requests.Route.Method.GET;

import app.revanced.integrations.requests.Route;

public class WhitelistRoutes {
    public static final Route GET_CHANNEL_DETAILS = new Route(GET, "videos?part=snippet&id={video_id}&key=AIzaSyC28h1S_kId35V6n0wR749yLrndF0yZyXM");

    private WhitelistRoutes() {
    }
}