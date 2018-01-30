package cn.com.larunda.safebox.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.recycler.Enclosure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-17.
 */

public class EnclosureAdapter extends RecyclerView.Adapter<EnclosureAdapter.ViewHolder> {

    private List<Enclosure> enclosureList = new ArrayList<>();

    private EnclosureLongClickListener enclosureLongClickListener;
    private boolean isCheckedLayout = false;

    private EnclosureOnClickListener enclosureOnClickListener;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView checked_button;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.enclosure_name);
            checked_button = itemView.findViewById(R.id.enclosure_check_button);
            layout = itemView.findViewById(R.id.enclosure_item_layout);
        }
    }

    public EnclosureAdapter(List<Enclosure> enclosureList) {
        this.enclosureList = enclosureList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enclosure_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        /**
         * 设置子条目的长按点击事件
         */
        viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (enclosureLongClickListener != null) {
                    enclosureLongClickListener.onLongClick(v);
                    return true;
                }
                return false;
            }
        });
        /**
         * 子条目的单击事件
         */
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enclosureOnClickListener != null) {
                    Enclosure enclosure = enclosureList.get(viewHolder.getAdapterPosition());
                    if (enclosure.getId() != null) {
                        enclosureOnClickListener.onClick(v, enclosure.getId());
                    }
                }
            }
        });


        /**
         * 设置多选按钮的单击事件 改变当前条目的选中状态
         */
        viewHolder.checked_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Enclosure enclosure = enclosureList.get(position);
                if (enclosure.isImgIsChecked()) {
                    enclosure.setImgIsChecked(false);
                    viewHolder.checked_button.setImageResource(R.mipmap.unchecked);
                } else {
                    enclosure.setImgIsChecked(true);
                    viewHolder.checked_button.setImageResource(R.mipmap.checked);
                }
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Enclosure enclosure = enclosureList.get(position);
        holder.name.setText(enclosure.getName());
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
        if (enclosure.isImgIsChecked()) {
            holder.checked_button.setImageResource(R.mipmap.checked);
        } else {
            holder.checked_button.setImageResource(R.mipmap.unchecked);
        }
    }

    @Override
    public int getItemCount() {
        return enclosureList.size();
    }

    public void setOnLongClickListener(EnclosureLongClickListener enclosureLongClickListener) {
        this.enclosureLongClickListener = enclosureLongClickListener;
    }

    public interface EnclosureLongClickListener {
        void onLongClick(View v);
    }

    public interface EnclosureOnClickListener {
        void onClick(View v, String id);
    }

    public boolean isCheckedLayout() {
        return isCheckedLayout;
    }

    public void setCheckedLayout(boolean checkedLayout) {
        isCheckedLayout = checkedLayout;
    }

    public List<Enclosure> getEnclosureList() {
        return enclosureList;
    }

    public void setEnclosureOnClickListener(EnclosureOnClickListener enclosureOnClickListener) {
        this.enclosureOnClickListener = enclosureOnClickListener;
    }
}
