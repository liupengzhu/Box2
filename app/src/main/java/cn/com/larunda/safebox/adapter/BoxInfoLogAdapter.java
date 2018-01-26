package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.BoxInfoLog;

/**
 * Created by sddt on 18-1-26.
 */

public class BoxInfoLogAdapter extends RecyclerView.Adapter<BoxInfoLogAdapter.ViewHolder> {
    private Context context;
    private List<BoxInfoLog> boxInfoLogList = new ArrayList<>();

    public BoxInfoLogAdapter(Context context, List<BoxInfoLog> boxInfoLogList) {
        this.context = context;
        this.boxInfoLogList = boxInfoLogList;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView logName;
        TextView logContent;
        TextView logTime;
        public ViewHolder(View itemView) {
            super(itemView);
            logName = itemView.findViewById(R.id.box_info_log_text);
            logContent = itemView.findViewById(R.id.box_info_log_data_text);
            logTime = itemView.findViewById(R.id.box_info_log_time_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.box_info_log_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BoxInfoLog log = boxInfoLogList.get(position);
        holder.logName.setText(log.getLogName());
        holder.logContent.setText(log.getLogContent());
        holder.logTime.setText(log.getLogTime());

    }

    @Override
    public int getItemCount() {
        return boxInfoLogList.size();
    }
}
