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
import cn.com.larunda.safebox.recycler.UserInfo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sddt on 18-1-16.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {

    private List<UserInfo> userInfoList = new ArrayList<>();
    private UserInfoOnClickListener userInfoOnClickListener;
    private Context context;

    public UserInfoAdapter(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImg;
        TextView userName;
        TextView userId;
        TextView userQx;
        RelativeLayout userLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.user_info_img);
            userName = itemView.findViewById(R.id.user_info_name);
            userId = itemView.findViewById(R.id.user_info_username);
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
                if(userInfoOnClickListener!=null){
                    userInfoOnClickListener.onClick(v);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        UserInfo userInfo = userInfoList.get(position);
        if (userInfo.getUserImg() != null) {
            Glide.with(MyApplication.getContext()).load(userInfo.getUserImg()).into(holder.userImg);
        }
        holder.userName.setText(userInfo.getUserName());
        holder.userId.setText(userInfo.getUserId());
        holder.userQx.setText(userInfo.getUserQx());
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }


    public interface UserInfoOnClickListener{
        void onClick(View v);
    }

    public void setUserInfoOnClickListener(UserInfoOnClickListener userInfoOnClickListener) {
        this.userInfoOnClickListener = userInfoOnClickListener;
    }
}
