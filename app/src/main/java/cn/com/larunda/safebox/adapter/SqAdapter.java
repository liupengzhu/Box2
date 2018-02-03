package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import cn.com.larunda.safebox.MyApplication;
import com.larunda.safebox.R;
import cn.com.larunda.safebox.recycler.MySq;

import java.util.List;

/**
 * Created by sddt on 18-1-15.
 */

public class SqAdapter extends RecyclerView.Adapter<SqAdapter.ViewHolder> {
    private List<MySq> sqList;
    private Context context;
    private QrOnClickListener qrOnClickListener;
    private QxOnClickListener qxOnClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        de.hdodenhof.circleimageview.CircleImageView userImg;
        TextView userName;
        TextView xlh;
        TextView date;
        Button qrButton;
        Button cancelButton;


        public ViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.y_list_img);
            userName = itemView.findViewById(R.id.y_user_name_text);
            xlh = itemView.findViewById(R.id.y_xlh_text);
            date = itemView.findViewById(R.id.y_time_text);
            qrButton = itemView.findViewById(R.id.y_qr_button);
            cancelButton = itemView.findViewById(R.id.y_cancel_button);

        }
    }

    public SqAdapter(List<MySq> sqList) {
        this.sqList = sqList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.y_list_item, parent, false);
        context = parent.getContext();
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                MySq sq = sqList.get(position);

                if (qrOnClickListener != null) {
                    qrOnClickListener.onClick(v, sq);
                }

            }
        });
        viewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                MySq sq = sqList.get(position);
                if (qxOnClickListener != null) {
                    qxOnClickListener.onClick(v, sq);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MySq sq = sqList.get(position);
        Glide.with(MyApplication.getContext()).load(sq.getUserImg()).error(R.mipmap.user_img).into(holder.userImg);
        holder.userName.setText(sq.getUserName());
        holder.xlh.setText(sq.getUserXLH());
        holder.date.setText(sq.getDate());

    }

    @Override
    public int getItemCount() {
        return sqList.size();
    }


    public interface QrOnClickListener {
        void onClick(View v, MySq sq);
    }

    public interface QxOnClickListener {
        void onClick(View v, MySq sq);
    }


    public void setQxOnClickListener(QxOnClickListener qxOnClickListener) {
        this.qxOnClickListener = qxOnClickListener;
    }

    public void setQrOnClickListener(QrOnClickListener qrOnClickListener) {
        this.qrOnClickListener = qrOnClickListener;
    }
}
