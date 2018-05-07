package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.Destination;

public class CourierDestinationAdapter extends RecyclerView.Adapter<CourierDestinationAdapter.ViewHolder> {
    private Context context;
    private List<Destination> destinationList = new ArrayList<>();

    public CourierDestinationAdapter(Context context, List<Destination> destinationList) {
        this.context = context;
        this.destinationList = destinationList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView originText;
        private TextView destinationText;
        private TextView personText;
        private TextView timeText;
        private TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            originText = itemView.findViewById(R.id.courier_destination_origin_city);
            destinationText = itemView.findViewById(R.id.courier_destination_destination_city);
            personText = itemView.findViewById(R.id.courier_destination_person);
            timeText = itemView.findViewById(R.id.courier_destination_time);
            status = itemView.findViewById(R.id.courier_destination_status);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_courier_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Destination destination = destinationList.get(position);
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
        if (destination.getEndTime() != null && !TextUtils.isEmpty(destination.getEndTime())) {
            holder.status.setText("已完成");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_gray));
        } else {
            holder.status.setText("未完成");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_green));
        }
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }


    public List<Destination> getDestinationList() {
        return destinationList;
    }
}
