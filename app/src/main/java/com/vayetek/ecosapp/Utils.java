package com.vayetek.ecosapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vayetek.ecosapp.models.NoteModelRequest;
import com.vayetek.ecosapp.models.NoteModelRequestArray;
import com.vayetek.ecosapp.services.EcosApiRetrofitServices;
import com.vayetek.ecosapp.services.PresenceService;
import com.vayetek.ecosapp.services.UniversityRetrofitServices;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    private static final int TIME_OUT = 10000;
    public static String token;

    public static void saveToken(Context context, String token) {
        context.getSharedPreferences("ecos_prefs", Context.MODE_PRIVATE).edit().putString("token", token).apply();
    }

    public static boolean isUserAuthenticated(Context context) {
        return context.getSharedPreferences("ecos_prefs", Context.MODE_PRIVATE).getString("token", null) != null;
    }

    private static String retreiveToken(Context context) {
        return context.getSharedPreferences("ecos_prefs", Context.MODE_PRIVATE).getString("token", null);
    }

    public static String getAuthorization(Context context) {
        return "Bearer " + retreiveToken(context);
    }

    public static void saveObserver(Context context, int id) {
        SharedPreferences.Editor editor = context.getSharedPreferences("ecos_observer_id", Context.MODE_APPEND).edit();
        editor.remove("id");
        context.getSharedPreferences("ecos_observer_id", Context.MODE_APPEND).edit().putInt("id", id);
    }

    public static int getObserverId(Context context) {
        return context.getSharedPreferences("ecos_observer_id", ContextThemeWrapper.MODE_PRIVATE).getInt("id", 0);
    }

    public static void saveRequest(Context context, String id_Student, int id_Station, String id_Enseignant, String id_Patient, int resultatCode, int[] notes) throws JSONException {
        NoteModelRequest noteModelRequest = new NoteModelRequest(id_Student, id_Patient, id_Enseignant, id_Station, notes, resultatCode);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();
        NoteModelRequestArray noteModelRequestArray = getNoteRequests(context);
        if (noteModelRequestArray == null) {
            noteModelRequestArray = new NoteModelRequestArray();
        }
        noteModelRequestArray.getNoteModelRequests().add(noteModelRequest);
        Gson gson = new Gson();
        editor.putString("NoteModelRequestArray", gson.toJson(noteModelRequestArray));
        Log.d("NoteModelRequestArray", "saveRequest: " + gson.toJson(noteModelRequestArray));
        editor.apply();

    }

    public static NoteModelRequestArray getNoteRequests(Context context) throws JSONException {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String json = preferences.getString("NoteModelRequestArray", null);
        Gson gson = new Gson();

        return gson.fromJson(json, NoteModelRequestArray.class);
    }

    public static void deleteRequestes(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("NoteModelRequestArray");
        editor.apply();
    }

    //Ecos Api Retrofit Instance
    private static EcosApiRetrofitServices ecosApiRetrofitServicesInstance;

    public static EcosApiRetrofitServices getEcosApiRetrofitServicesInstance() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (ecosApiRetrofitServicesInstance == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(EcosApiRetrofitServices.ENDPOINT)
                    .client(new OkHttpClient().newBuilder().connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS).addInterceptor(interceptor).build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            ecosApiRetrofitServicesInstance = retrofit.create(EcosApiRetrofitServices.class);
        }
        return ecosApiRetrofitServicesInstance;
    }

    //University Api Retrofit Instance
    private static UniversityRetrofitServices universityRetrofitServices;

    public static UniversityRetrofitServices getUniversityRetrofitServices() {
        if (universityRetrofitServices == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UniversityRetrofitServices.ENDPOINT)
                    .client(new OkHttpClient().newBuilder().connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            universityRetrofitServices = retrofit.create(UniversityRetrofitServices.class);
        }
        return universityRetrofitServices;
    }

    //Presence Service Instance
    private static PresenceService presenceService;

    public static PresenceService getPresenceService() {
        if (presenceService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(PresenceService.ENDPOINT)
                    .client(new OkHttpClient().newBuilder().connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            presenceService = retrofit.create(PresenceService.class);
        }
        return presenceService;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
