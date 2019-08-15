package com.iqbaal.ruangadvokat.model;

import android.util.ArrayMap;

import com.google.gson.annotations.SerializedName;

public class CaptchaResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("error-codes")
    private String[] error_codes;

    public boolean getSuccess() {
        return success;
    }

    public String[] getError_codes() {
        return error_codes;
    }
}
