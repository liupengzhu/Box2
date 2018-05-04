package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.CollectorTask;

public class CollectorTaskAdapter extends RecyclerView.Adapter<CollectorTaskAdapter.ViewHolder> {
    private Context context;
    private List<CollectorTask> collectorTaskList = new ArrayList<>();

    public CollectorTaskAdapter(Context context, List<CollectorTask> collectorTaskList) {
        this.context = context;
        this.collectorTaskList = collectorTaskList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private TextView status;
        private TextView name;
        private TextView code;
        private TextView originCity;
        private TextView destinationCity;
        private LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.collector_time);
            status = itemView.findViewById(R.id.collector_status);
            name = itemView.findViewById(R.id.collector_name);
            code = itemView.findViewById(R.id.collector_code);
            originCity = itemView.findViewById(R.id.collector_origin_city);
            destinationCity = itemView.findViewById(R.id.collector_destination_city);
            layout = itemView.findViewById(R.id.collector_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CollectorTask collectorTask = collectorTaskList.get(position);
        if (collectorTask.getCreatedTime() != null) {
            holder.time.setText(collectorTask.getCreatedTime());
        } else {
            holder.time.setText("");
        }
        if (collectorTask.getCompletedTime() != null) {
            holder.status.setText("已完成");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_gray));
        } else {
            holder.status.setText("运送中");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_green));
        }
        if (collectorTask.getOriginCity() != null) {
            holder.originCity.setText(collectorTask.getOriginCity());
        } else {
            holder.originCity.setText("-");
        }
        if (collectorTask.getDestinationCity() != null) {
            holder.destinationCity.setText(collectorTask.getDestinationCity());
        } else {
            holder.destinationCity.setText("-");
        }
        if (collectorTask.getCode() != null) {
            holder.code.setText(collectorTask.getCode());
        } else {
            holder.code.setText("");
        }
        if (collectorTask.getName() != null) {
            holder.name.setText(collectorTask.getName());
        } else {
            holder.name.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return collectorTaskList.size();
    }
}
