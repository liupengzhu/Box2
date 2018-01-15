package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.box.MyApplication;
import com.example.box.R;
import com.example.box.recycler.MySq;

import java.util.List;

/**
 * Created by sddt on 18-1-15.
 */

public class SqAdapter extends RecyclerView.Adapter<SqAdapter.ViewHolder> {
    private List<MySq> sqList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        de.hdodenhof.circleimageview.CircleImageView userImg;
        TextView userName;
        TextView xlh;
        TextView date;
        Button qrButton;
        Button cancelButton;


        public ViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.y_list_img);
            userName = itemView.findViewById(R.id.y_user_name_text);
            xlh = itemView.findViewById(R.id.y_xlh_text);
            date = itemView.findViewById(R.id.y_time_text);
            qrButton = itemView.findViewById(R.id.y_qr_button);
            cancelButton = itemView.findViewById(R.id.y_cancel_button);

        }
    }

    public SqAdapter(List<MySq> sqList) {
        this.sqList = sqList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.y_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MySq sq = sqList.get(position);
        Glide.with(MyApplication.getContext()).load(sq.getUserImg()).into(holder.userImg);
        holder.userName.setText(sq.getUserName());
        holder.xlh.setText(sq.getUserXLH());
        holder.date.setText(sq.getDate());

    }

    @Override
    public int getItemCount() {
        return sqList.size();
    }
}
