package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.box.R;
import com.example.box.recycler.MySqLs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-15.
 */

public class SqLsAdapter extends RecyclerView.Adapter<SqLsAdapter.ViewHolder> {

    private List<MySqLs> sqLsList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView data;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.ls_time_text);
            data = itemView.findViewById(R.id.ls_data_text);
        }
    }

    public SqLsAdapter(List<MySqLs> sqLsList) {
        this.sqLsList = sqLsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ls_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MySqLs sqLs = sqLsList.get(position);
        holder.date.setText(sqLs.getDate());
        holder.data.setText(sqLs.getData());
    }

    @Override
    public int getItemCount() {
        return sqLsList.size();
    }

}
