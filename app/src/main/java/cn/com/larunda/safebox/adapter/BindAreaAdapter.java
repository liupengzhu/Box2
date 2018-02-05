package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.BindArea;

/**
 * Created by sddt on 18-1-31.
 */

public class BindAreaAdapter extends RecyclerView.Adapter<BindAreaAdapter.ViewHolder> {
    private Context context;
    private List<BindArea> bindAreaList = new ArrayList<>();
    private boolean isCheckedLayout = false;
    private BindAreaOnLongClickListener bindAreaOnLongClickListener;

    public BindAreaAdapter(Context context, List<BindArea> bindAreaList) {
        this.context = context;
        this.bindAreaList = bindAreaList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView inOrOut;
        LinearLayout userLayout;
        ImageView checked_button;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.area_item_area_text);
            time = itemView.findViewById(R.id.area_item_time_text);
            inOrOut = itemView.findViewById(R.id.area_item_position_text);
            userLayout = itemView.findViewById(R.id.area_item_layout);
            checked_button = itemView.findViewById(R.id.area_check_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.area_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        /**
         * 设置子条目的长按点击事件
         */
        viewHolder.userLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (bindAreaOnLongClickListener != null) {
                    bindAreaOnLongClickListener.onClick(v);
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
                BindArea bindArea = bindAreaList.get(position);
                if (bindArea.isImgIsChecked()) {
                    bindArea.setImgIsChecked(false);
                    viewHolder.checked_button.setImageResource(R.mipmap.unchecked);
                } else {
                    bindArea.setImgIsChecked(true);
                    viewHolder.checked_button.setImageResource(R.mipmap.checked);
                }
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BindArea bindArea = bindAreaList.get(position);
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
        if (bindArea.isImgIsChecked()) {
            holder.checked_button.setImageResource(R.mipmap.checked);
        } else {
            holder.checked_button.setImageResource(R.mipmap.unchecked);
        }

        holder.name.setText(bindArea.getName());
        holder.time.setText(bindArea.getTime());
        holder.inOrOut.setText(bindArea.getIn_or_out());
    }

    @Override
    public int getItemCount() {
        return bindAreaList.size();
    }

    public interface BindAreaOnLongClickListener {
        void onClick(View v);
    }

    public void setCheckedLayout(boolean checkedLayout) {
        isCheckedLayout = checkedLayout;
    }

    public void setBindAreaOnLongClickListener(BindAreaOnLongClickListener bindAreaOnLongClickListener) {
        this.bindAreaOnLongClickListener = bindAreaOnLongClickListener;
    }

    public List<BindArea> getBindAreaList() {
        return bindAreaList;
    }
}
