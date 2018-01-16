package com.example.box.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.battery.BatteryView;
import com.example.box.MyApplication;
import com.example.box.R;
import com.example.box.recycler.MyBox;
import com.example.box.recycler.MyLog;

import java.util.List;

/**
 * Created by Administrator on 2018/1/9.
 */

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ViewHolder> {

    private List<MyBox> myBoxList;

    public BoxAdapter(List<MyBox> myBoxList) {
        super();
        this.myBoxList = myBoxList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView boxImg;
        TextView boxName;
        ImageView box_one;
        ImageView box_two;
        ImageView box_three;
        ImageView box_isbf_img;
        TextView box_isbf_text;
        ImageView box_issd_img;
        TextView box_issd_text;
        BatteryView box_dl_img;
        TextView box_dl_text;


        public ViewHolder(View itemView) {
            super(itemView);

            boxImg = itemView.findViewById(R.id.dsx_img);
            boxName = itemView.findViewById(R.id.dsx_name);
            box_one = itemView.findViewById(R.id.dsx_x1);
            box_two = itemView.findViewById(R.id.dsx_x2);
            box_three = itemView.findViewById(R.id.dsx_x3);
            box_isbf_img = itemView.findViewById(R.id.dsx_bf_img);
            box_isbf_text = itemView.findViewById(R.id.dsx_bf_text);
            box_issd_img = itemView.findViewById(R.id.dsx_sd_img);
            box_issd_text = itemView.findViewById(R.id.dsx_sd_text);
            box_dl_img = itemView.findViewById(R.id.dsx_dl_img);
            box_dl_text = itemView.findViewById(R.id.dsx_dl_text);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dsx_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyBox box = myBoxList.get(position);
        Glide.with(MyApplication.getContext()).load(box.getBox_img()).into(holder.boxImg);
        holder.boxName.setText(box.getBox_name());
        if (box.getBox_qx() == 3) {
            holder.box_one.setVisibility(View.VISIBLE);
            holder.box_two.setVisibility(View.VISIBLE);
            holder.box_three.setVisibility(View.VISIBLE);
        } else if (box.getBox_qx() == 2) {
            holder.box_one.setVisibility(View.VISIBLE);
            holder.box_two.setVisibility(View.VISIBLE);
            holder.box_three.setVisibility(View.INVISIBLE);
        } else if (box.getBox_qx() == 1) {
            holder.box_one.setVisibility(View.VISIBLE);
            holder.box_two.setVisibility(View.INVISIBLE);
            holder.box_three.setVisibility(View.INVISIBLE);
        }
        if (box.isIs_bf()) {
            holder.box_isbf_img.setImageResource(R.mipmap.list_ybf);
            holder.box_isbf_text.setText("已布防");
            holder.box_isbf_text.setTextColor(R.color.list_text_h);
        } else {
            holder.box_isbf_img.setImageResource(R.mipmap.list_wbf);
            holder.box_isbf_text.setText("未布防");
            holder.box_isbf_text.setTextColor(R.color.list_text_l);
        }
        if (box.isIs_sd()) {
            holder.box_issd_img.setImageResource(R.mipmap.list_ys);
            holder.box_issd_text.setText("已锁定");
            holder.box_issd_text.setTextColor(R.color.list_text_h);
        } else {
            holder.box_issd_img.setImageResource(R.mipmap.list_ws);
            holder.box_issd_text.setText("未锁定");
            holder.box_issd_text.setTextColor(R.color.list_text_l);
        }
        if (box.getBox_dl() != null) {
            holder.box_dl_img.setPaintColor(Integer.parseInt(box.getBox_dl()));
            holder.box_dl_text.setText(box.getBox_dl() + "%");
        } else {
            holder.box_dl_img.setPaintColor(-1);
            holder.box_dl_text.setText("null");
        }


    }

    @Override
    public int getItemCount() {
        return myBoxList.size();
    }
}
