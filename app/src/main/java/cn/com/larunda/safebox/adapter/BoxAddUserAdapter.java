package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.MyApplication;
import cn.com.larunda.safebox.recycler.BoxAddUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sddt on 18-1-26.
 */

public class BoxAddUserAdapter extends RecyclerView.Adapter<BoxAddUserAdapter.ViewHolder> {
    private Context context;
    private List<BoxAddUser> boxAddUserList = new ArrayList<>();

    public BoxAddUserAdapter(Context context, List<BoxAddUser> boxAddUserList) {
        this.context = context;
        this.boxAddUserList = boxAddUserList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView company;
        TextView department;
        TextView phone;
        CircleImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.box_add_user_item_dw_name_text);
            company = itemView.findViewById(R.id.box_add_user_item_dw_text);
            department = itemView.findViewById(R.id.box_add_user_item_bm_text);
            phone = itemView.findViewById(R.id.box_add_user_item_dw_phone_text);
            img = itemView.findViewById(R.id.box_add_user_item_img);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.box_add_user_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BoxAddUser boxAddUser = boxAddUserList.get(position);
        if (boxAddUser.getPic() != null) {
            Glide.with(context).load(boxAddUser.getPic()).into(holder.img);
        }
        holder.name.setText(boxAddUser.getName());
        holder.company.setText(boxAddUser.getCompany());
        holder.department.setText(boxAddUser.getDepartment());
        holder.phone.setText(boxAddUser.getPhone());
    }

    @Override
    public int getItemCount() {
        return boxAddUserList.size();
    }
}
