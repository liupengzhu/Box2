package cn.com.larunda.safebox.adapter;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
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
        Button button;

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
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_company, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Company company = companyList.get(position);
        if (company.getPic() != null) {
            Glide.with(context).load(company.getPic())
                    .placeholder(R.drawable.box_null)
                    .error(R.drawable.box_null).into(holder.pic);
        } else {
            holder.pic.setImageDrawable(context.getResources().getDrawable(R.drawable.box_null));
        }
        if (company.getName() != null) {
            holder.name.setText(company.getName());
        }
        if (company.getTel() != null) {
            holder.tel.setText(company.getTel());
        }
        if (company.getAddress() != null) {
            holder.address.setText(company.getAddress());
        }
        if (company.getLetter() != null) {
            holder.letter.setText(company.getLetter());
        }
        if (company.getSalesAddress() != null) {
            holder.salesAddress.setText(company.getSalesAddress());
        }
        if (company.getEmail() != null) {
            holder.email.setText(company.getEmail());
        }
        if (company.getContacts() != null) {
            holder.contacts.setText(company.getContacts());
        }
        if (company.getFax() != null) {
            holder.fax.setText(company.getFax());
        }
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }
}
