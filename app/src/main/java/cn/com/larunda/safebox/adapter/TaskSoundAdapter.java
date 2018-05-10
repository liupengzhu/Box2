package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.DetailedSound;
import cn.com.larunda.safebox.recycler.TaskSound;

/**
 * Created by sddt on 18-1-25.
 */

public class TaskSoundAdapter extends RecyclerView.Adapter<TaskSoundAdapter.ViewHolder> {
    private Context context;
    private List<TaskSound> taskSoundList = new ArrayList<>();
    private TaskSoundOnClickListener taskSoundOnClickListener;

    public TaskSoundAdapter(Context context, List<TaskSound> taskSoundList) {
        this.context = context;
        this.taskSoundList = taskSoundList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView soundId;
        TextView soundTime;
        CheckBox playButton;

        public ViewHolder(View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.item_task_sound_play);
            soundId = itemView.findViewById(R.id.item_task_sound_id);
            soundTime = itemView.findViewById(R.id.item_task_sound_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_sound, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //viewHolder.isDownloadView.setVisibility(View.GONE);
                TaskSound taskSound = taskSoundList.get(viewHolder.getAdapterPosition());
                if(taskSoundOnClickListener!=null){
                    taskSoundOnClickListener.onClick(v,taskSound.getPath(),taskSound.getId(),true);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskSound taskSound = taskSoundList.get(position);
        holder.soundId.setText(taskSound.getId()+"");
        holder.soundTime.setText(taskSound.getCreateTime());
        /*if(TaskSound.isDownload()){
            holder.isDownloadView.setVisibility(View.GONE);
        }else {
            holder.isDownloadView.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public int getItemCount() {
        return taskSoundList.size();
    }

    public interface TaskSoundOnClickListener {
        void onClick(View view, String path, int id, boolean isExist);
    }

    public void setTaskSoundOnClickListener(TaskSoundOnClickListener taskSoundOnClickListener) {
        this.taskSoundOnClickListener = taskSoundOnClickListener;
    }
}
