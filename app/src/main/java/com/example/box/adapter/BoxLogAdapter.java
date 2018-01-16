package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.box.R;
import com.example.box.recycler.BoxLog;
import com.example.box.recycler.UserLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-15.
 */

public class BoxLogAdapter extends RecyclerView.Adapter<BoxLogAdapter.ViewHolder> {

    private List<BoxLog> boxLogList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView data;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.box_log_time_text);
            data = itemView.findViewById(R.id.box_log_data_text);
            title = itemView.findViewById(R.id.box_log_text);
        }
    }

    public BoxLogAdapter(List<BoxLog> boxLogList) {
        this.boxLogList = boxLogList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.box_log_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BoxLog boxLog = boxLogList.get(position);
        holder.date.setText(boxLog.getDate());
        holder.data.setText(boxLog.getData());
        holder.title.setText(boxLog.getTitle());
    }

    @Override
    public int getItemCount() {
        return boxLogList.size();
    }

}
