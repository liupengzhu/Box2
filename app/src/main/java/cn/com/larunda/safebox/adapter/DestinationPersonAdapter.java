package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.Area;
import cn.com.larunda.safebox.recycler.Person;

public class DestinationPersonAdapter extends RecyclerView.Adapter<DestinationPersonAdapter.ViewHolder> {
    private Context context;
    private List<Person> personList = new ArrayList<>();
    private ItemOnclickListener itemOnclickListener;

    public DestinationPersonAdapter(Context context, List<Person> personList) {
        this.context = context;
        this.personList = personList;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout nameButton;
        private TextView name;
        private EditText password;
        private ImageView img1;
        private ImageView img2;
        private ImageView img3;

        public ViewHolder(View itemView) {
            super(itemView);
            nameButton = itemView.findViewById(R.id.item_destination_person_name_layout);
            name = itemView.findViewById(R.id.item_destination_person_name_text);
            password = itemView.findViewById(R.id.item_destination_person_password_edit);
            img1 = itemView.findViewById(R.id.item_destination_person_img1);
            img2 = itemView.findViewById(R.id.item_destination_person_img2);
            img3 = itemView.findViewById(R.id.item_destination_person_img3);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_destination_person, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                viewHolder.password.setSelection(s.length());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (itemOnclickListener != null) {
                    itemOnclickListener.onTextChanged(s, start, before, count, viewHolder.getAdapterPosition());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Person person = personList.get(position);

        if (person.getName() != null) {
            holder.name.setText(person.getName());
        } else {
            holder.name.setText("");
        }
        if (person.isUseDynamic()) {
            holder.img1.setImageDrawable(context.getResources().getDrawable(R.mipmap.checked));
        } else {
            holder.img1.setImageDrawable(context.getResources().getDrawable(R.mipmap.unchecked));
        }
        if (person.isUseFingerprint()) {
            holder.img2.setImageDrawable(context.getResources().getDrawable(R.mipmap.checked));
        } else {
            holder.img2.setImageDrawable(context.getResources().getDrawable(R.mipmap.unchecked));
        }
        if (person.getPwd() != null) {
            if(person.getPwd().equals("******")){
                holder.password.setHint("******");
            }else {
                holder.password.setText(person.getPwd());
            }
        } else {
            holder.password.setHint("请输入密码");
        }
        if (person.isUsePwd()) {
            holder.img3.setImageDrawable(context.getResources().getDrawable(R.mipmap.checked));
            holder.password.setVisibility(View.VISIBLE);
        } else {
            holder.img3.setImageDrawable(context.getResources().getDrawable(R.mipmap.unchecked));
            holder.password.setVisibility(View.GONE);
        }

        holder.nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemOnclickListener != null) {
                    itemOnclickListener.nameOnclick(v, position);
                }
            }
        });

        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemOnclickListener != null) {
                    itemOnclickListener.dynamicOnclick(v, position);
                }
            }
        });
        holder.img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemOnclickListener != null) {
                    itemOnclickListener.fingerprintOnclick(v, position);
                }
            }
        });
        holder.img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemOnclickListener != null) {
                    itemOnclickListener.pwdOnclick(v, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public interface ItemOnclickListener {
        void nameOnclick(View v, int position);

        void dynamicOnclick(View v, int position);

        void fingerprintOnclick(View v, int position);

        void pwdOnclick(View v, int position);

        void onTextChanged(CharSequence s, int start, int before, int count, int position);
    }

    public void setItemOnclickListener(ItemOnclickListener itemOnclickListener) {
        this.itemOnclickListener = itemOnclickListener;
    }
}
