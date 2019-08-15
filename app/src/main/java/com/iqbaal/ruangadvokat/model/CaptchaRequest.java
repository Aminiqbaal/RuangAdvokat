package com.iqbaal.ruangadvokat.model;

import com.google.gson.annotations.SerializedName;

import static com.iqbaal.ruangadvokat.helper.Global.SECRET_KEY;

public class CaptchaRequest {
    @SerializedName("secret")
    private String secret;
    @SerializedName("response")
    private String response;

    public CaptchaRequest(String response) {
        this.secret = SECRET_KEY;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
