package com.vayetek.ecosapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.Utils;
import com.vayetek.ecosapp.fragments.GrilleActivity;
import com.vayetek.ecosapp.services.EcosApiRetrofitServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthMedecinObserverActivity extends AppCompatActivity implements View.OnClickListener {
    Button sigin_btn;
    EditText login_edit;
    EditText passwrod_edit;
    EditText patien_simule;
    EcosApiRetrofitServices ecosApiRetrofitServices;
    private int idSession;
    private int idExam;
    private int idStation;
    private ProgressDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_observer);
        idSession = getIntent().getIntExtra("idSession", 0);
        idExam = getIntent().getIntExtra("idExam", 0);
        idStation = getIntent().getIntExtra("idStation", 0);
        loadingDialog = loadingDialog("Chargement...");

        sigin_btn = (Button) findViewById(R.id.signin_btn);
        Button qr_sigin_btn = (Button) findViewById(R.id.qr_signin_btn);
        login_edit = (EditText) findViewById(R.id.login_edit);
        passwrod_edit = (EditText) findViewById(R.id.password_edit);
        patien_simule = (EditText) findViewById(R.id.patien_simule);
        ecosApiRetrofitServices = Utils.getEcosApiRetrofitServicesInstance();

        sigin_btn.setOnClickListener(this);
        qr_sigin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanneQR();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (login_edit.getText().toString().isEmpty() ||
                passwrod_edit.getText().toString().isEmpty()) {
            //|| patien_simule.getText().toString().isEmpty()
            Toast.makeText(this, "Veuillez saisir votre Login, Password et CIN du patient simulé", Toast.LENGTH_LONG).show();
        } else {
            signin(login_edit.getText().toString(), passwrod_edit.getText().toString());
        }

    }

    public void scanneQR() {
        /*
        if (patien_simule.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez saisir le cin du patient simulé", Toast.LENGTH_LONG).show();
            return;
        }
        */
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
            loadingDialog.show();

            Call<ResponseBody> call = ecosApiRetrofitServices.qrSignin(contents);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    loadingDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String token = jsonObject.getString("token");
                        final int idObserver = jsonObject.getJSONObject("enseignant").getInt("id_Enseignant");
                        Log.d("idObserver", "onResponse: " + idObserver);
                        JSONArray privileges = jsonObject.getJSONObject("enseignant").getJSONArray("privileges");
                        boolean isObserver = false;
                        for (int i = 0; i < privileges.length(); i++) {
                            int idPrivilege = privileges.getJSONObject(i).getInt("id_Privilege");
                            if (idPrivilege == 6) {
                                isObserver = true;
                            }
                        }
                        if (!isObserver) {
                            Toast.makeText(AuthMedecinObserverActivity.this, "Cet enseignant n'est pas un observateur !", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (token.length() > 0) {
                            //Utils.token = token;
                            // Utils.saveToken(AuthMedecinObserverActivity.this, token);
                            Call<ResponseBody> checkMed = ecosApiRetrofitServices.checkIfMedCanPassSession("Bearer " + token, idSession);
                            checkMed.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    int code = response.code();

                                    switch (code) {
                                        case 404:
                                            Toast.makeText(AuthMedecinObserverActivity.this, "station n'existe pas", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 403:
                                            Toast.makeText(AuthMedecinObserverActivity.this, "enseignant non affecté à la session", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 200:
                                            Intent intent = new Intent(AuthMedecinObserverActivity.this, GrilleActivity.class);
                                            intent.putExtra("idExam", idExam);
                                            intent.putExtra("idSession", idSession);
                                            intent.putExtra("idStation", idStation);
                                            intent.putExtra("idPatient", patien_simule.getText().toString());
                                            intent.putExtra("idObserver", idObserver);
                                            startActivity(intent);
                                            AuthMedecinObserverActivity.this.finish();
                                            break;
                                    }

                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                }
                            });


                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(AuthMedecinObserverActivity.this, "invalid QRCode !", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AuthMedecinObserverActivity.this, "invalid QRCode !", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loadingDialog.dismiss();
                    Toast.makeText(AuthMedecinObserverActivity.this, "invalid QRCode !", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void signin(String login, String password) {
        loadingDialog.show();
        Call<ResponseBody> call = ecosApiRetrofitServices.signin(login, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String token = jsonObject.getString("token");
                    final int idObserver = jsonObject.getJSONObject("enseignant").getInt("id_Enseignant");
                    Log.d("idObserver", "onResponse: " + idObserver);
                    JSONArray privileges = jsonObject.getJSONObject("enseignant").getJSONArray("privileges");
                    boolean isObserver = false;
                    for (int i = 0; i < privileges.length(); i++) {
                        int idPrivilege = privileges.getJSONObject(i).getInt("id_Privilege");
                        if (idPrivilege == 6) {
                            isObserver = true;
                        }
                    }
                    if (!isObserver) {
                        Toast.makeText(AuthMedecinObserverActivity.this, "Cet enseignant n'est pas un observateur !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Utils.saveToken(AuthMedecinObserverActivity.this, s);
                    //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

                    Call<ResponseBody> checkMed = ecosApiRetrofitServices.checkIfMedCanPassSession("Bearer " + token, idSession);
                    checkMed.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            int code = response.code();

                            switch (code) {
                                case 404:
                                    Toast.makeText(AuthMedecinObserverActivity.this, "station n'existe pas", Toast.LENGTH_SHORT).show();
                                    break;
                                case 403:
                                    Toast.makeText(AuthMedecinObserverActivity.this, "enseignant non affecté à la session", Toast.LENGTH_SHORT).show();
                                    break;
                                case 200:
                                    Intent intent = new Intent(AuthMedecinObserverActivity.this, GrilleActivity.class);
                                    intent.putExtra("idExam", idExam);
                                    intent.putExtra("idSession", idSession);
                                    intent.putExtra("idStation", idStation);
                                    intent.putExtra("idPatient", patien_simule.getText().toString());
                                    intent.putExtra("idObserver", idObserver);
                                    startActivity(intent);
                                    finish();
                                    break;
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loadingDialog.dismiss();
                        }
                    });

                } catch (JSONException | IOException | NullPointerException e) {
                    Toast.makeText(AuthMedecinObserverActivity.this, "informations erronées", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    loadingDialog.dismiss();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingDialog.dismiss();
            }
        });
    }


    private ProgressDialog loadingDialog(String msg) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

}
