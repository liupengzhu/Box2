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

import cn.com.larunda.safebox.recycler.BindArea;

/**
 * Created by sddt on 18-1-31.
 */

public class BindAreaAdapter extends RecyclerView.Adapter<BindAreaAdapter.ViewHolder> {
    private Context context;
    private List<BindArea> bindAreaList = new ArrayList<>();

    public BindAreaAdapter(Context context, List<BindArea> bindAreaList) {
        this.context = context;
        this.bindAreaList = bindAreaList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView inOrOut;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.area_item_area_text);
            time = itemView.findViewById(R.id.area_item_time_text);
            inOrOut = itemView.findViewById(R.id.area_item_position_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.area_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BindArea bindArea = bindAreaList.get(position);
        holder.name.setText(bindArea.getName());
        holder.time.setText(bindArea.getTime());
        holder.inOrOut.setText(bindArea.getIn_or_out());
    }

    @Override
    public int getItemCount() {
        return bindAreaList.size();
    }
}
