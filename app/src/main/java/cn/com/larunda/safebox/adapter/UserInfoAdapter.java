package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.com.larunda.safebox.MyApplication;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.recycler.MyUserInfo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sddt on 18-1-16.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {

    private List<MyUserInfo> myUserInfoList = new ArrayList<>();
    private UserInfoOnClickListener userInfoOnClickListener;
    private Context context;

    public UserInfoAdapter(List<MyUserInfo> myUserInfoList) {
        this.myUserInfoList = myUserInfoList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImg;
        TextView userName;
        TextView user;
        TextView userQx;
        RelativeLayout userLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.user_info_img);
            userName = itemView.findViewById(R.id.user_info_name);
            user = itemView.findViewById(R.id.user_info_username);
            userQx = itemView.findViewById(R.id.user_info_qx);
            userLayout = itemView.findViewById(R.id.user_info_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.user_info_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfoOnClickListener != null) {
                    userInfoOnClickListener.onClick(v);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MyUserInfo myUserInfo = myUserInfoList.get(position);
        if (myUserInfo.getUserImg() != null) {
            Glide.with(MyApplication.getContext()).load(myUserInfo.getUserImg()).into(holder.userImg);
        }
        if (myUserInfo.getUserName() != null) {
            holder.userName.setText(myUserInfo.getUserName());
        } else {
            holder.userName.setText("");
        }
        if (myUserInfo.getUser() != null) {
            holder.user.setText(myUserInfo.getUser());
        } else {
            holder.user.setText("");
        }
        if (myUserInfo.getUserQx() != null) {
            holder.userQx.setText(myUserInfo.getUserQx());
        } else {
            holder.userQx.setText("一般用户");
        }
    }

    @Override
    public int getItemCount() {
        return myUserInfoList.size();
    }


    public interface UserInfoOnClickListener {
        void onClick(View v);
    }

    public void setUserInfoOnClickListener(UserInfoOnClickListener userInfoOnClickListener) {
        this.userInfoOnClickListener = userInfoOnClickListener;
    }
}
