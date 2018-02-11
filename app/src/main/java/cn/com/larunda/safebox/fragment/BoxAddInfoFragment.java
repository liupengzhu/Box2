package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.com.larunda.safebox.BoxAddActivity;
import cn.com.larunda.safebox.BoxAddUserActivity;
import cn.com.larunda.safebox.BoxInitActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.gson.Result;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.larunda.safebox.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by sddt on 18-1-18.
 */

public class BoxAddInfoFragment extends Fragment implements View.OnClickListener {

    public static EditText name_text;
    public static EditText material_text;
    public static EditText size_text;
    public static EditText protect_text;

    private Button putButton;

    public SwipeRefreshLayout swipeRefreshLayout;

    public static final String MESSAGE_URI = Util.URL + "box/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_add_info_fragment, container, false);
        initView(view);
        initEvent();
        return view;
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        putButton.setOnClickListener(this);
    }


    /**
     * 初始化View
     *
     * @param view
     */
    private void initView(View view) {
        name_text = view.findViewById(R.id.box_add_info_name_text);
        material_text = view.findViewById(R.id.box_add_info_material_text);
        size_text = view.findViewById(R.id.box_add_info_size_text);
        protect_text = view.findViewById(R.id.box_add_info_protect_text);

        putButton = view.findViewById(R.id.box_add_info_button);

        swipeRefreshLayout = view.findViewById(R.id.box_add_info_swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setEnabled(false);//设置swipe不可用

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_add_info_button:
                if (name_text != null && material_text != null && size_text != null && protect_text != null) {
                    String name = name_text.getText().toString().trim();
                    String material = material_text.getText().toString().trim();
                    String size = size_text.getText().toString().trim();
                    String protect = protect_text.getText().toString().trim();
                    if (!isEmpty(name, material, size, protect)) {
                        sendPutRequest(name, material, size, protect);
                    }
                }
                break;
            default:
                break;

        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param material
     * @param size
     * @param protect
     * @return
     */
    private boolean isEmpty(String name, String material, String size, String protect) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "别名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(material)) {
            Toast.makeText(getContext(), "材质不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(size)) {
            Toast.makeText(getContext(), "尺寸不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(protect)) {
            Toast.makeText(getContext(), "防护等级不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 更新递送箱信息
     *
     * @param trim
     * @param trim1
     * @param trim2
     */
    private void sendPutRequest(String name, String trim, String trim1, String trim2) {
        swipeRefreshLayout.setRefreshing(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("f_aliases", name);
            jsonObject.put("f_material", trim);
            jsonObject.put("f_size", trim1);
            jsonObject.put("f_protect_grade", trim2);
            jsonObject.put("type", "app");
            HttpUtil.sendPutRequestWithHttp(MESSAGE_URI + BoxAddActivity.id + Util.TOKEN + BoxAddActivity.token, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    if (Util.isGoodJson(content)) {
                        final Result result = Util.handleResult(content);
                        if (result != null && result.error == null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parseResult(result);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    BoxAddActivity.preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });
                        }
                    }
                }

            });


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void parseResult(Result result) {
        if (result.data != null && result.data.equals("true")) {
            Toast.makeText(getContext(), "更新成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "更新失败", Toast.LENGTH_SHORT).show();
        }
    }

}
