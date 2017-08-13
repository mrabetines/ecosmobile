package com.vayetek.ecosapp.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.models.GrilleModel;

public class GrilleRecyclerViewAdapter extends RecyclerView.Adapter<GrilleRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private final List<GrilleModel> grilleModels;

    public GrilleRecyclerViewAdapter(Context context, List<GrilleModel> grilleModels) {
        this.context = context;
        this.grilleModels = grilleModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.grille_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final GrilleModel grilleModel = grilleModels.get(position);
        holder.response.clearCheck();
        if (grilleModel.getItem() != null) {
            holder.title.setVisibility(View.GONE);
            holder.question.setVisibility(View.VISIBLE);
            holder.question.setText(grilleModel.getIndex() + ") " + grilleModel.getItem().getLabel());
        } else {
            holder.title.setVisibility(View.VISIBLE);
            holder.question.setVisibility(View.GONE);
            holder.title.setText(grilleModel.getIndex() + ") " + grilleModel.getTitle());
        }

        holder.response.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("radio button id", "onCheckedChanged: " + i);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(grilleModel, ((RadioButton) radioGroup.getChildAt(0)).isChecked());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return grilleModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView question;
        RadioGroup response;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            question = (TextView) itemView.findViewById(R.id.question);
            response = (RadioGroup) itemView.findViewById(R.id.response);
        }
    }

    public OnItemClickListener onItemClickListener;
}

