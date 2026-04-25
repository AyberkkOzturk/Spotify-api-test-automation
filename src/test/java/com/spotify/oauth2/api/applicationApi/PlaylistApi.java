package com.spotify.oauth2.api.applicationApi;
import com.spotify.oauth2.api.RestResource;
import com.spotify.oauth2.pojo.Playlist;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import static com.spotify.oauth2.api.Route.PLAYLISTS;
import static com.spotify.oauth2.api.Route.USER;
import static com.spotify.oauth2.api.TokenManager.getToken;
public class PlaylistApi {
    @Step("Post request generator")
    public static Response post (Playlist requestPlaylist) {
        return RestResource.post(USER + PLAYLISTS,getToken(),requestPlaylist);

    }
    @Step("Post request generator but invalid token")
    public static Response post (String token,Playlist requestPlaylist) {
        return RestResource.post(USER + PLAYLISTS,token,requestPlaylist);

    }
    @Step("Get request generator")
    public static Response get (String playlistId) {
        return RestResource.get(PLAYLISTS + "/" + playlistId,getToken());
    }

    @Step("Put request generator")
    public static Response update(String playlistId,Playlist requestPlaylist) {
        return RestResource.update(PLAYLISTS + "/" + playlistId,getToken(),requestPlaylist);

    }
}
