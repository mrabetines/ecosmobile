package com.vayetek.ecosapp.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ASUS on 07/08/2017.
 */

public interface PresenceService {
    String ENDPOINT = "http://192.168.1.7/api/v1/";

    @POST("presencebyqrcode")
    Call<JsonElement> markPresence(//@Header("Authorization") String authorization,
                                   @Body JsonObject jsonObject);
}
