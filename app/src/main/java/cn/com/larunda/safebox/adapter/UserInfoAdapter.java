package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.com.larunda.safebox.MyApplication;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.gson.UserInfo;
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
    private UserInfoOnLongClickListener userInfoOnLongClickListener;
    private Context context;
    private boolean isCheckedLayout = false;

    public UserInfoAdapter(List<MyUserInfo> myUserInfoList) {
        this.myUserInfoList = myUserInfoList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImg;
        TextView userName;
        TextView user;
        TextView userQx;
        LinearLayout userLayout;
        ImageView checked_button;

        public ViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.user_info_img);
            userName = itemView.findViewById(R.id.user_info_name);
            user = itemView.findViewById(R.id.user_info_username);
            userQx = itemView.findViewById(R.id.user_info_qx);
            userLayout = itemView.findViewById(R.id.user_info_item_layout);
            checked_button = itemView.findViewById(R.id.user_info_check_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.user_info_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUserInfo myUserInfo = myUserInfoList.get(viewHolder.getAdapterPosition());
                if (userInfoOnClickListener != null) {
                    userInfoOnClickListener.onClick(v, myUserInfo.getUserId());
                }
            }
        });
        /**
         * 设置子条目的长按点击事件
         */
        viewHolder.userLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (userInfoOnLongClickListener != null) {
                    userInfoOnLongClickListener.onClick(v);
                    return true;
                }
                return false;
            }
        });
        /**
         * 设置多选按钮的单击事件 改变当前条目的选中状态
         */
        viewHolder.checked_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                MyUserInfo userInfo = myUserInfoList.get(position);
                if (userInfo.isImgIsChecked()) {
                    userInfo.setImgIsChecked(false);
                    viewHolder.checked_button.setImageResource(R.mipmap.unchecked);
                } else {
                    userInfo.setImgIsChecked(true);
                    viewHolder.checked_button.setImageResource(R.mipmap.checked);
                }
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MyUserInfo myUserInfo = myUserInfoList.get(position);

        /**
         * 判断当前是否是长按状态设置多选按钮是否可见
         */
        if (isCheckedLayout) {
            holder.checked_button.setVisibility(View.VISIBLE);
        } else {
            holder.checked_button.setVisibility(View.GONE);
        }
        /**
         * 判断当前按钮是否是选中状态 改变图标
         */
        if (myUserInfo.isImgIsChecked()) {
            holder.checked_button.setImageResource(R.mipmap.checked);
        } else {
            holder.checked_button.setImageResource(R.mipmap.unchecked);
        }
        if (myUserInfo.getUserImg() != null) {
            Glide.with(MyApplication.getContext()).load(myUserInfo.getUserImg())
                    .placeholder(R.drawable.user).dontAnimate()
                    .error(R.mipmap.user_img).into(holder.userImg);
        } else {
            holder.userImg.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
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
        void onClick(View v, String id);
    }

    public interface UserInfoOnLongClickListener {
        void onClick(View v);
    }

    public void setUserInfoOnClickListener(UserInfoOnClickListener userInfoOnClickListener) {
        this.userInfoOnClickListener = userInfoOnClickListener;
    }

    public void setUserInfoOnLongClickListener(UserInfoOnLongClickListener userInfoOnLongClickListener) {
        this.userInfoOnLongClickListener = userInfoOnLongClickListener;
    }

    public void setCheckedLayout(boolean checkedLayout) {
        isCheckedLayout = checkedLayout;
    }

    public List<MyUserInfo> getMyUserInfoList() {
        return myUserInfoList;
    }
}
