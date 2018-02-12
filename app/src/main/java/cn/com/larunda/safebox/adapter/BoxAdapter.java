package cn.com.larunda.safebox.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.larunda.battery.BatteryView;

import cn.com.larunda.safebox.BoxActivity;
import cn.com.larunda.safebox.MyApplication;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.recycler.MyBox;

import java.util.List;

/**
 * Created by Administrator on 2018/1/9.
 */

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ViewHolder> {

    private List<MyBox> myBoxList;
    private Context context;
    private DsxLongClickListener dsxLongClickListener;
    private DsxOnClickListener dsxOnClickListener;

    private boolean isCheckedLayout = false;

    public BoxAdapter(List<MyBox> myBoxList) {
        super();
        this.myBoxList = myBoxList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout box_layout;
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
        ImageView checked_button;


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
            box_layout = itemView.findViewById(R.id.dsx_layout);
            checked_button = itemView.findViewById(R.id.dsx_check_button);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dsx_list_item, parent, false);
        context = parent.getContext();
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.box_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                MyBox box = myBoxList.get(position);
                if (dsxOnClickListener != null) {
                    dsxOnClickListener.onClick(v, box.getId());
                }

            }
        });
        viewHolder.box_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (dsxLongClickListener != null) {
                    dsxLongClickListener.onLongClick(v);
                    return true;
                }
                return false;
            }
        });
        viewHolder.checked_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                MyBox box = myBoxList.get(position);
                if (box.isImgIsChecked()) {
                    box.setImgIsChecked(false);
                    viewHolder.checked_button.setImageResource(R.mipmap.unchecked);
                } else {
                    box.setImgIsChecked(true);
                    viewHolder.checked_button.setImageResource(R.mipmap.checked);
                }
            }
        });


        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyBox box = myBoxList.get(position);
        if (box.getBox_img() != null) {
            Glide.with(MyApplication.getContext()).load(box.getBox_img()).into(holder.boxImg);
        }
        if (box.getBox_name() != null) {
            holder.boxName.setText(box.getBox_name());
        } else {
            holder.boxName.setText("");
        }
        if (isCheckedLayout) {
            holder.checked_button.setVisibility(View.VISIBLE);
        } else {
            holder.checked_button.setVisibility(View.GONE);
        }
        if (box.isImgIsChecked()) {
            holder.checked_button.setImageResource(R.mipmap.checked);
        } else {
            holder.checked_button.setImageResource(R.mipmap.unchecked);
        }
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
        } else {
            holder.box_one.setVisibility(View.INVISIBLE);
            holder.box_two.setVisibility(View.INVISIBLE);
            holder.box_three.setVisibility(View.INVISIBLE);
        }
        if (box.isIs_bf()) {
            holder.box_isbf_img.setImageResource(R.mipmap.list_ybf);
            holder.box_isbf_text.setText("已布防");
            holder.box_isbf_text.setTextColor(context.getResources().getColor(R.color.list_text_h));
        } else {
            holder.box_isbf_img.setImageResource(R.mipmap.list_wbf);
            holder.box_isbf_text.setText("未布防");
            holder.box_isbf_text.setTextColor(context.getResources().getColor(R.color.list_text_l));
        }
        if (box.isIs_sd()) {
            holder.box_issd_img.setImageResource(R.mipmap.list_ys);
            holder.box_issd_text.setText("已锁定");
            holder.box_issd_text.setTextColor(context.getResources().getColor(R.color.list_text_h));
        } else {
            holder.box_issd_img.setImageResource(R.mipmap.list_ws);
            holder.box_issd_text.setText("未锁定");
            holder.box_issd_text.setTextColor(context.getResources().getColor(R.color.list_text_l));
        }
        if (box.getBox_dl() != null) {
            holder.box_dl_img.setPaintColor(Integer.parseInt(box.getBox_dl()));
            holder.box_dl_text.setText(box.getBox_dl() + "%");
        } else {
            holder.box_dl_img.setPaintColor(-1);
            holder.box_dl_text.setText("");
        }


    }

    @Override
    public int getItemCount() {
        return myBoxList.size();
    }


    public void setOnLongClickListener(DsxLongClickListener dsxLongClickListener) {
        this.dsxLongClickListener = dsxLongClickListener;
    }

    public interface DsxLongClickListener {
        void onLongClick(View v);
    }

    public interface DsxOnClickListener {
        void onClick(View v, String id);
    }

    public boolean isCheckedLayout() {
        return isCheckedLayout;
    }

    public void setCheckedLayout(boolean checkedLayout) {
        isCheckedLayout = checkedLayout;
    }

    public List<MyBox> getMyBoxList() {
        return myBoxList;
    }

    public void setDsxOnClickListener(DsxOnClickListener dsxOnClickListener) {
        this.dsxOnClickListener = dsxOnClickListener;
    }
}
