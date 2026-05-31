package com.spotify.oauth2.utils;

import com.spotify.oauth2.api.StatusCode;
import com.spotify.oauth2.pojo.Error;
import com.spotify.oauth2.pojo.Playlist;
import io.qameta.allure.Step;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AssertUtils {
    @Step("Body assertions")
    public static void assertPlaylistEqual(Playlist responsePlaylist, Playlist requestPlaylist){
        assertThat(responsePlaylist.getName(),equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(),equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.get_public(),equalTo(requestPlaylist.get_public()));
    }
    @Step("Status code assert")
    public static void assertStatusCode(int actualStatusCode, StatusCode statusCode) {
        assertThat(actualStatusCode,equalTo(statusCode.code));

    }
    @Step("Error assertions")
    public static void assertError(Error responseError, StatusCode statusCode) {
        assertThat(responseError.getError().getStatus(),equalTo(statusCode.code));
        assertThat(responseError.getError().getMessage(),equalTo(statusCode.message));
    }
    @Step("Create a playlist body")
    public static Playlist playlistBuilder(String name, String description, boolean _public){
        return Playlist.builder()
               .name(name)
              .description(description)
                ._public(_public)
                .build();
    }
}
