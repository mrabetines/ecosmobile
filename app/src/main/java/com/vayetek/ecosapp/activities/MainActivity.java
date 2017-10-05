package com.vayetek.ecosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.Utils;
import com.vayetek.ecosapp.adapters.ExamAdapter;
import com.vayetek.ecosapp.fragments.SessionFragment;
//added by ines
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.vayetek.ecosapp.services.PresenceService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity /*implements ExamAdapter.OnLongClickAdapter*/{
    PresenceService presenceService;
    private int variable;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragment(SessionFragment.newInstance());
        presenceService = Utils.getPresenceService();
    }

    public void showFragment(Fragment currentFragment) {
        if (currentFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, currentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void setVariable(int variable)
    {
        this.variable=variable;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            //result.toString();
            if (result.getContents() == null) {
                Toast.makeText(this, "pas de resultat", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                    String qr_code= result.getContents().toString();
                    //Log.d("onActivityResult", qr_code );
                    JsonObject postParams = new JsonObject();
                    postParams.addProperty("qr_code", qr_code);
                Log.d("id_Examen",this.variable+"");
                    postParams.addProperty("id_Examen", this.variable);
                    Call<JsonElement> call = presenceService.markPresence(postParams);
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                            if (response.code() != 200) {
                                Toast.makeText(MainActivity.this, "erreur", Toast.LENGTH_LONG).show();
                                return;
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                String message = jsonObject.getString("result");
                                Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                            } catch (JSONException e /*| /*IOException e*/) {

                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "erreur", Toast.LENGTH_LONG).show();

                            }

                        }
                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "erreur", Toast.LENGTH_LONG).show();



                        }
                    });
        }}
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

   /* @Override
    public void onLongClickAdapter(int id_Examen) {
        Log.d("id_Examen Listner",id_Examen+"");
        this.variable = id_Examen;
    }*/
}
