package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.Fingerprint;

/**
 * Created by sddt on 18-3-2.
 */

public class FingerprintAdapter extends RecyclerView.Adapter<FingerprintAdapter.ViewHolder> {
    private Context context;
    private List<Fingerprint> fingerprintList = new ArrayList<>();

    public FingerprintAdapter(Context context, List<Fingerprint> fingerprintList) {
        this.context = context;
        this.fingerprintList = fingerprintList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView timeText;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.fingerprint_item_layout);
            timeText = itemView.findViewById(R.id.fingerprint_item_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fingerprint_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Fingerprint fingerprint = fingerprintList.get(position);
        if (fingerprint.getTime() != null) {
            holder.timeText.setText(fingerprint.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return fingerprintList.size();
    }
}
