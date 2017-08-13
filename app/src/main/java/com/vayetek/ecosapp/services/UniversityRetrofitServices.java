package com.vayetek.ecosapp.services;


import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import com.vayetek.ecosapp.models.University;

public interface UniversityRetrofitServices {

    String ENDPOINT = "http://137.74.165.25:5000/api/";


    @GET("universities")
    Call<List<University>> getListUniversities();

    @POST("universities/signin")
    Call<ResponseBody> universitySingIn(@Body JsonObject jsonObject);

}