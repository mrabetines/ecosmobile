package com.vayetek.ecosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.Utils;
import com.vayetek.ecosapp.models.Session;

public class SessionActivity extends AppCompatActivity {
    private List<Session> sessionList;
    String authorization = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);

        authorization = Utils.getAuthorization(SessionActivity.this);

        if (!Utils.isUserAuthenticated(this)) {
            // perform auth
            Intent intent = new Intent(this, UniversityAuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            //Toast.makeText(getApplicationContext(),authorization,Toast.LENGTH_LONG).show();
            //new ListSessionsTask().execute();
        }

    }

    /*
    class ListSessionsTask extends AsyncTask<Void, Void, List<Session>> {
        private ProgressDialog progress = null;

        @Override
        protected List<Session> doInBackground(Void... params) {
            EcosApiRetrofitServices ecosApiRetrofitServices = new RestAdapter.Builder()
                    .setEndpoint(EcosApiRetrofitServices.ENDPOINT)
                    .build()
                    .create(EcosApiRetrofitServices.class);
            try {
                sessionList = ecosApiRetrofitServices.listSessions(authorization);
            } catch (RuntimeException e) {
                Intent intent = new Intent(SessionActivity.this, AuthActivity.class);
                startActivity(intent);
            }


            return sessionList;
        }

        @Override
        protected void onPreExecute() {

            progress = ProgressDialog.show(
                    SessionActivity.this, null, "Loading ...");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Session> sessions) {
            super.onPostExecute(sessions);

            if (sessions != null) {
                createButtonSession(getApplicationContext(), sessions);
            }


            progress.dismiss();
        }

        void createButtonSession(Context context, List<Session> sessions) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutSession);
            for (int i = 0; i < sessions.size(); i++) {
                Button sessionButton = new Button(context);
                sessionButton.setText(sessions.get(i).getNom());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 18, 0, 0);
                sessionButton.setLayoutParams(params);
                sessionButton.setId(sessions.get(i).getId_Session());

                final int id_ = sessionButton.getId();

                layout.addView(sessionButton);

                clickEvent(id_, sessionButton, context);
            }
        }

        void clickEvent(final int id_, Button sessionButton, final Context context) {
            sessionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnline()) {
                        Intent intent = new Intent(context, ExamenActivity.class);
                        intent.putExtra("id_Session", id_);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "Verify connectivity", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(context, id_ + "", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Utils.saveToken(SessionActivity.this, null);
                Intent intent = new Intent(SessionActivity.this, AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_restore:
                new notesStore().execute();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    class notesStore extends AsyncTask<int[], Void, Integer> {

        private ProgressDialog progress = new ProgressDialog(SessionActivity.this);

        @Override
        protected Integer doInBackground(int[]... params) {
            EcosApiRetrofitServices ecosApiRetrofitServices = new RestAdapter.Builder()
                    .setEndpoint(EcosApiRetrofitServices.ENDPOINT)
                    .build()
                    .create(EcosApiRetrofitServices.class);


            int codeRetour = 0;
            try {
                NoteModelRequestArray noteModelRequestArray = Utils.getNoteRequests(SessionActivity.this);
                for (NoteModelRequest noteModelRequest : noteModelRequestArray.getNoteModelRequests()) {
                    codeRetour = ecosApiRetrofitServices.storeNote(authorization, noteModelRequest.getIdStudent(), noteModelRequest.getIdStation(),
                            noteModelRequest.getNotes(), noteModelRequest.getIdEnseignant(), noteModelRequest.getIdPatient(), noteModelRequest.getResultCode());

                }
                Utils.deleteRequestes(SessionActivity.this);

            } catch (RuntimeException | JSONException e) {
                e.printStackTrace();
            }
            return codeRetour;
        }

        @Override
        protected void onPreExecute() {
            progress.setMessage("Chargement ...");
            progress.show();
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Integer codeRetour) {
            super.onPostExecute(codeRetour);
            progress.dismiss();
            switch (codeRetour) {
                case 0:
                    Toast.makeText(getBaseContext(), "non enregistré, données invalides", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getBaseContext(), "enregistré", Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(getBaseContext(), "Student had notes already", Toast.LENGTH_LONG).show();
                    break;
            }
            // reinitialize checkboxes
            /*
            int i = 0, size = listItems.size();
            while (i < size) {
                int id = listItems.get(i).getId_Item();
                ((RadioGroup) findViewById(id)).clearCheck();
                i++;
            }*/
/*

        }
    }
    */

}
