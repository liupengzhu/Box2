package com.example.box.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.box.R;
import com.example.box.recycler.MyLog;

import java.util.List;

/**
 * Created by Administrator on 2018/1/9.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    private List<MyLog> myLogList;
    public LogAdapter(List<MyLog> myLogList) {
        super();
        this.myLogList=myLogList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView titleText;
        TextView timeText;
        TextView contentText;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.log_img);
            titleText = itemView.findViewById(R.id.log_title);
            timeText = itemView.findViewById(R.id.log_time);
            contentText = itemView.findViewById(R.id.log_info);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MyLog log = myLogList.get(position);

        holder.imageView.setBackgroundResource(log.getImageId());
        holder.titleText.setText(log.getTitle());
        holder.contentText.setText(log.getContent());
        holder.timeText.setText(log.getTime());

    }

    @Override
    public int getItemCount() {
        return myLogList.size();
    }
}
