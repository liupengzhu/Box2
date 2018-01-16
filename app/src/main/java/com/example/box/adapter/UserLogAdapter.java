package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.box.R;
import com.example.box.recycler.MySqLs;
import com.example.box.recycler.UserLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-15.
 */

public class UserLogAdapter extends RecyclerView.Adapter<UserLogAdapter.ViewHolder> {

    private List<UserLog> userLogList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView data;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.user_log_time_text);
            data = itemView.findViewById(R.id.user_log_data_text);
            title = itemView.findViewById(R.id.user_log_text);
        }
    }

    public UserLogAdapter(List<UserLog> userLogList) {
        this.userLogList = userLogList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_log_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserLog userLog = userLogList.get(position);
        holder.date.setText(userLog.getDate());
        holder.data.setText(userLog.getData());
        holder.title.setText(userLog.getTitle());
    }

    @Override
    public int getItemCount() {
        return userLogList.size();
    }

}
