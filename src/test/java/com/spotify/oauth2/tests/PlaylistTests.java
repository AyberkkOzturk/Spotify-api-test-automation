package com.spotify.oauth2.tests;
import com.spotify.oauth2.api.applicationApi.PlaylistApi;
import com.spotify.oauth2.pojo.Error;
import com.spotify.oauth2.pojo.Playlist;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PlaylistTests {

    @Test
    public void ShouldBeAbleToCreatePlaylist() {
        Playlist requestPlaylist = new Playlist()
                .setName("New Playlist")
                .setDescription("New playlist description")
                .setPublic(false);

        Response response = PlaylistApi.post(requestPlaylist);
        assertThat(response.statusCode(),equalTo(201));

        Playlist responsePlaylist = response.as(Playlist.class);

        assertThat(responsePlaylist.getName(),equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(),equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.getPublic(),equalTo(requestPlaylist.getPublic()));

    }
    @Test
    public void ShouldBeAbleToGetAPlaylist() {
        Playlist requestPlaylist = new Playlist()
                .setName("Update Playlist Name")
                .setDescription("Update playlist description")
                .setPublic(true);
        Response response = PlaylistApi.get("4F2I7431e2tKJ6CYAU3WEt");
        assertThat(response.statusCode(),equalTo(200));

        Playlist responsePlaylist = response.as(Playlist.class);

        assertThat(responsePlaylist.getName(),equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(),equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.getPublic(),equalTo(requestPlaylist.getPublic()));

    }

    @Test
    public void ShouldBeAbleToUpdateAPlaylist() {
        Playlist requestPlaylist = new Playlist()
                .setName("Update Playlist Name")
                .setDescription("Update playlist description")
                .setPublic(false);

        Response response = PlaylistApi.update("4F2I7431e2tKJ6CYAU3WEt",requestPlaylist);
        assertThat(response.statusCode(),equalTo(200));

    }
    @Test
    public void ShouldNotBeAbleToCreatePlaylistWithName() {
        Playlist requestPlaylist = new Playlist()
                .setName("")
                .setDescription("New playlist description")
                .setPublic(false);
        Response response = PlaylistApi.post(requestPlaylist);
        assertThat(response.statusCode(),equalTo(400));
        Error error = response.as(Error.class);

        assertThat(error.getError().getStatus(),equalTo(400));
        assertThat(error.getError().getMessage(),equalTo("Missing required field: name"));

    }
    @Test
    public void ShouldNotBeAbleToCreatePlaylistWithExpiredToken() {
        String invalid_token = "12312asdasd123123";
        Playlist requestPlaylist = new Playlist()
                .setName("New Playlist")
                .setDescription("New playlist description")
                .setPublic(false);

        Response response = PlaylistApi.post(invalid_token,requestPlaylist);
        assertThat(response.statusCode(),equalTo(401));
        Error error = response.as(Error.class);


        assertThat(error.getError().getStatus(),equalTo(401));
        assertThat(error.getError().getMessage(),equalTo("Invalid access token"));

    }

}
