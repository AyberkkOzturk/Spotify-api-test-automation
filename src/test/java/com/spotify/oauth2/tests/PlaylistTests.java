package com.spotify.oauth2.tests;

import com.spotify.oauth2.api.StatusCode;
import com.spotify.oauth2.api.applicationApi.PlaylistApi;
import com.spotify.oauth2.pojo.Error;
import com.spotify.oauth2.pojo.Playlist;
import com.spotify.oauth2.utils.DataLoader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.spotify.oauth2.utils.AssertUtils.*;
import static com.spotify.oauth2.utils.FakerUtils.generateDescription;
import static com.spotify.oauth2.utils.FakerUtils.generateName;

@Epic("Spotify Oauth 2.0")
@Feature("Playlist API")
public class PlaylistTests extends BaseTest {

    @Story("Create a playlist story")
    @Description("Should be able to create a empty playlist")
    @Test(description = "should be able to create a playlist")
    public void ShouldBeAbleToCreateAPlaylist() {
        Playlist requestPlaylist = playlistBuilder(generateName(),generateDescription(),false);
        Response response = PlaylistApi.post(requestPlaylist);
        assertStatusCode(response.statusCode(), StatusCode.CODE_201);
        assertPlaylistEqual(response.as(Playlist.class),requestPlaylist);
    }

    @Story("Update a playlist story")
    @Description("Should be able to update a empty playlist")
    @Test(description = "should be able to update a playlist")
    public void ShouldBeAbleToUpdateAPlaylist() {
        Playlist requestPlaylist = playlistBuilder(generateName(),generateDescription(),true);
        Response response = PlaylistApi.update(DataLoader.getInstance().getPlaylistId(), requestPlaylist);
        assertStatusCode(response.statusCode(), StatusCode.CODE_200);
    }

    @Story("Get a playlist story")
    @Description("Should be able to get a empty playlist")
    @Test(description = "should be able to get a playlist")
    public void ShouldBeAbleToGetAPlaylist() {
        Playlist requestPlaylist = playlistBuilder(generateName(), generateDescription(), true);
        Response postResponse = PlaylistApi.post(requestPlaylist);
        assertStatusCode(postResponse.statusCode(), StatusCode.CODE_201);
        String createdPlaylistId = postResponse.as(Playlist.class).getId();
        Response getResponse = PlaylistApi.get(createdPlaylistId);
        assertStatusCode(getResponse.statusCode(), StatusCode.CODE_200);
        assertPlaylistEqual(getResponse.as(Playlist.class), requestPlaylist);

    }

    @Story("Create a playlist story")
    @Description("Should not be able to create playlist with name")
    @Test(description = "should not be able to create playlist with name")
    public void ShouldNotBeAbleToCreatePlaylistWithName() {
        Playlist requestPlaylist = playlistBuilder("",generateDescription(),false);
        Response response = PlaylistApi.post(requestPlaylist);
        assertStatusCode(response.statusCode(),StatusCode.CODE_400);
        assertError(response.as(Error.class),StatusCode.CODE_400);
    }

    @Story("Create a playlist story")
    @Description("Should not be able to create playlist with expired token")
    @Test(description = "should not be able to create playlist with expired token")
    public void ShouldNotBeAbleToCreatePlaylistWithExpiredToken() {
        String invalid_token = "12312asdasd123123";
        Playlist requestPlaylist = playlistBuilder(generateName(),generateDescription(),false);
        Response response = PlaylistApi.post(invalid_token,requestPlaylist);
        assertStatusCode(response.statusCode(),StatusCode.CODE_401);
        assertError(response.as(Error.class),StatusCode.CODE_401);
    }
}
