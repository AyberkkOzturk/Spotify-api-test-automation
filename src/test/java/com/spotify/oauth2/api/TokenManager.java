package com.spotify.oauth2.api;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.HashMap;
public class TokenManager {
    private static String access_token;
    private static Instant expiry_time;

    public static String getToken() {
        try {
            if (access_token == null || Instant.now().isAfter(expiry_time)){
                System.out.println("Renewing Token ...");
                Response response = renewToken();
                access_token = response.path("access_token");
                int expiryDuration = response.path("expires_in");
                expiry_time = Instant.now().plusSeconds(expiryDuration - 300);
            }else{
                System.out.println("Token is to use");
            }
    } catch (Exception e) {
            throw new RuntimeException("Failed to get token!!!");
        }
        return access_token;
    }

    private static Response renewToken() {
        HashMap<String,String> formParams = new HashMap<String, String>();
        formParams.put("client_id","2eda8e2224f442e3a59f2ac52b2cf22a");
        formParams.put("client_secret","d5b5380d05ae40659f99a38424ae468b");
        formParams.put("refresh_token","AQD0mt_I_Hv91LnNG3qGSc97EOUEIWwbia9NTqeOOL7w0Neytk6pk5RM1qlCtrwcoL4nMIex-aiYQ-N4mPj95jsj9mV3dcyKy8pgaxcFgEWINDxVmjxeK2YnlBEV4qoog6I");
        formParams.put("grant_type","refresh_token");

        Response response = RestResource.postAccount(formParams);

        if (response.statusCode() != 200){
            throw new RuntimeException("Renew Token Failed");
        }
        return response;
    }
}
