package com.example.box.fragment;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.box.R;
import com.example.box.gson.Home;
import com.example.box.gson.HomeAlarm;
import com.example.box.gson.HomeInfo;
import com.example.box.util.HttpUtil;
import com.example.box.util.Util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/8.
 */

public class HomeFragment extends Fragment {

    public static final String BOX_URI = "http://192.168.1.117:8084/api/app/home";
    public static final String TOKEN = "?_token=f2e51f0386dddff3f9a8fa283074c86df5429134";
    TextView totalView;
    TextView defendView;
    TextView lockedView;
    TextView bindView;

    TextView leaving_alarm_view;
    TextView area_alarm_view;

    ImageView serverState;
    ImageView dbState;
    ImageView computerState;
    TextView memoryText;
    TextView diskText;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        totalView = view.findViewById(R.id.total_text);
        defendView = view.findViewById(R.id.defend_text);
        lockedView = view.findViewById(R.id.locked_text);
        bindView = view.findViewById(R.id.bind_text);

        leaving_alarm_view = view.findViewById(R.id.leaving_alarm_text);
        area_alarm_view = view.findViewById(R.id.area_alarm_text);

        serverState = view.findViewById(R.id.server_img);
        dbState = view.findViewById(R.id.db_img);
        computerState = view.findViewById(R.id.computer_img);
        memoryText = view.findViewById(R.id.memory_text);
        diskText = view.findViewById(R.id.disk_text);
        queryInfo();

        return view;
    }

    public void queryInfo(){
        HttpUtil.sendGetRequestWithHttp(BOX_URI  + TOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                final Home home = Util.handleHomeInfo(content);
                if(home !=null){
                     getActivity().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             showInfo(home);
                         }
                     });

                 }



            }
        });



    }


    private void showInfo(Home home) {
        totalView.setText(home.info.total_num);
        defendView.setText(home.info.defend_num);
        lockedView.setText(home.info.locked_num);
        bindView.setText(home.info.bind_num);
        leaving_alarm_view.setText(home.alarm_num.area_alarm);
        area_alarm_view.setText(home.alarm_num.leaving_alarm);
        memoryText.setText(home.over_view.mem_percent+"%");
        diskText.setText(home.over_view.hd_usage+"%");
        if(home.over_view.database.equals("1")){
            dbState.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.normal));
        }else {
            dbState.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.warning));
        }
        if(home.over_view.computer.equals("1")){
            computerState.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.normal));
        }else {
            computerState.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.warning));
        }


    }

}
