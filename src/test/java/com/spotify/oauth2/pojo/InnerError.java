package com.spotify.oauth2.pojo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InnerError {
    @JsonProperty("status")
    private int status;
    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    public int getStatus() {
        return status;
    }
    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = status;
    }
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }
}
