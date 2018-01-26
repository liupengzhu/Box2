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
import cn.com.larunda.safebox.recycler.DetailedSound;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-25.
 */

public class DetailedSoundAdapter extends RecyclerView.Adapter<DetailedSoundAdapter.ViewHolder> {
    private Context context;
    private List<DetailedSound> detailedSoundList = new ArrayList<>();
    private DetailedSoundOnClickListener detailedSoundOnClickListener;

    public DetailedSoundAdapter(Context context, List<DetailedSound> detailedSoundList) {
        this.context = context;
        this.detailedSoundList = detailedSoundList;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView soundId;
        TextView soundTime;
        ImageView isDownloadView;
        CheckBox playButton;
        public ViewHolder(View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.detailed_sound_item_play);
            soundId = itemView.findViewById(R.id.detailed_sound_id);
            soundTime = itemView.findViewById(R.id.detailed_sound_time);
            isDownloadView = itemView.findViewById(R.id.detailed_sound_item_play_img);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detailed_sound_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.isDownloadView.setVisibility(View.GONE);
                if(detailedSoundOnClickListener!=null){
                    detailedSoundOnClickListener.onClick(v);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DetailedSound detailedSound = detailedSoundList.get(position);
        holder.soundId.setText(detailedSound.getSoundId());
        holder.soundTime.setText(detailedSound.getTime());
        if(detailedSound.isDownload()){
            holder.isDownloadView.setVisibility(View.GONE);
        }else {
            holder.isDownloadView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return detailedSoundList.size();
    }

    public interface DetailedSoundOnClickListener{
        void onClick(View view);
    }

    public void setDetailedSoundOnClickListener(DetailedSoundOnClickListener detailedSoundOnClickListener) {
        this.detailedSoundOnClickListener = detailedSoundOnClickListener;
    }
}
