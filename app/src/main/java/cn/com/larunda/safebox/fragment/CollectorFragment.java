package cn.com.larunda.safebox.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.larunda.safebox.R;

public class CollectorFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collector,container,false);
        initView(view);
        return view;
    }

    /**
     * 初始化view
     * @param view
     */
    private void initView(View view) {

    }
}