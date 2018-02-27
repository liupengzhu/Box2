package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.MyBLE;

/**
 * Created by sddt on 18-2-27.
 */

public class BLEAdapter extends RecyclerView.Adapter<BLEAdapter.ViewHolder> {
    private Context context;
    private List<MyBLE> bleList = new ArrayList<>();
    private BLEOnClickListener bleOnClickListener;

    public BLEAdapter(Context context, List<MyBLE> bleList) {
        this.context = context;
        this.bleList = bleList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView bleName;
        TextView statusText;
        RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            bleName = itemView.findViewById(R.id.ble_name);
            statusText = itemView.findViewById(R.id.ble_status);
            layout = itemView.findViewById(R.id.ble_item_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ble_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bleOnClickListener != null) {
                    bleOnClickListener.onClickListener(v, viewHolder.getAdapterPosition());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyBLE ble = bleList.get(position);
        switch (ble.getStatus()) {
            case 0:
                holder.statusText.setVisibility(View.GONE);
                break;
            case 1:
                holder.statusText.setVisibility(View.VISIBLE);
                holder.statusText.setText("正在连接...");
                break;
            case 2:
                holder.statusText.setVisibility(View.VISIBLE);
                holder.statusText.setText("已连接");
                break;
        }
        holder.bleName.setText(ble.getBleName());
    }

    @Override
    public int getItemCount() {
        return bleList.size();
    }

    public interface BLEOnClickListener {
        void onClickListener(View v, int position);
    }

    public void setBleOnClickListener(BLEOnClickListener bleOnClickListener) {
        this.bleOnClickListener = bleOnClickListener;
    }
}
