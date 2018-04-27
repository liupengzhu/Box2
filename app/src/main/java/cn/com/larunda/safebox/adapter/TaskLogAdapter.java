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

import cn.com.larunda.safebox.recycler.TaskLog;

public class TaskLogAdapter extends RecyclerView.Adapter<TaskLogAdapter.ViewHolder> {
    private Context context;
    private List<TaskLog> taskLogList = new ArrayList<>();

    public TaskLogAdapter(Context context, List<TaskLog> taskLogList) {
        this.context = context;
        this.taskLogList = taskLogList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private TextView process;
        private TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_task_log_title);
            content = itemView.findViewById(R.id.item_task_log_content);
            process = itemView.findViewById(R.id.item_task_log_process);
            time = itemView.findViewById(R.id.item_task_log_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskLog taskLog = taskLogList.get(position);
        if (taskLog.getTitle() != null) {
            holder.title.setText(taskLog.getTitle());
        } else {
            holder.title.setText("");
        }
        if (taskLog.getContent() != null) {
            holder.content.setText(taskLog.getContent());
        } else {
            holder.content.setText("");
        }
        if (taskLog.getProcess() != null) {
            holder.process.setText(taskLog.getProcess());
        } else {
            holder.process.setText("");
        }
        if (taskLog.getTime() != null) {
            holder.time.setText(taskLog.getTime());
        } else {
            holder.time.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return taskLogList.size();
    }
}
