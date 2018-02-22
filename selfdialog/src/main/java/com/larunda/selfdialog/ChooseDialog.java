package com.larunda.selfdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sddt on 18-1-18.
 */

public class ChooseDialog extends Dialog {


    private OnClickListener onClickListener;
    private List<String> datas = new ArrayList<>();
    private ChooseAdapter chooseAdapter;
    private ListView listView;

    public ChooseDialog(@NonNull Context context, List<String> datas) {
        super(context, R.style.MyDialog);
        this.datas = datas;

    }


    /**
     * 创建dialog的时候调用的方法
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_dialog);
        initView();

        /**
         * 动态改变listview的高
         */
        RelativeLayout.LayoutParams list = (RelativeLayout.LayoutParams) listView.getLayoutParams();
        if (datas.size() > 6) {
            list.height = dp2px(340);
        } else {
            list.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }


        //初始化界面控件的事件
        initEvent();


    }


    /**
     * 控件点击事件监听
     */
    private void initEvent() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onClickListener != null) {
                    onClickListener.OnClick(view, position);
                }
            }
        });

    }

    /**
     * 初始化View
     */
    private void initView() {
        chooseAdapter = new ChooseAdapter(datas, getContext());
        listView = findViewById(R.id.choose_list_view);
        listView.setAdapter(chooseAdapter);
        listView.setVerticalScrollBarEnabled(false);
    }


    public interface OnClickListener {
        void OnClick(View v, int positon);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getContext().getResources().getDisplayMetrics());
    }

}
