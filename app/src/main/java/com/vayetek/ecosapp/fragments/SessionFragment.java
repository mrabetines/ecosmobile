package com.vayetek.ecosapp.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.Utils;
import com.vayetek.ecosapp.activities.MainActivity;
import com.vayetek.ecosapp.activities.UniversityAuthActivity;
import com.vayetek.ecosapp.adapters.SessionAdapter;
import com.vayetek.ecosapp.models.NoteModelRequest;
import com.vayetek.ecosapp.models.NoteModelRequestArray;
import com.vayetek.ecosapp.models.Session;

import org.json.JSONException;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SessionFragment extends Fragment {
    private List<Session> sessionList;
    String authorization = null;

    RecyclerView list;
    private ProgressDialog progress;

    public static SessionFragment newInstance() {

        Bundle args = new Bundle();

        SessionFragment fragment = new SessionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(getContext());
        progress.setMessage("Chargement ...");
        authorization = Utils.getAuthorization(getContext());
        if (!Utils.isUserAuthenticated(getContext())) {
            // perform auth
            Intent intent = new Intent(getContext(), UniversityAuthActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        //Toast.makeText(getApplicationContext(),authorization,Toast.LENGTH_LONG).show();
        //    new MainActivity.ListSessionsTask().execute();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.session_fragment, container, false);
        initializeViews(rootView);
        initializeToolbar(rootView);
        getListSessions();
        return rootView;
    }

    private void initializeToolbar(View rootView) {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Sessions");
        toolbar.setSubtitleTextColor(Color.parseColor("#FFFFFF"));
        setHasOptionsMenu(true);
    }

    private void initializeViews(View rootView) {
        list = (RecyclerView) rootView.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);

        Button restore = (Button) rootView.findViewById(R.id.restore);
        restore.setVisibility(View.VISIBLE);
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = new ProgressDialog(getContext());

                progress.setMessage("Chargement ...");
                try {
                    NoteModelRequestArray noteModelRequestArray = Utils.getNoteRequests(getContext());
                    //Log.d("onOptionsItemSelected", "onOptionsItemSelected: " + noteModelRequestArray.getNoteModelRequests().size());
                    for (NoteModelRequest noteModelRequest : noteModelRequestArray.getNoteModelRequests()) {
                        //Log.d("NoteModelRequest", "NoteModelRequest: " + noteModelRequest.toString());
                        progress.show();
                        Call<ResponseBody> call = Utils.getEcosApiRetrofitServicesInstance().storeNote(authorization,noteModelRequest.getIdStudent(), noteModelRequest.getIdStation(),
                                noteModelRequest.getNotes(), noteModelRequest.getResultCode());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d("code", "onResponse: " + response.code());
                                Log.d("message", "onResponse: " + response.message());
                                progress.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.setMessage("Succès de la récupération de données");

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                } catch (RuntimeException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button clearCache = (Button) rootView.findViewById(R.id.clear_cache);
        clearCache.setVisibility(View.VISIBLE);
        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.deleteRequestes(getContext());
                Toast.makeText(getContext(), "Cache est vidé", Toast.LENGTH_LONG).show();
            }
        });
    }

    void getListSessions() {
        progress.show();
        Call<List<Session>> call = Utils.getEcosApiRetrofitServicesInstance().listSessions(Utils.token);
        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                Log.d("message", "onResponse: " + response.message());
                Log.d("code", "onResponse: " + response.code());
                if (response.code() == 401 || response.code() == 400) {
                    Utils.token = "";
                    Utils.saveToken(getContext(), null);
                    Intent intent = new Intent(getContext(), UniversityAuthActivity.class);
                    Toast.makeText(getContext(), "Token est expiré", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    getActivity().finish();
                }
                if (response.code() == 200) {
                    sessionList = response.body();
                    SessionAdapter sessionAdapter = new SessionAdapter(getContext(), sessionList);
                    sessionAdapter.setOnItemClickListener(new SessionAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClickListener(int id) {
                            if (Utils.isOnline(getContext())) {
                                ((MainActivity) getActivity()).showFragment(ExamFragment.newInstance(id));
                            } else {
                                Toast.makeText(getContext(), "Verifiez votre connectivité", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    list.setAdapter(sessionAdapter);
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                progress.dismiss();
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.generic_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Utils.saveToken(getContext(), null);
                Intent intent = new Intent(getContext(), UniversityAuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_restore:

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}