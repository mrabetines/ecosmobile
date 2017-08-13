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
import com.vayetek.ecosapp.models.University;
import com.vayetek.ecosapp.services.EcosApiRetrofitServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity implements Button.OnClickListener {
    Button sigin_btn;
    EditText login_edit;
    EditText passwrod_edit;
    EcosApiRetrofitServices ecosApiRetrofitServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        University university = (University) getIntent().getSerializableExtra("university");
        getSupportActionBar().setTitle(university.getLabel());
        sigin_btn = (Button) findViewById(R.id.signin_btn);
        Button qr_sigin_btn = (Button) findViewById(R.id.qr_signin_btn);
        login_edit = (EditText) findViewById(R.id.login_edit);
        passwrod_edit = (EditText) findViewById(R.id.password_edit);

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

        if (login_edit.getText().toString().isEmpty() || passwrod_edit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez saisir le Login de l'Organisateur et son Password", Toast.LENGTH_LONG).show();
        } else {
            signin(login_edit.getText().toString(), passwrod_edit.getText().toString());
        }

    }

    public void scanneQR() {
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
        Log.d("requestCode", "onActivityResult: " + requestCode);
        if (resultCode == RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
            Log.d("qrcode", "onActivityResult: " + contents);
            Call<ResponseBody> call = ecosApiRetrofitServices.qrSignin(contents);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("code", "onResponse: " + response.code());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String token = jsonObject.getString("token");
                        JSONArray privileges = jsonObject.getJSONObject("enseignant").getJSONArray("privileges");
                        boolean isOrganisateur = false;
                        for (int i = 0; i < privileges.length(); i++) {
                            int idPrivilege = privileges.getJSONObject(i).getInt("id_Privilege");
                            if (idPrivilege == 7) {
                                isOrganisateur = true;
                            }
                        }
                        if (!isOrganisateur) {
                            Toast.makeText(AuthActivity.this, "Cet enseignant n'est pas un organisateur !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (token.length() > 0) {
                            Utils.token = "Bearer " + token;
                            Utils.saveToken(AuthActivity.this, token);
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                            AuthActivity.this.finish();
                        } else {
                            Toast.makeText(AuthActivity.this, "invalid QRCode !", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AuthActivity.this, "invalid QRCode !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(AuthActivity.this, "invalid QRCode !", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void signin(String login, String password) {
        final ProgressDialog loadingDialog = loadingDialog("Chargement...");
        loadingDialog.show();
        Call<ResponseBody> call = ecosApiRetrofitServices.signin(login, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String token = jsonObject.getString("token");
                    JSONArray privileges = jsonObject.getJSONObject("enseignant").getJSONArray("privileges");
                    boolean isOrganisateur = false;
                    for (int i = 0; i < privileges.length(); i++) {
                        int idPrivilege = privileges.getJSONObject(i).getInt("id_Privilege");
                        if (idPrivilege == 7) {
                            isOrganisateur = true;
                            break;
                        }
                    }
                    if (!isOrganisateur) {
                        Toast.makeText(AuthActivity.this, "Cet enseignant n'est pas un organisateur !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Utils.saveToken(AuthActivity.this, token);
                    Utils.token = "Bearer " + token;
                    //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException | IOException | NullPointerException e) {
                    Toast.makeText(AuthActivity.this, "informations erronées", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AuthActivity.this, "informations erronées", Toast.LENGTH_LONG).show();
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