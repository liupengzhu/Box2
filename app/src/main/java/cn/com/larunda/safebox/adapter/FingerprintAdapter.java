package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private FingerprintOnLongClickListener fingerprintOnLongClickListener;
    private boolean isCheckedLayout = false;

    public FingerprintAdapter(Context context, List<Fingerprint> fingerprintList) {
        this.context = context;
        this.fingerprintList = fingerprintList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView timeText;

        ImageView checked_button;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.fingerprint_item_layout);
            timeText = itemView.findViewById(R.id.fingerprint_item_time);
            checked_button = itemView.findViewById(R.id.fingerprint_check_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fingerprint_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (fingerprintOnLongClickListener != null) {
                    fingerprintOnLongClickListener.onClick(v);
                    return true;
                }
                return false;
            }
        });
        /**
         * 设置多选按钮的单击事件 改变当前条目的选中状态
         */
        viewHolder.checked_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Fingerprint fingerprint = fingerprintList.get(position);
                if (fingerprint.isImgIsChecked()) {
                    fingerprint.setImgIsChecked(false);
                    viewHolder.checked_button.setImageResource(R.mipmap.unchecked);
                } else {
                    fingerprint.setImgIsChecked(true);
                    viewHolder.checked_button.setImageResource(R.mipmap.checked);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Fingerprint fingerprint = fingerprintList.get(position);

        /**
         * 判断当前是否是长按状态设置多选按钮是否可见
         */
        if (isCheckedLayout) {
            holder.checked_button.setVisibility(View.VISIBLE);
        } else {
            holder.checked_button.setVisibility(View.GONE);
        }
        /**
         * 判断当前按钮是否是选中状态 改变图标
         */
        if (fingerprint.isImgIsChecked()) {
            holder.checked_button.setImageResource(R.mipmap.checked);
        } else {
            holder.checked_button.setImageResource(R.mipmap.unchecked);
        }

        if (fingerprint.getTime() != null) {
            holder.timeText.setText(fingerprint.getTime());
        }

    }

    @Override
    public int getItemCount() {
        return fingerprintList.size();
    }

    public interface FingerprintOnLongClickListener {
        void onClick(View v);
    }

    public void setFingerprintOnLongClickListener(FingerprintOnLongClickListener fingerprintOnLongClickListener) {
        this.fingerprintOnLongClickListener = fingerprintOnLongClickListener;
    }

    public void setCheckedLayout(boolean checkedLayout) {
        isCheckedLayout = checkedLayout;
    }

    public List<Fingerprint> getFingerprintList() {
        return fingerprintList;
    }
}
