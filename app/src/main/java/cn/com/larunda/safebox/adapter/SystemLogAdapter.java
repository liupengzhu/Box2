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

import cn.com.larunda.safebox.recycler.SystemLog;

public class SystemLogAdapter extends RecyclerView.Adapter<SystemLogAdapter.ViewHolder> {
    private Context context;
    private List<SystemLog> systemLogList = new ArrayList<>();

    public SystemLogAdapter(Context context, List<SystemLog> systemLogList) {
        this.context = context;
        this.systemLogList = systemLogList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private TextView type;
        private TextView time;
        private TextView user;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_system_log_title);
            content = itemView.findViewById(R.id.item_system_log_content);
            type = itemView.findViewById(R.id.item_system_log_type);
            time = itemView.findViewById(R.id.item_system_log_time);
            user = itemView.findViewById(R.id.item_system_log_person);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_system_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SystemLog systemLog = systemLogList.get(position);
        if (systemLog.getTitle() != null) {
            holder.title.setText(systemLog.getTitle());
        } else {
            holder.title.setText("");
        }
        if (systemLog.getContent() != null) {
            holder.content.setText(systemLog.getContent());
        } else {
            holder.content.setText("");
        }
        if (systemLog.getTime() != null) {
            holder.time.setText(systemLog.getTime());
        } else {
            holder.time.setText("");
        }
        if (systemLog.getType() != null) {
            holder.type.setText(systemLog.getType());
        } else {
            holder.type.setText("");
        }
        if (systemLog.getUer() != null) {
            holder.user.setText(systemLog.getUer());
        } else {
            holder.user.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return systemLogList.size();
    }
}
