package com.vayetek.ecosapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.Utils;
import com.vayetek.ecosapp.models.University;
import com.vayetek.ecosapp.services.UniversityRetrofitServices;

public class UniversityAuthActivity extends AppCompatActivity implements Button.OnClickListener {
    Button sigin_btn;
    EditText login_edit;
    EditText passwrod_edit;
    UniversityRetrofitServices universityRetrofitServices;
    LinearLayout universitiesLayout;
    Button retry;
    University checkedUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_university);

        sigin_btn = (Button) findViewById(R.id.signin_btn);
        login_edit = (EditText) findViewById(R.id.login_edit);
        passwrod_edit = (EditText) findViewById(R.id.password_edit);
        universitiesLayout = (LinearLayout) findViewById(R.id.universities);

        universityRetrofitServices = Utils.getUniversityRetrofitServices();

        sigin_btn.setOnClickListener(this);
        retry = (Button) findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListUniversities();
            }
        });
        getListUniversities();
    }

    @Override
    public void onClick(View v) {

        if (login_edit.getText().toString().isEmpty() || passwrod_edit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez saisir votre Login et Password", Toast.LENGTH_LONG).show();
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


    private void signin(String login, String password) {
        final ProgressDialog loadingDialog = loadingDialog("Chargement...");
        loadingDialog.show();
        JsonObject postParams = new JsonObject();
        postParams.addProperty("login", login);
        postParams.addProperty("password", password);
        Call<ResponseBody> call = universityRetrofitServices.universitySingIn(postParams);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingDialog.dismiss();
                if (response.code() != 200) {
                    Toast.makeText(UniversityAuthActivity.this, "informations erronées", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    //Log.d("onResponse", "onResponse: " + jsonObject);
                    //String token = jsonObject.getJSONObject("response").getString("token");
                    //Utils.saveToken(UniversityAuthActivity.this, token);
                    //Utils.token = "Bearer " + token;
                    //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UniversityAuthActivity.this, AuthActivity.class);
                    intent.putExtra("university", checkedUniversity);
                    startActivity(intent);
                    finish();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
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


    public void getListUniversities() {
        if (!Utils.isOnline(UniversityAuthActivity.this)) {
            retry.setVisibility(View.VISIBLE);
            Toast.makeText(UniversityAuthActivity.this, "Verifiez la connectivié", Toast.LENGTH_LONG).show();
            return;
        }
        retry.setVisibility(View.GONE);

        final ProgressDialog loadingDialog = loadingDialog("Chargement des facultés disponibles...");
        loadingDialog.show();

        if (!Utils.isOnline(UniversityAuthActivity.this)) {
            retry.setVisibility(View.VISIBLE);
            return;
        }
        Call<List<University>> call = Utils.getUniversityRetrofitServices().getListUniversities();
        call.enqueue(new Callback<List<University>>() {
            @Override
            public void onResponse(Call<List<University>> call, Response<List<University>> response) {
                List<University> listUniversities = response.body();
                //list.setAdapter(new UniversityAdapter(AuthActivity.this, listUniversities));
                RadioGroup radioGroup = new RadioGroup(UniversityAuthActivity.this);
                radioGroup.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT));
                radioGroup.setOrientation(RadioGroup.VERTICAL);
                for (final University university : listUniversities) {
                    RadioButton radioButton = new RadioButton(UniversityAuthActivity.this);
                    radioButton.setText(university.getLabel() + " - " + university.getRegion());
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 16, 0, 0);
                    radioButton.setLayoutParams(params);
                    final float scale = getBaseContext().getResources().getDisplayMetrics().density;
                    radioButton.setPadding(radioButton.getPaddingLeft() + (int) (9.0f * scale + 0.5f)
                            , radioButton.getPaddingTop()
                            , radioButton.getPaddingRight()
                            , radioButton.getPaddingBottom());
                    radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                login_edit.setText(university.getLogin());
                                checkedUniversity = university;
                            }
                        }
                    });
                    radioGroup.addView(radioButton);
                }
                universitiesLayout.addView(radioGroup);

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<University>> call, Throwable t) {
                loadingDialog.dismiss();
                t.printStackTrace();
            }
        });
    }

}