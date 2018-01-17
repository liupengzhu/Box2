package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.box.R;
import com.example.box.recycler.Enclosure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-17.
 */

public class EnclosureAdapter extends RecyclerView.Adapter<EnclosureAdapter.ViewHolder> {

    private List<Enclosure> enclosureList = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.enclosure_name);
        }
    }

    public EnclosureAdapter(List<Enclosure> enclosureList) {
        this.enclosureList = enclosureList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enclosure_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Enclosure enclosure = enclosureList.get(position);
        holder.name.setText(enclosure.getName());
    }

    @Override
    public int getItemCount() {
        return enclosureList.size();
    }
}
