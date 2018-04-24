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

import cn.com.larunda.safebox.recycler.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Context context;
    private List<Task> taskList = new ArrayList<>();
    private ItemOnclickListener itemOnclickListener;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time;
        private TextView originCity;
        private TextView destinationCity;
        private TextView status;
        private LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.task_time);
            originCity = itemView.findViewById(R.id.task_origin_city);
            destinationCity = itemView.findViewById(R.id.task_destination_city);
            status = itemView.findViewById(R.id.task_status);
            layout = itemView.findViewById(R.id.task_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Task task = taskList.get(position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemOnclickListener != null) {
                    itemOnclickListener.onClick(v, task.getId(), task.getName(), task.getCreatedTime(), task.getCompletedTime());
                }
            }
        });
        if (task.getCreatedTime() != null) {
            holder.time.setText(task.getCreatedTime());
        } else {
            holder.time.setText("");
        }
        if (task.getOriginCity() != null) {
            holder.originCity.setText(task.getOriginCity());
        } else {
            holder.originCity.setText("-");
        }
        if (task.getDestinationCity() != null) {
            holder.destinationCity.setText(task.getDestinationCity());
        } else {
            holder.destinationCity.setText("-");
        }
        if (task.getCompletedTime() != null) {
            holder.status.setText("已完成");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_gray));
        } else {
            holder.status.setText("运送中");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_green));
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public interface ItemOnclickListener {
        void onClick(View v, int id, String name, String createTime, String completedTime);
    }

    public void setItemOnclickListener(ItemOnclickListener itemOnclickListener) {
        this.itemOnclickListener = itemOnclickListener;
    }
}
