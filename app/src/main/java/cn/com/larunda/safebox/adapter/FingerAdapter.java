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

import cn.com.larunda.safebox.recycler.Finger;

public class FingerAdapter extends RecyclerView.Adapter<FingerAdapter.ViewHolder> {
    private Context context;
    private List<Finger> fingerList = new ArrayList<>();

    public FingerAdapter(Context context, List<Finger> fingerList) {
        this.context = context;
        this.fingerList = fingerList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView code;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_task_finger_name);
            code = itemView.findViewById(R.id.item_task_finger_code);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_finger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Finger finger = fingerList.get(position);
        if (finger.getName() != null) {
            holder.name.setText(finger.getName());
        } else {
            holder.name.setText("");
        }
        if (finger.getCode() != null) {
            holder.code.setText(finger.getCode());
        } else {
            holder.code.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return fingerList.size();
    }
}
