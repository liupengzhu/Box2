package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.Destination;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {
    private Context context;
    private List<Destination> destinationList = new ArrayList<>();
    private ItemButtonOnclickListener itemButtonOnclickListener;
    private ItemBoxButtonOnclickListener itemBoxButtonOnclickListener;

    public DestinationAdapter(Context context, List<Destination> destinationList) {
        this.context = context;
        this.destinationList = destinationList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rankText;
        private TextView statusText;
        private TextView originText;
        private TextView destinationText;
        private TextView personText;
        private TextView timeText;
        private TextView personButton;
        private TextView boxButton;
        private LinearLayout button;

        private TextView intervalText;
        private TextView areaText;

        private TextView leavingText;
        private TextView defenceText;

        public ViewHolder(View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.destination_rank);
            statusText = itemView.findViewById(R.id.destination_status);
            originText = itemView.findViewById(R.id.destination_origin_city);
            destinationText = itemView.findViewById(R.id.destination_destination_city);
            personText = itemView.findViewById(R.id.destination_person);
            timeText = itemView.findViewById(R.id.destination_time);
            personButton = itemView.findViewById(R.id.destination_person_button);
            boxButton = itemView.findViewById(R.id.destination_box_button);
            button = itemView.findViewById(R.id.destination_button);

            intervalText = itemView.findViewById(R.id.destination_interval);
            areaText = itemView.findViewById(R.id.destination_area);
            leavingText = itemView.findViewById(R.id.destination_leaving);
            defenceText = itemView.findViewById(R.id.destination_defence);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Destination destination = destinationList.get(position);
        holder.rankText.setText("任务" + (position + 1));
        holder.personButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemButtonOnclickListener != null) {
                    itemButtonOnclickListener.onClick(v, destination.getId());
                }
            }
        });
        holder.boxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemBoxButtonOnclickListener != null) {
                    itemBoxButtonOnclickListener.onClick(v, destination.getId());
                }
            }
        });
        if (destination.getOriginCity() != null) {
            holder.originText.setText(destination.getOriginCity());
        } else {
            holder.originText.setText("");
        }
        if (destination.getDestinationCity() != null) {
            holder.destinationText.setText(destination.getDestinationCity());
        } else {
            holder.destinationText.setText("");
        }
        if (destination.getPerson() != null) {
            holder.personText.setText(destination.getPerson());
        } else {
            holder.personText.setText("");
        }
        if (destination.getStartTime() != null) {
            holder.timeText.setText(destination.getStartTime());
        } else {
            holder.timeText.setText("");
        }
        if (destination.getEndTime() != null) {
            holder.statusText.setText("已完成");
            holder.button.setVisibility(View.GONE);
            holder.statusText.setBackground(context.getResources().getDrawable(R.drawable.text_gray));
        } else {
            holder.statusText.setText("运送中");
            holder.button.setVisibility(View.VISIBLE);
            holder.statusText.setBackground(context.getResources().getDrawable(R.drawable.text_green));
        }
        holder.intervalText.setText(destination.getInterval() != null ? destination.getInterval() : "");
        holder.areaText.setText(destination.getArea() != null ? destination.getArea() : "");
        holder.leavingText.setText(destination.getUseLeaving() == null ? "继承任务配置"
                : (destination.getUseLeaving().equals("0") ? "关"
                : "开"));

        holder.defenceText.setText(destination.getUseDefence() == null ? "继承任务配置"
                : (destination.getUseDefence().equals("0") ? "关"
                : "开"));
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    public interface ItemButtonOnclickListener {
        void onClick(View v, int id);
    }

    public interface ItemBoxButtonOnclickListener {
        void onClick(View v, int id);
    }

    public void setItemButtonOnclickListener(ItemButtonOnclickListener itemButtonOnclickListener) {
        this.itemButtonOnclickListener = itemButtonOnclickListener;
    }

    public List<Destination> getDestinationList() {
        return destinationList;
    }

    public void setItemBoxButtonOnclickListener(ItemBoxButtonOnclickListener itemBoxButtonOnclickListener) {
        this.itemBoxButtonOnclickListener = itemBoxButtonOnclickListener;
    }
}
