package com.example.box.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.box.AddEnclosureActivity;
import com.example.box.EnclosureActivity;
import com.example.box.R;
import com.example.box.TrackActivity;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxMessageSafeFragment extends Fragment implements View.OnClickListener {

    RelativeLayout enclosureButton;
    RelativeLayout trackButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_message_safe_fragment, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {
        enclosureButton = view.findViewById(R.id.box_message_enclosure);
        trackButton = view.findViewById(R.id.box_message_track);

        trackButton.setOnClickListener(this);
        enclosureButton.setOnClickListener(this);
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_message_enclosure:
                Intent intent = new Intent(getContext(), AddEnclosureActivity.class);
                startActivity(intent);
                break;
            case R.id.box_message_track:
                Intent trackIntent = new Intent(getContext(), TrackActivity.class);
                startActivity(trackIntent);
                break;
            default:
                break;

        }

    }
}
