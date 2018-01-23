package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.box.R;
import com.example.box.recycler.LeavingAlarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-23.
 */

public class LeavingAlarmAdapter extends RecyclerView.Adapter<LeavingAlarmAdapter.ViewHolder> {

    List<LeavingAlarm> leavingAlarms = new ArrayList<>();

    private boolean isCheckedLayout = false;
    private LeavingAlarmLongClickListener leavingAlarmLongClickListener;
    private LeavingAlarmOnClickListener leavingAlarmOnClickListener;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView boxName;

        TextView distance;

        TextView name;
        TextView isLeaving;
        ImageView checked_button;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            boxName = itemView.findViewById(R.id.leaving_alarm_name);
            distance = itemView.findViewById(R.id.leaving_alarm_location_text);
            name = itemView.findViewById(R.id.leaving_alarm_name_text);
            isLeaving = itemView.findViewById(R.id.leaving_alarm_is_text);

            checked_button = itemView.findViewById(R.id.leaving_alarm_check_button);
            layout = itemView.findViewById(R.id.leaving_alarm_item_layout);

        }

    }

    public LeavingAlarmAdapter(List<LeavingAlarm> leavingAlarms) {
        this.leavingAlarms = leavingAlarms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaving_alarm_item,
                parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        /**
         * 设置子条目的长按点击事件
         */
        viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (leavingAlarmLongClickListener != null) {
                    leavingAlarmLongClickListener.onLongClick(v);
                    return true;
                }
                return false;
            }
        });
        /**
         * 设置子条目的单击事件
         */
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leavingAlarmOnClickListener != null) {
                    leavingAlarmOnClickListener.onClick(v);
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
                LeavingAlarm leavingAlarm = leavingAlarms.get(position);
                if (leavingAlarm.isImgIsChecked()) {
                    leavingAlarm.setImgIsChecked(false);
                    viewHolder.checked_button.setImageResource(R.mipmap.unchecked);
                } else {
                    leavingAlarm.setImgIsChecked(true);
                    viewHolder.checked_button.setImageResource(R.mipmap.checked);
                }
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LeavingAlarm leavingAlarm = leavingAlarms.get(position);
        holder.boxName.setText(leavingAlarm.getBoxName());
        holder.distance.setText(leavingAlarm.getDistance());
        holder.name.setText(leavingAlarm.getName());
        holder.isLeaving.setText(leavingAlarm.getIsLeaving());

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
        if (leavingAlarm.isImgIsChecked()) {
            holder.checked_button.setImageResource(R.mipmap.checked);
        } else {
            holder.checked_button.setImageResource(R.mipmap.unchecked);
        }

    }

    @Override
    public int getItemCount() {
        return leavingAlarms.size();
    }

    public void setOnLongClickListener(LeavingAlarmLongClickListener leavingAlarmLongClickListener) {
        this.leavingAlarmLongClickListener = leavingAlarmLongClickListener;
    }


    public void setOnClickListener(LeavingAlarmOnClickListener leavingAlarmOnClickListener) {
        this.leavingAlarmOnClickListener = leavingAlarmOnClickListener;
    }

    public interface LeavingAlarmLongClickListener {
        void onLongClick(View v);
    }

    public interface LeavingAlarmOnClickListener {
        void onClick(View v);
    }

    public boolean isCheckedLayout() {
        return isCheckedLayout;
    }

    public void setCheckedLayout(boolean checkedLayout) {
        isCheckedLayout = checkedLayout;
    }

    public List<LeavingAlarm> getLeavingAlarms() {
        return leavingAlarms;
    }
}
