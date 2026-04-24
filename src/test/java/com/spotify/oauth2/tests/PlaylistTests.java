package com.spotify.oauth2.tests;
import com.spotify.oauth2.api.applicationApi.PlaylistApi;
import com.spotify.oauth2.pojo.Error;
import com.spotify.oauth2.pojo.Playlist;
import com.spotify.oauth2.utils.DataLoader;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PlaylistTests {


    @Test
    public void ShouldBeAbleToCreatePlaylist() {
        Playlist requestPlaylist = playlistBuilder("New Playlist","New playlist description",false);
        Response response = PlaylistApi.post(requestPlaylist);
        assertStatusCode(response.statusCode(), 201);
        Playlist responsePlaylist = response.as(Playlist.class);
        assertPlaylistEqual(responsePlaylist,requestPlaylist);
    }
    @Test
    public void ShouldBeAbleToGetAPlaylist() {
        Playlist requestPlaylist = playlistBuilder("Update Playlist Name","Update playlist description",true);
        Response response = PlaylistApi.get(DataLoader.getInstance().getPlaylistId());
        assertStatusCode(response.statusCode(), 200);
        Playlist responsePlaylist = response.as(Playlist.class);
        assertPlaylistEqual(responsePlaylist,requestPlaylist);

    }

    @Test
    public void ShouldBeAbleToUpdateAPlaylist() {
        Playlist requestPlaylist = playlistBuilder("Update Playlist Name","Update playlist description",true);
        Response response = PlaylistApi.update(DataLoader.getInstance().getPlaylistId(), requestPlaylist);
        assertStatusCode(response.statusCode(), 200);
    }
    @Test
    public void ShouldNotBeAbleToCreatePlaylistWithName() {
        Playlist requestPlaylist = playlistBuilder("","New playlist description",false);
        Response response = PlaylistApi.post(requestPlaylist);
        assertStatusCode(response.statusCode(),400);
        Error error = response.as(Error.class);
        assertError(error,400,"Missing required field: name");
    }
    @Test
    public void ShouldNotBeAbleToCreatePlaylistWithExpiredToken() {
        String invalid_token = "12312asdasd123123";
        Playlist requestPlaylist = playlistBuilder("New Playlist","New playlist description",false);
        Response response = PlaylistApi.post(invalid_token,requestPlaylist);
        assertStatusCode(response.statusCode(),401);
        Error error = response.as(Error.class);
        assertError(error,401,"Invalid access token");
    }
    public Playlist playlistBuilder(String name, String description, boolean _public){
        return new Playlist()
                .setName(name)
                .setDescription(description)
                .setPublic(_public);
    }
    public void assertPlaylistEqual(Playlist responsePlaylist,Playlist requestPlaylist){
        assertThat(responsePlaylist.getName(),equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(),equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.getPublic(),equalTo(requestPlaylist.getPublic()));
    }
    public void assertStatusCode(int actualStatusCode,int expectedStatusCode) {
        assertThat(actualStatusCode,equalTo(expectedStatusCode));

    }
    public void assertError(Error responseError, int expectedStatusCode,String expectedMsg) {
        assertThat(responseError.getError().getStatus(),equalTo(expectedStatusCode));
        assertThat(responseError.getError().getMessage(),equalTo(expectedMsg));
    }
}
