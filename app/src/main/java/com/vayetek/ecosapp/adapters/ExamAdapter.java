package com.vayetek.ecosapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.activities.MainActivity;
import com.vayetek.ecosapp.models.Examen;

//added by ines
import android.view.View.OnLongClickListener;

import com.google.zxing.integration.android.IntentIntegrator;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

    private final Context context;
    private final List<Examen> listSessions;
    //OnLongClickAdapter onLongClickAdapter;
    //
    private IntentIntegrator qrScan;

    public ExamAdapter(Context context, List<Examen> sessions) {
        this.context = context;
        this.listSessions = sessions;
        //
        qrScan = new IntentIntegrator((MainActivity) context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Examen examen = listSessions.get(position);
        holder.title.setText(examen.getNom());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    Log.d("onclick", "onClick: " + examen.getId_Session());
                    onItemClickListener.onItemClickListener(examen.getId_Examen());
                }
            }
        });
        /////added by ines
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               /* try {
                    onLongClickAdapter = (OnLongClickAdapter) context;
                } catch (ClassCastException e) {
                    Log.d("ex",e.getMessage());
                }
                onLongClickAdapter.onLongClickAdapter(examen.getId_Examen());*/
                ((MainActivity) context).setVariable(examen.getId_Examen());
                qrScan.initiateScan();
                return false;
            }

        });

    }


    @Override
    public int getItemCount() {
        return listSessions.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_title);

        }
    }

    private OnItemClickListener onItemClickListener;


    public interface OnItemClickListener {
        void onItemClickListener(int id);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

   /* public interface OnLongClickAdapter {
        public void onLongClickAdapter(int id_Examen);
    }

    public void setOnLongClickAdapter(OnLongClickAdapter onLongClickAdapter) {
        this.onLongClickAdapter = onLongClickAdapter;
    }*/

}
