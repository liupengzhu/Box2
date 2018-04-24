package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.BoxAddUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sddt on 18-1-26.
 */

public class BoxAddUserAdapter extends RecyclerView.Adapter<BoxAddUserAdapter.ViewHolder> {
    private Context context;
    private List<BoxAddUser> boxAddUserList = new ArrayList<>();
    private BoxAddUserOnLongClickListener boxAddUserOnLongClickListener;
    private BoxAddUserOnClickListener boxAddUserOnClickListener;

    private boolean isCheckedLayout = false;

    public BoxAddUserAdapter(Context context, List<BoxAddUser> boxAddUserList) {
        this.context = context;
        this.boxAddUserList = boxAddUserList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView company;
        TextView department;
        TextView phone;
        CircleImageView img;

        LinearLayout userLayout;
        ImageView checked_button;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.box_add_user_item_dw_name_text);
            company = itemView.findViewById(R.id.box_add_user_item_dw_text);
            department = itemView.findViewById(R.id.box_add_user_item_bm_text);
            phone = itemView.findViewById(R.id.box_add_user_item_dw_phone_text);
            img = itemView.findViewById(R.id.box_add_user_item_img);

            userLayout = itemView.findViewById(R.id.box_add_user_item_layout);
            checked_button = itemView.findViewById(R.id.box_add_user_check_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.box_add_user_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        /**
         * 设置子条目的长按点击事件
         */
        viewHolder.userLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (boxAddUserOnLongClickListener != null) {
                    boxAddUserOnLongClickListener.onClick(v);
                    return true;
                }
                return false;
            }
        });
        /**
         * 子条目的单击事件
         */
        viewHolder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxAddUserOnClickListener != null) {
                    boxAddUserOnClickListener.onClick(v,boxAddUserList.get(viewHolder.getAdapterPosition()).getId());
                }
            }
        });
        /**
         * 设置多选按钮的单击事件 改变当前条目的选中状态
         */
        viewHolder.checked_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                BoxAddUser boxAddUser = boxAddUserList.get(position);
                if (boxAddUser.isImgIsChecked()) {
                    boxAddUser.setImgIsChecked(false);
                    viewHolder.checked_button.setImageResource(R.mipmap.unchecked);
                } else {
                    boxAddUser.setImgIsChecked(true);
                    viewHolder.checked_button.setImageResource(R.mipmap.checked);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BoxAddUser boxAddUser = boxAddUserList.get(position);

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
        if (boxAddUser.isImgIsChecked()) {
            holder.checked_button.setImageResource(R.mipmap.checked);
        } else {
            holder.checked_button.setImageResource(R.mipmap.unchecked);
        }

        if (boxAddUser.getPic() != null) {
            Glide.with(context).load(boxAddUser.getPic()).into(holder.img);
        }
        holder.name.setText(boxAddUser.getName());
        holder.company.setText(boxAddUser.getCompany());
        holder.department.setText(boxAddUser.getDepartment());
        holder.phone.setText(boxAddUser.getPhone());
    }

    @Override
    public int getItemCount() {
        return boxAddUserList.size();
    }

    public interface BoxAddUserOnLongClickListener {
        void onClick(View v);
    }

    public interface BoxAddUserOnClickListener {
        void onClick(View v, String userId);
    }

    public void setCheckedLayout(boolean checkedLayout) {
        isCheckedLayout = checkedLayout;
    }

    public void setBoxAddUserOnLongClickListener(BoxAddUserOnLongClickListener boxAddUserOnLongClickListener) {
        this.boxAddUserOnLongClickListener = boxAddUserOnLongClickListener;
    }

    public List<BoxAddUser> getBoxAddUserList() {
        return boxAddUserList;
    }

    public void setBoxAddUserOnClickListener(BoxAddUserOnClickListener boxAddUserOnClickListener) {
        this.boxAddUserOnClickListener = boxAddUserOnClickListener;
    }
}
