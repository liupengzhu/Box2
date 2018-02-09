package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.larunda.horizontalprogressbar.HorizontalProgressBarWithNunber;
import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.BoxInfoSound;

/**
 * Created by sddt on 18-1-26.
 */

public class BoxInfoSoundAdapter extends RecyclerView.Adapter<BoxInfoSoundAdapter.ViewHolder> {
    private Context context;
    private List<BoxInfoSound> boxInfoSoundList = new ArrayList<>();
    private BoxInfoSoundOnClickListener boxInfoSoundOnClickListener;

    public BoxInfoSoundAdapter(Context context, List<BoxInfoSound> boxInfoSoundList) {
        this.context = context;
        this.boxInfoSoundList = boxInfoSoundList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView soundName;
        TextView soundTime;
        TextView soundDate;
        TextView totalTime;
        CheckBox playButton;

        TextView currentTime;
        HorizontalProgressBarWithNunber progress;

        public ViewHolder(View itemView) {
            super(itemView);
            soundName = itemView.findViewById(R.id.box_info_sound_text);
            soundTime = itemView.findViewById(R.id.box_info_sound_time_text);
            soundDate = itemView.findViewById(R.id.box_info_sound_date_text);
            totalTime = itemView.findViewById(R.id.box_info_sound_total_time);
            playButton = itemView.findViewById(R.id.box_info_sound_play_button);
            currentTime = itemView.findViewById(R.id.box_info_sound_current_time);
            progress = itemView.findViewById(R.id.box_info_sound_progress);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.box_info_sound_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoxInfoSound sound = boxInfoSoundList.get(viewHolder.getAdapterPosition());
                if (boxInfoSoundOnClickListener != null) {
                    boxInfoSoundOnClickListener.onClick(v, sound.getPath(), sound.getId(), sound.isIs_exist(),viewHolder.getAdapterPosition());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BoxInfoSound sound = boxInfoSoundList.get(position);
        holder.soundName.setText(sound.getId());
        holder.soundDate.setText(sound.getSoundDate());
        holder.soundTime.setText(sound.getSoundTime());
        holder.totalTime.setText(sound.getSoundTime());
    }

    @Override
    public int getItemCount() {
        return boxInfoSoundList.size();
    }

    public interface BoxInfoSoundOnClickListener {
        void onClick(View view, String path, String id, boolean isExist,int position);
    }

    public void setBoxInfoSoundOnClickListener(BoxInfoSoundOnClickListener boxInfoSoundOnClickListener) {
        this.boxInfoSoundOnClickListener = boxInfoSoundOnClickListener;
    }
}
