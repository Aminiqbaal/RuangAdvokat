package com.iqbaal.ruangadvokat.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.iqbaal.ruangadvokat.helper.Global.BASE_URL;

public class ApiService {

    public static ApiEndPoint getService() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiEndPoint.class);
    }
}
