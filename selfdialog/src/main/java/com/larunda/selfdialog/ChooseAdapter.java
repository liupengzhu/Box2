package com.larunda.selfdialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-25.
 */

public class ChooseAdapter extends BaseAdapter {
    private List<String> mdatas = new ArrayList<>();
    private Context context;


    public ChooseAdapter(List<String> mdatas, Context context) {
        this.mdatas = mdatas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mdatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mdatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.choose_dialog_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.textView.setText(mdatas.get(position));

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }
}
