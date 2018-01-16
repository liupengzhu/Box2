package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.box.R;
import com.example.box.recycler.AppLog;
import com.example.box.recycler.BoxLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-15.
 */

public class AppLogAdapter extends RecyclerView.Adapter<AppLogAdapter.ViewHolder> {

    private List<AppLog> appLogList = new ArrayList<>();
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView data;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.app_log_time_text);
            data = itemView.findViewById(R.id.app_log_data_text);
            title = itemView.findViewById(R.id.app_log_text);
        }
    }

    public AppLogAdapter(List<AppLog> appLogList) {
        this.appLogList = appLogList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_log_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppLog appLog = appLogList.get(position);
        holder.date.setText(appLog.getDate());
        holder.data.setText(appLog.getData());
        holder.title.setText(appLog.getTitle());
    }

    @Override
    public int getItemCount() {
        return appLogList.size();
    }

}
