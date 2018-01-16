package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.box.MyApplication;
import com.example.box.R;
import com.example.box.recycler.UserInfo;
import com.example.box.recycler.UserLog;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sddt on 18-1-16.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {

    private List<UserInfo> userInfoList = new ArrayList<>();

    public UserInfoAdapter(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImg;
        TextView userName;
        TextView userId;
        TextView userQx;

        public ViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.user_info_img);
            userName = itemView.findViewById(R.id.user_info_name);
            userId = itemView.findViewById(R.id.user_info_username);
            userQx = itemView.findViewById(R.id.user_info_qx);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        UserInfo userInfo = userInfoList.get(position);
        Glide.with(MyApplication.getContext()).load(userInfo.getUserImg()).into(holder.userImg);
        holder.userName.setText(userInfo.getUserName());
        holder.userId.setText(userInfo.getUserId());
        holder.userQx.setText(userInfo.getUserQx());
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }
}
