package com.vayetek.ecosapp.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.Utils;
import com.vayetek.ecosapp.activities.UniversityAuthActivity;
import com.vayetek.ecosapp.adapters.GrilleRecyclerViewAdapter;
import com.vayetek.ecosapp.adapters.OnItemClickListener;
import com.vayetek.ecosapp.models.GrilleModel;
import com.vayetek.ecosapp.models.Item;
import com.vayetek.ecosapp.services.EcosApiRetrofitServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GrilleActivity extends AppCompatActivity {
    List<Item> itemList = null;
    int id_Session;
    int id_Examen;
    int id_Station;
    int scanneMode;
    //scanneMode = 0 : Read  Observer Doctor
    //scanneMode = 1 : Read Patient simulé
    //scanneMode = 2 : Read Student Id
    String id_Enseignant = "";
    String id_Patient = "";
    String id_Student = "";
    Button add_Student;
    TextView patientInfo;
    TextView studentInfo;
    LayoutInflater inflater;
    EcosApiRetrofitServices ecosApiRetrofitServices;
    String authorization = null;
    private int[] notes;
    private int resultatCode = 0;
    private ProgressDialog progress;
    private RecyclerView layoutItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grille);
        progress = new ProgressDialog(GrilleActivity.this);
        progress.setMessage("chargement...");
        authorization = Utils.getAuthorization(GrilleActivity.this);
        inflater = LayoutInflater.from(this);
        scanneMode = 0;
        id_Session = getIntent().getIntExtra("idSession", 0);
        id_Examen = getIntent().getIntExtra("idExam", 0);
        id_Station = getIntent().getIntExtra("idStation", 0);
        id_Patient = getIntent().getStringExtra("idPatient");

        Log.d("authorization", "onCreate: " + id_Patient);
        patientInfo = (TextView) findViewById(R.id.patientInfo);
        studentInfo = (TextView) findViewById(R.id.studentInfo);
        patientInfo.setText(id_Patient);
        if (id_Patient.length() == 0) {
            id_Patient = "0";
        }
        /*add_Patient = (Button) findViewById(R.id.add_Patient);
        add_Patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanneMode = 1;
                scanneQR();
                //dialogIdSetter(inflater, GrilleActivity.this);
            }
        });*/

        add_Student = (Button) findViewById(R.id.add_Student);
        add_Student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanneMode = 2;
                scanneQR();
                //dialogIdSetter(inflater, GrilleActivity.this);
            }
        });

        //scanne id MedecinOBservateur
        id_Enseignant = String.valueOf(getIntent().getIntExtra("idObserver", 0));
        Log.d("id_Enseignant", "onCreate: " + id_Enseignant);
        //dialogIdSetter(inflater, this);

        ecosApiRetrofitServices = Utils.getEcosApiRetrofitServicesInstance();
        progress.show();
        Log.d("ids", "onCreate: " + id_Session + " " + id_Examen + " " + id_Station);
        Call<List<Item>> call = ecosApiRetrofitServices.listItems(authorization, id_Session, id_Examen, id_Station);
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                itemList = response.body();
                notes = new int[itemList.size()];
                for (int i = 0; i < notes.length; i++) {
                    notes[i] = -1;
                }
                createGrille(GrilleActivity.this, itemList);
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();

            }
        });

    }

    void createGrille(final Context context, List<Item> items) {
        layoutItemList = (RecyclerView) findViewById(R.id.itemList);
        layoutItemList.setLayoutManager(new LinearLayoutManager(GrilleActivity.this));
        layoutItemList.setHasFixedSize(false);
        layoutItemList.setNestedScrollingEnabled(false);

        int size = items.size(), i = 0, j = 1;

        List<GrilleModel> grilleModelList = new ArrayList<>();

        while (i < size) {
            GrilleModel titleGrilleModel = new GrilleModel();
            titleGrilleModel.setIndex(j);
            String title = items.get(i).getNom();
            titleGrilleModel.setTitle(title);
            grilleModelList.add(titleGrilleModel);

            while (i < size && title.equals(items.get(i).getNom())) {
                GrilleModel grilleModel = new GrilleModel();
                grilleModel.setItem(items.get(i));
                grilleModel.setIndex(i + 1);
                grilleModelList.add(grilleModel);
                i++;
            }
            j++;
        }
        GrilleRecyclerViewAdapter grilleRecyclerViewAdapter = new GrilleRecyclerViewAdapter(GrilleActivity.this, grilleModelList);
        grilleRecyclerViewAdapter.onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClickListener(GrilleModel grilleModel, boolean checked) {
                notes[grilleModel.getIndex() - 1] = checked ? 1 : 0;
                //Log.d("onItemClickListener", "onItemClickListener: " + notes[grilleModel.getIndex() - 1] + " " + (grilleModel.getIndex() - 1));
            }
        };
        layoutItemList.setAdapter(grilleRecyclerViewAdapter);
        Button submitButton = (Button) findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGrilleResult(context);
            }
        });


        RadioButton radioButton = (RadioButton) findViewById(R.id.echec);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    resultatCode = 1;
                }
            }
        });
        radioButton = (RadioButton) findViewById(R.id.insuffisant);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    resultatCode = 2;
                }
            }
        });
        radioButton = (RadioButton) findViewById(R.id.suffisant);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    resultatCode = 3;
                }
            }
        });
        radioButton = (RadioButton) findViewById(R.id.attendu);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    resultatCode = 4;
                }
            }
        });
        radioButton = (RadioButton) findViewById(R.id.excellent);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    resultatCode = 5;

                }
            }
        });
    }

    void getGrilleResult(Context context) throws ClassCastException {
        Log.d("resultatCode", "getGrilleResult: " + resultatCode);
        boolean completGrille = true;
        for (int note : notes) {
            if (note == -1) {
                completGrille = false;
            }
        }

        if (completGrille) {
            if (id_Student.equals("")) {
                Toast.makeText(context, "svp scannez l'id de l'etudiant", Toast.LENGTH_LONG).show();
            } else if (id_Enseignant.equals("")) {
                Toast.makeText(context, "svp scannez l'id du medecin observateur", Toast.LENGTH_LONG).show();
            } /*else if (id_Patient.equals("")) {
                            Toast.makeText(context, "please scanne a Patient Simulé", Toast.LENGTH_LONG).show();
                        } */ else if (resultatCode == 0) {
                Toast.makeText(context, "svp donnez une observation à cet étudiant", Toast.LENGTH_LONG).show();
            } else if (isOnline(GrilleActivity.this)) {
                try {
                    Utils.saveRequest(GrilleActivity.this, id_Student, id_Station, id_Enseignant, id_Patient, resultatCode, notes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.show();
                Call<ResponseBody> call = ecosApiRetrofitServices.storeNote(authorization, id_Student, id_Station, notes, resultatCode);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("message", "onResponse: " + response.message());
                        Log.d("code", "onResponse: " + response.code());
                        progress.dismiss();
                        int codeRetour = response.code();
                        switch (codeRetour) {
                            case 400:
                                Toast.makeText(getBaseContext(), "non enregistré, données invalides", Toast.LENGTH_LONG).show();
                                return;
                            case 500:
                                Toast.makeText(getBaseContext(), "l'etudiant doit scanner son QRcode", Toast.LENGTH_LONG).show();
                                return;
                            case 401:
                                Toast.makeText(getBaseContext(), "Rien à modifier", Toast.LENGTH_LONG).show();
                                return;
                            case 404:
                                Toast.makeText(getBaseContext(), "etudiant ou station n'existe pas", Toast.LENGTH_LONG).show();
                                return;
                            case 200:
                                Toast.makeText(getBaseContext(), "enregistré avec succées", Toast.LENGTH_LONG).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                                break;
                        }
                        layoutItemList.getAdapter().notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progress.dismiss();
                    }
                });
            } else {
                Toast.makeText(context, "Verifier la connectivité", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Grille incomplète", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                switch (scanneMode) {
                    case 1:
                        id_Patient = contents.substring(6, contents.length());
                        patientInfo.setText(id_Patient);
                        break;
                    case 2:
                        Log.d("contents", "onActivityResult: " + contents);
                        progress.show();
                        Call<ResponseBody> call = ecosApiRetrofitServices.checkCanPassStation(authorization,
                                "", contents, id_Station, id_Patient, Integer.parseInt(id_Enseignant));

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                progress.dismiss();
                                int code = response.code();

                                try {
                                    if (code == 200) {
                                        JSONObject object = new JSONObject(response.body().string());
                                        String message = object.getString("message");
                                        String etudiantCIN = object.getString("CIN");
                                        studentInfo.setText(etudiantCIN);
                                        id_Student = etudiantCIN;
                                    } else {
                                        String errorMessage = "";

                                        switch (code) {
                                            case 400: {
                                                errorMessage = "Bad request";
                                                break;
                                            }
                                            case 401: {
                                                errorMessage = "Patient n'existe pas";
                                                break;
                                            }
                                            case 402: {
                                                errorMessage = "Station n'existe pas";
                                                break;
                                            }
                                            case 403: {
                                                errorMessage = "Etudiant n'existe pas";
                                                break;
                                            }
                                            case 405: {
                                                errorMessage = "Enseignant n'existe pas";
                                                break;
                                            }
                                            case 406: {
                                                errorMessage = "Etudiant peut pas passé l'examen";
                                                break;
                                            }
                                            case 500: {
                                                errorMessage = "Server problem";
                                                break;
                                            }
                                        }
                                        id_Student = "";
                                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                progress.dismiss();
                            }
                        });

                        break;
                }
            }
            if (resultCode == RESULT_CANCELED) {
                dialogIdSetter(inflater, this);
            }
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
    protected void onPause() {
        progress.dismiss();
        super.onPause();
    }

    public void dialogIdSetter(final LayoutInflater inflater, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LinearLayout dialogIdSetter = (LinearLayout) inflater.inflate(R.layout.dialog_idsetter, null, false);
        String title = "";
        switch (scanneMode) {
            case 1:
                title = "Patient Simulé CIN";
                break;
            case 2:
                title = "Student CIN";
                break;
        }
        builder.setCancelable(false)
                .setTitle(title)
                .setPositiveButton("Valid",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                String content = ((EditText) dialogIdSetter.findViewById(R.id.idSetter)).getText().toString();
                                switch (scanneMode) {
                                    case 1:
                                        id_Patient = content;
                                        patientInfo.setText(id_Patient);
                                        break;
                                    case 2:
                                        id_Student = content;
                                        progress.show();
                                        Call<ResponseBody> call = ecosApiRetrofitServices.checkCanPassStation(
                                                authorization, id_Student, "", id_Station, "", Integer.parseInt(id_Enseignant));
                                        Log.d("authorization", "onClick: " + authorization);
                                        Log.d("student", "onClick: " + id_Student);
                                        Log.d("station", "onClick: " + String.valueOf(id_Station));
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                int code = response.code();

                                                try {
                                                    JSONObject object = new JSONObject(response.body().toString());
                                                    if (code == 200) {
                                                        String message = object.getString("message");
                                                        String etudiantCIN = object.getString("CIN");
                                                        studentInfo.setText(etudiantCIN);
                                                        id_Student = etudiantCIN;
                                                    } else {
                                                        id_Student = "";
                                                        String errorMessage = object.getString("error");
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(GrilleActivity.this);

                                                        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.dismiss();
                                                                finish();
                                                            }
                                                        });
                                                        builder.setMessage(errorMessage);

                                                        AlertDialog dialog = builder.create();
                                                        dialog.show();
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                        break;
                                }
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.setView(dialogIdSetter);
        alert.show();

    }

    @Override
    public void onBackPressed() {

    }

    public static boolean isOnline(Context context) {
        if (context != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.grille_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Utils.saveToken(GrilleActivity.this, null);
                Intent intent = new Intent(GrilleActivity.this, UniversityAuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_station:
                super.onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
