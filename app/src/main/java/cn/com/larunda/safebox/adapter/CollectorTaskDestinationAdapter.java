package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.Destination;

public class CollectorTaskDestinationAdapter extends RecyclerView.Adapter<CollectorTaskDestinationAdapter.ViewHolder> {
    private Context context;
    private List<Destination> destinationList = new ArrayList<>();
    private ItemButtonOnclickListener itemButtonOnclickListener;

    public CollectorTaskDestinationAdapter(Context context, List<Destination> destinationList) {
        this.context = context;
        this.destinationList = destinationList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView originText;
        private TextView destinationText;
        private TextView personText;
        private TextView timeText;
        private Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            originText = itemView.findViewById(R.id.collector_task_destination_origin_city);
            destinationText = itemView.findViewById(R.id.collector_task_destination_destination_city);
            personText = itemView.findViewById(R.id.collector_task_destination_person);
            timeText = itemView.findViewById(R.id.collector_task_destination_time);
            button = itemView.findViewById(R.id.collector_task_destination_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collector_task_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Destination destination = destinationList.get(position);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemButtonOnclickListener != null) {
                    itemButtonOnclickListener.onClick(v, destination.getId());
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
        if (destination.getDynamic() != null && destination.getDynamic().equals("1")) {
            holder.button.setVisibility(View.VISIBLE);
        } else {
            holder.button.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    public interface ItemButtonOnclickListener {
        void onClick(View v, int id);
    }

    public void setItemButtonOnclickListener(ItemButtonOnclickListener itemButtonOnclickListener) {
        this.itemButtonOnclickListener = itemButtonOnclickListener;
    }

    public List<Destination> getDestinationList() {
        return destinationList;
    }
}
