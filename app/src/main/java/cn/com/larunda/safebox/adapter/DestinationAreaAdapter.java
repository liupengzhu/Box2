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

import cn.com.larunda.safebox.recycler.Area;

public class DestinationAreaAdapter extends RecyclerView.Adapter<DestinationAreaAdapter.ViewHolder> {
    private Context context;
    private List<Area> areaList = new ArrayList<>();

    public DestinationAreaAdapter(Context context, List<Area> areaList) {
        this.context = context;
        this.areaList = areaList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView type;
        private TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_destination_name);
            type = itemView.findViewById(R.id.item_destination_type);
            time = itemView.findViewById(R.id.item_destination_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_destination_area, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Area area = areaList.get(position);
        holder.name.setText(area.getName() + "");
        holder.time.setText(area.getTime() + "");
        holder.type.setText(area.getType() + "");
    }

    @Override
    public int getItemCount() {
        return areaList.size();
    }
}
