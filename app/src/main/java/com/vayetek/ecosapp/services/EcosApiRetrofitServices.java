package com.vayetek.ecosapp.services;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.vayetek.ecosapp.models.Examen;
import com.vayetek.ecosapp.models.Item;
import com.vayetek.ecosapp.models.Session;
import com.vayetek.ecosapp.models.Station;
import com.vayetek.ecosapp.models.University;

public interface EcosApiRetrofitServices {

    String ENDPOINT = "http://137.74.165.25:8082/api/";
    //String ENDPOINT = "http://192.168.152.1/api/";

    @GET("mobile/session/showActifSessions")
    Call<List<Session>> listSessions(@Header("Authorization") String authorization) throws RuntimeException;

    @GET("mobile/session/{id_Session}/examen")
    Call<List<Examen>> listExamens(@Header("Authorization") String authorization, @Path("id_Session") int id_Session) throws RuntimeException;

    @GET("mobile/session/{id_Session}/examen/{id_Examen}/station")
    Call<List<Station>> listStations(@Header("Authorization") String authorization, @Path("id_Session") int id_Session, @Path("id_Examen") int id_Examen) throws RuntimeException;

    @GET("mobile/session/{id_Session}/examen/{id_Examen}/station/{id_Station}/grille")
    Call<List<Item>> listItems(@Header("Authorization") String authorization, @Path("id_Session") int id_Session, @Path("id_Examen") int id_Examen, @Path("id_Station") int id_Station) throws RuntimeException;

    @FormUrlEncoded
    @POST("mobile/passStation/{id_Station}")
    Call<ResponseBody> storeNote(@Header("Authorization") String authorization,
                            @Field("studentCIN") String studentCIN, @Path("id_Station") int id_Station, @Field("inputs[]") int[] notes,
                            @Field("resultat") int resultatCode) throws RuntimeException;

    @FormUrlEncoded
    @POST("signin")
    Call<ResponseBody> signin(@Field("login") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST("mobile/signin/qr")
    Call<ResponseBody> qrSignin(@Field("QRCode") String login);

    @GET("/mobile/check-if-student-can-pass-the-exam/{studentCIN}/{examId}")
    Call<Integer> checkIfStudentCanPassTheExam(@Header("Authorization") String authorization, @Path("studentCIN") String stduentId, @Path("examId") String examId);

    @FormUrlEncoded
    @POST("mobile/checkCanPassStation")
    Call<ResponseBody> checkCanPassStation(@Header("Authorization") String authorization,
                                           @Field("studentCIN") String studentCIN,
                                           @Field("studentQRcode") String studentQRcode,
                                           @Field("stationId") int stationId,
                                           @Field("patientCIN") String patientCIN,
                                           @Field("enseigantId") int enseigantId);

    @FormUrlEncoded
    @POST("/mobile/cin-by-qrcode")
    Call<String> getStudentCIN(@Header("Authorization") String authorization, @Field("QRCode") String params);

    @GET("mobile/universities")
    Call<List<University>> getListUniversities();

    @GET("mobile/session/{sessionId}/checkIfMedObSession")
    Call<ResponseBody> checkIfMedCanPassSession(@Header("Authorization") String authorization, @Path("sessionId") int sessionId);


}