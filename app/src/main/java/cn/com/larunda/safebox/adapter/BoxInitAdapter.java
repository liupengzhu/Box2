package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.BoxInit;

/**
 * Created by sddt on 18-2-6.
 */

public class BoxInitAdapter extends RecyclerView.Adapter<BoxInitAdapter.ViewHolder> {
    private Context context;
    private List<BoxInit> boxInitList = new ArrayList<>();
    private BoxInitAdapterOnClickListener boxInitAdapterOnClickListener;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView codeText;
        private TextView timeText;
        private RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            codeText = itemView.findViewById(R.id.box_init_name_text);
            timeText = itemView.findViewById(R.id.box_init_time_text);
            layout = itemView.findViewById(R.id.box_init_item_layout);
        }
    }

    public BoxInitAdapter(Context context, List<BoxInit> boxInitList) {
        this.context = context;
        this.boxInitList = boxInitList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.box_init_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoxInit boxInit = boxInitList.get(viewHolder.getAdapterPosition());
                if (boxInitAdapterOnClickListener != null) {
                    boxInitAdapterOnClickListener.onClick(v, boxInit.getId(), boxInit.getCode());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BoxInit boxInit = boxInitList.get(position);
        holder.timeText.setText(boxInit.getTime());
        holder.codeText.setText(boxInit.getCode());
    }

    @Override
    public int getItemCount() {
        return boxInitList.size();
    }

    public interface BoxInitAdapterOnClickListener {
        void onClick(View v, String id, String code);
    }

    public void setBoxInitAdapterOnClickListener(BoxInitAdapterOnClickListener boxInitAdapterOnClickListener) {
        this.boxInitAdapterOnClickListener = boxInitAdapterOnClickListener;
    }
}
