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

import cn.com.larunda.safebox.recycler.TaskWarning;

public class TaskWarningAdapter extends RecyclerView.Adapter<TaskWarningAdapter.ViewHolder> {
    private Context context;
    private List<TaskWarning> taskWarningList = new ArrayList<>();

    public TaskWarningAdapter(Context context, List<TaskWarning> taskWarningList) {
        this.context = context;
        this.taskWarningList = taskWarningList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView type;
        private TextView status;
        private TextView process;
        private TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_task_warning_title);
            type = itemView.findViewById(R.id.item_task_warning_content);
            status = itemView.findViewById(R.id.item_task_warning_status);
            process = itemView.findViewById(R.id.item_task_warning_process);
            time = itemView.findViewById(R.id.item_task_warning_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_warning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskWarning taskWarning = taskWarningList.get(position);
        if (taskWarning.getTitle() != null) {
            holder.title.setText(taskWarning.getTitle());
        } else {
            holder.title.setText("");
        }

        if (taskWarning.getTime() != null) {
            holder.time.setText(taskWarning.getTime());
        } else {
            holder.time.setText("");
        }

        if (taskWarning.getContent() != null) {
            holder.type.setText(taskWarning.getContent());
        } else {
            holder.type.setText("");
        }

        if (taskWarning.getStatus() != null) {
            holder.status.setText(taskWarning.getStatus());
        } else {
            holder.status.setText("");
        }

        if (taskWarning.getProcess() != null) {
            holder.process.setText(taskWarning.getProcess());
        } else {
            holder.process.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return taskWarningList.size();
    }
}
