package com.vayetek.ecosapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.models.Session;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private final Context context;
    private final List<Session> listSessions;

    public SessionAdapter(Context context, List<Session> sessions) {
        this.context = context;
        this.listSessions = sessions;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Session session = listSessions.get(position);
        holder.title.setText(session.getNom());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    Log.d("onclick", "onClick: " + session.getId_Session());
                    onItemClickListener.onItemClickListener(session.getId_Session());
                }
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
}
