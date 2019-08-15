package com.iqbaal.ruangadvokat.api;

import com.iqbaal.ruangadvokat.model.CaptchaResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiEndPoint {
    @POST("api/siteverify")
    @FormUrlEncoded
    Call<CaptchaResponse> captcha(@Field("secret") String secret_key,
                                  @Field("response") String response);
}
