package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.larunda.safebox.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.recycler.Company;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {
    private Context context;
    private List<Company> companyList = new ArrayList<>();
    private CompanyLayoutOnclick layoutOnclick;
    private CompanyButtonOnclick buttonOnclick;
    private CompanyDeleteButtonOnclick deleteButtonOnclick;

    public CompanyAdapter(Context context, List<Company> companyList) {
        this.context = context;
        this.companyList = companyList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView name;
        TextView tel;
        TextView address;
        TextView letter;
        TextView fax;
        TextView salesAddress;
        TextView email;
        TextView contacts;
        LinearLayout layout;
        TextView button;
        TextView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.item_company_pic);
            name = itemView.findViewById(R.id.item_company_name);
            tel = itemView.findViewById(R.id.item_company_tel);
            address = itemView.findViewById(R.id.item_company_add);
            letter = itemView.findViewById(R.id.item_company_letter);
            fax = itemView.findViewById(R.id.item_company_fax);
            salesAddress = itemView.findViewById(R.id.item_company_sales_add);
            email = itemView.findViewById(R.id.item_company_email);
            contacts = itemView.findViewById(R.id.item_company_contacts);
            layout = itemView.findViewById(R.id.item_company_layout);
            button = itemView.findViewById(R.id.item_company_button);
            deleteButton = itemView.findViewById(R.id.item_company_delete_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_company, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Company company = companyList.get(position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutOnclick != null) {
                    int id = companyList.get(position).getId();
                    layoutOnclick.onClick(v, id);
                }
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonOnclick != null) {
                    int id = companyList.get(position).getId();
                    buttonOnclick.onclick(v, id);
                }
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteButtonOnclick != null) {
                    int id = companyList.get(position).getId();
                    String name = companyList.get(position).getName();
                    deleteButtonOnclick.onclick(v, id, name);
                }
            }
        });
        Glide.with(context).load(company.getPic())
                .placeholder(R.drawable.company_bull)
                .error(R.drawable.company_bull)
                .dontAnimate()
                .into(holder.pic);
        if (company.getName() != null) {
            holder.name.setText(company.getName());
        } else {
            holder.name.setText("");
        }
        if (company.getTel() != null) {
            holder.tel.setText(company.getTel());
        } else {
            holder.tel.setText("");
        }
        if (company.getAddress() != null) {
            holder.address.setText(company.getAddress());
        } else {
            holder.address.setText("");
        }
        if (company.getLetter() != null) {
            holder.letter.setText(company.getLetter());
        } else {
            holder.letter.setText("");
        }
        if (company.getSalesAddress() != null) {
            holder.salesAddress.setText(company.getSalesAddress());
        } else {
            holder.salesAddress.setText("");
        }
        if (company.getEmail() != null) {
            holder.email.setText(company.getEmail());
        } else {
            holder.email.setText("");
        }
        if (company.getContacts() != null) {
            holder.contacts.setText(company.getContacts());
        } else {
            holder.contacts.setText("");
        }
        if (company.getFax() != null) {
            holder.fax.setText(company.getFax());
        } else {
            holder.fax.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public interface CompanyLayoutOnclick {
        void onClick(View v, int id);
    }

    public interface CompanyButtonOnclick {
        void onclick(View v, int id);
    }

    public interface CompanyDeleteButtonOnclick {
        void onclick(View v, int id, String name);
    }

    public void setLayoutOnclick(CompanyLayoutOnclick layoutOnclick) {
        this.layoutOnclick = layoutOnclick;
    }

    public void setButtonOnclick(CompanyButtonOnclick buttonOnclick) {
        this.buttonOnclick = buttonOnclick;
    }

    public void setDeleteButtonOnclick(CompanyDeleteButtonOnclick deleteButtonOnclick) {
        this.deleteButtonOnclick = deleteButtonOnclick;
    }
}
