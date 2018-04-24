package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.Box;

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ViewHolder> {
    private Context context;
    private List<Box> boxList = new ArrayList<>();

    public BoxAdapter(Context context, List<Box> boxList) {
        this.context = context;
        this.boxList = boxList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layout;
        private TextView name;
        private TextView code;
        private TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.box_layout);
            name = itemView.findViewById(R.id.box_name);
            code = itemView.findViewById(R.id.box_code);
            status = itemView.findViewById(R.id.box_status);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_box_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Box box = boxList.get(position);
        if (box.getName() != null) {
            holder.name.setText(box.getName());
        } else {
            holder.name.setText("");
        }
        if (box.getCode() != null) {
            holder.code.setText(box.getCode());
        } else {
            holder.code.setText("");
        }
        if (box.getStatus() != null && box.getStatus().equals("0")) {
            holder.status.setText("离线");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_gray));
        } else if (box.getStatus() != null && box.getStatus().equals("1")) {
            holder.status.setText("正常");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_green));
        } else if (box.getStatus() != null && box.getStatus().equals("2")) {
            holder.status.setText("警报");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_yellow));
        } else if (box.getStatus() != null && box.getStatus().equals("3")) {
            holder.status.setText("异常");
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.text_red));
        }
    }

    @Override
    public int getItemCount() {
        return boxList.size();
    }
}
