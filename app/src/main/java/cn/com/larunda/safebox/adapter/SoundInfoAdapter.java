package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.recycler.SoundInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-25.
 */

public class SoundInfoAdapter extends RecyclerView.Adapter<SoundInfoAdapter.ViewHolder> {
    private List<SoundInfo> soundInfoList = new ArrayList<>();
    private Context context;
    private SoundInfoOnClickListener soundInfoOnClickListener;

    public SoundInfoAdapter(Context context, List<SoundInfo> soundInfoList) {
        this.soundInfoList = soundInfoList;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView totalText;
        RelativeLayout button;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.sound_name_text);
            totalText = itemView.findViewById(R.id.sound_data_text);
            button = itemView.findViewById(R.id.sound_item_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sound_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundInfoOnClickListener != null) {
                    SoundInfo soundInfo = soundInfoList.get(viewHolder.getAdapterPosition());
                    if (soundInfo.getId() != null) {
                        soundInfoOnClickListener.onClick(v, soundInfo.getId(),soundInfo.getBox_img(),soundInfo.getBoxName());
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SoundInfo soundInfo = soundInfoList.get(position);
        holder.nameText.setText(soundInfo.getBoxName());
        holder.totalText.setText(soundInfo.getTotal());
    }

    @Override
    public int getItemCount() {
        return soundInfoList.size();
    }

    public interface SoundInfoOnClickListener {
        void onClick(View view, String id ,String img ,String code);
    }

    public void setSoundInfoOnClickListener(SoundInfoOnClickListener soundInfoOnClickListener) {
        this.soundInfoOnClickListener = soundInfoOnClickListener;
    }
}
