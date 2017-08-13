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
import com.vayetek.ecosapp.activities.MainActivity;
import com.vayetek.ecosapp.activities.UniversityAuthActivity;
import com.vayetek.ecosapp.adapters.ExamAdapter;
import com.vayetek.ecosapp.models.Examen;

public class ExamFragment extends Fragment {
    List<Examen> examsList = null;
    int idSession;
    private RecyclerView list;
    private ProgressDialog progress;

    public static ExamFragment newInstance(int idSession) {

        Bundle args = new Bundle();
        args.putInt("idSession", idSession);
        ExamFragment fragment = new ExamFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idSession = getArguments().getInt("idSession");
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
        toolbar.setTitle("Examens");
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
        Call<List<Examen>> call = Utils.getEcosApiRetrofitServicesInstance().listExamens(Utils.token, idSession);
        call.enqueue(new Callback<List<Examen>>() {
            @Override
            public void onResponse(Call<List<Examen>> call, Response<List<Examen>> response) {
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
                    examsList = response.body();
                    ExamAdapter examAdapter = new ExamAdapter(getContext(), examsList);
                    examAdapter.setOnItemClickListener(new ExamAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClickListener(int id) {
                            if (Utils.isOnline(getContext())) {
                                ((MainActivity) getActivity()).showFragment(StationFragment.newInstance(idSession, id));
                            } else {
                                Toast.makeText(getContext(), "Verifiez votre connectivité", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    list.setAdapter(examAdapter);
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Examen>> call, Throwable t) {
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
