package com.vayetek.ecosapp.fragments;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.Utils;
import com.vayetek.ecosapp.activities.AuthActivity;
import com.vayetek.ecosapp.activities.AuthMedecinObserverActivity;
import com.vayetek.ecosapp.activities.UniversityAuthActivity;
import com.vayetek.ecosapp.adapters.StationAdapter;
import com.vayetek.ecosapp.models.Station;

public class StationFragment extends Fragment {
    List<Station> stationList = null;
    int idSession;
    int idExam;
    private ProgressDialog progress;
    private RecyclerView list;

    public static StationFragment newInstance(int idSession, int idExam) {

        Bundle args = new Bundle();
        args.putInt("idSession", idSession);
        args.putInt("idExam", idExam);
        StationFragment fragment = new StationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idSession = getArguments().getInt("idSession");
        idExam = getArguments().getInt("idExam");
        progress = new ProgressDialog(getContext());
        progress.setMessage("Chargement ...");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.session_fragment, container, false);
        initializeViews(rootView);
        initializeToolbar(rootView);
        getListExams();
        return rootView;
    }

    private void initializeToolbar(View rootView) {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Stations");
        toolbar.setSubtitleTextColor(Color.parseColor("#FFFFFF"));
        setHasOptionsMenu(true);
    }

    private void initializeViews(View rootView) {
        list = (RecyclerView) rootView.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
    }

    void getListExams() {
        progress.show();
        Call<List<Station>> call = Utils.getEcosApiRetrofitServicesInstance().listStations(Utils.token, idSession, idExam);
        call.enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                Log.d("message", "onResponse: " + response.message());
                Log.d("code", "onResponse: " + response.code());
                if (response.code() == 401 || response.code() == 400) {
                    Utils.token = "";
                    Intent intent = new Intent(getContext(), AuthActivity.class);
                    Toast.makeText(getContext(), "Token est expiré", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    getActivity().finish();
                }
                if (response.code() == 200) {
                    stationList = response.body();
                    StationAdapter stationAdapter = new StationAdapter(getContext(), stationList);
                    stationAdapter.setOnItemClickListener(new StationAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClickListener(int idStation) {
                            if (Utils.isOnline(getContext())) {
                                //((MainActivity) getActivity()).showFragment(null);
                                Intent intent = new Intent(getContext(), AuthMedecinObserverActivity.class);
                                intent.putExtra("idSession", idSession);
                                intent.putExtra("idExam", idExam);
                                intent.putExtra("idStation", idStation);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Verifiez votre connectivité", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    list.setAdapter(stationAdapter);
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
