package cn.com.larunda.safebox.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.larunda.safebox.BoxActivity;
import cn.com.larunda.safebox.LoginActivity;
import cn.com.larunda.safebox.MainActivity;

import com.larunda.safebox.R;

import cn.com.larunda.safebox.SettingQxActivity;
import cn.com.larunda.safebox.SettingStatesActivity;
import cn.com.larunda.safebox.UserInfoActivity;
import cn.com.larunda.safebox.adapter.BoxAdapter;
import cn.com.larunda.safebox.adapter.FootAdapter;
import cn.com.larunda.safebox.gson.BoxData;
import cn.com.larunda.safebox.gson.BoxInfo;
import cn.com.larunda.safebox.recycler.MyBox;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class DListFragment extends Fragment implements View.OnClickListener {


    public static final String BOX_URL = Util.URL + "box" + Util.TOKEN;
    public static final String IMG_URL = "http://safebox.dsmcase.com:90";
    private List<MyBox> myBoxList = new ArrayList<>();
    private RecyclerView recyclerView;
    private static BoxAdapter adapter;
    private LinearLayoutManager manager;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout loodingErrorLayout;
    private ImageView loodingLayout;

    private ImageView allCheckedImage;
    private TextView allCheckedText;

    private static RelativeLayout top_layout;
    private static LinearLayout bottom_layout;

    private Button settingQx_Button;
    private Button settingState_Button;

    public static boolean isLongClick = false;

    private boolean isAllChecked = false;

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;
    private ArrayList<String> idList = new ArrayList<>();
    private String search;
    private int page;
    private int lastVisibleItem;
    private int count;
    private static FootAdapter footAdapter;
    private int total;

    private boolean isInit = false;
    private SharedPreferences preferences;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_list_fragment, container, false);
        initView(view);
        initEvent();

        if (isLongClick) {
            adapter.setCheckedLayout(true);
            footAdapter.notifyDataSetChanged();
            top_layout.setVisibility(View.GONE);
            bottom_layout.setVisibility(View.VISIBLE);
            MainActivity.tabLayout.setVisibility(View.GONE);
        }
        isInit = true;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 初始化view
     *
     * @param view
     */
    private void initView(View view) {

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", null);

        recyclerView = view.findViewById(R.id.dsx_list);
        refreshLayout = view.findViewById(R.id.box_list_swiper);

        loodingErrorLayout = view.findViewById(R.id.d_loading_error_layout);
        loodingLayout = view.findViewById(R.id.d_loading_layout);

        top_layout = view.findViewById(R.id.top_layout);
        bottom_layout = view.findViewById(R.id.bottom_layout);

        allCheckedImage = view.findViewById(R.id.all_checked_image);
        allCheckedText = view.findViewById(R.id.all_checked_text);

        settingQx_Button = view.findViewById(R.id.dsx_list_setting_qx);
        settingState_Button = view.findViewById(R.id.dsx_list_setting_state);

        searchText = view.findViewById(R.id.list_serch_edit);
        cancelButton = view.findViewById(R.id.list_cancel_button);
        ensureButton = view.findViewById(R.id.list_ensure_button);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        manager = new LinearLayoutManager(getContext());
        adapter = new BoxAdapter(myBoxList);
        footAdapter = new FootAdapter(getContext(), adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(footAdapter);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search = null;
                sendRequest();

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (page <= total) {
                    //在newState为滑到底部时
                    if (lastVisibleItem + 1 == footAdapter.getItemCount()) {
                        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                            footAdapter.setHasMore(true);
                            footAdapter.notifyDataSetChanged();
                        }
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (myBoxList.size() < count) {
                                search = null;
                                sendRequest();
                            } else {
                                sendAddRequest();
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });

    }

    /**
     * 加载下一页
     */
    private void sendAddRequest() {
        refreshLayout.setRefreshing(true);
        String searchText = "";
        if (search != null) {
            searchText = "&search=" + search;
        } else {
            searchText = "";
        }
        HttpUtil.sendGetRequestWithHttp(BOX_URL + token + searchText + "&page=" + page + Util.TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final BoxInfo boxInfo = Util.handleBoxInfo(content);

                    if (boxInfo != null && boxInfo.error == null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addBoxList(boxInfo);
                                refreshLayout.setRefreshing(false);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                    }
                }
            }
        });


    }

    /**
     * 添加数据
     *
     * @param boxInfo
     */
    private void addBoxList(BoxInfo boxInfo) {
        page = boxInfo.current_page + 1;
        if (boxInfo.boxDataList.size() == 0 || boxInfo.boxDataList.size() < count) {
            footAdapter.setHasMore(false);
        }
        if (boxInfo.boxDataList != null) {
            for (BoxData boxData : boxInfo.boxDataList) {

                MyBox box = new MyBox();
                String img_url = null;
                if (boxData.code != null) {
                    box.setCode(boxData.code);
                }
                if (boxData.f_pic != null) {
                    img_url = boxData.f_pic.replace('\\', ' ');
                    box.setBox_img(IMG_URL + img_url);
                } else {
                    box.setBox_img(null);
                }
                if (boxData.name != null) {
                    box.setBox_name(boxData.name);
                } else {
                    box.setBox_name(null);
                }
                if (boxData.electricity != null) {
                    box.setBox_dl(boxData.electricity);
                } else {
                    box.setBox_dl(null);
                }

                if (boxData.level != null) {
                    box.setBox_qx(Integer.parseInt(boxData.level));
                } else {
                    box.setBox_qx(0);
                }
                if (boxData.is_defence != null) {
                    if (boxData.is_defence.equals("1")) {
                        box.setIs_bf(true);
                    } else {
                        box.setIs_bf(false);
                    }
                } else {
                    box.setIs_bf(false);
                }
                if (boxData.is_locked != null) {
                    if (boxData.is_locked.equals("1")) {
                        box.setIs_sd(true);
                    } else {
                        box.setIs_sd(false);
                    }
                } else {
                    box.setIs_sd(false);
                }

                if (boxData.id != null) {
                    box.setId(boxData.id);
                } else {
                    box.setId(null);
                }
                myBoxList.add(box);

            }
        }
        footAdapter.notifyDataSetChanged();


    }

    /**
     * recyclerview的监听事件
     */
    private void initEvent() {

        adapter.setOnLongClickListener(new BoxAdapter.DsxLongClickListener() {
            @Override
            public void onLongClick(View v) {
                isLongClick = true;
                adapter.setCheckedLayout(true);
                footAdapter.notifyDataSetChanged();
                top_layout.setVisibility(View.GONE);
                bottom_layout.setVisibility(View.VISIBLE);
                MainActivity.tabLayout.setVisibility(View.GONE);
            }
        });
        adapter.setDsxOnClickListener(new BoxAdapter.DsxOnClickListener() {
            @Override
            public void onClick(View v, String id) {
                Intent intent = new Intent(getContext(), BoxActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


        allCheckedImage.setOnClickListener(this);
        allCheckedText.setOnClickListener(this);
        settingQx_Button.setOnClickListener(this);
        settingState_Button.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search = searchText.getText().toString().trim();
                sendRequest();
                return false;
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (cancelButton != null) {
                        cancelButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (cancelButton != null) {
                        cancelButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    //发送网络请求
    private void sendRequest() {
        refreshLayout.setRefreshing(true);
        String searchText = "";
        if (search != null) {
            searchText = "&search=" + search;
        } else {
            searchText = "";
        }
        HttpUtil.sendGetRequestWithHttp(BOX_URL + token + searchText + "&page=1" + Util.TYPE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        loodingErrorLayout.setVisibility(View.VISIBLE);
                        loodingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                if (Util.isGoodJson(content)) {
                    final BoxInfo boxInfo = Util.handleBoxInfo(content);
                    if (boxInfo != null && boxInfo.error == null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initBoxList(boxInfo);
                                preferences.edit().putString("boxInfo", content).apply();
                                refreshLayout.setRefreshing(false);
                                loodingErrorLayout.setVisibility(View.GONE);
                                loodingLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.putExtra("token_timeout", "登录超时");
                                preferences.edit().putString("token", null).commit();
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            loodingErrorLayout.setVisibility(View.VISIBLE);
                            loodingLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });


    }

    //解析BoxInfo
    private void initBoxList(BoxInfo boxInfo) {
        page = boxInfo.current_page + 1;
        count = boxInfo.per_page;
        total = boxInfo.last_page;
        myBoxList.clear();
        if (boxInfo.boxDataList.size() == 0 || boxInfo.boxDataList.size() < count) {
            footAdapter.setHasMore(false);
        }
        if (boxInfo.boxDataList != null) {
            for (BoxData boxData : boxInfo.boxDataList) {

                MyBox box = new MyBox();
                String img_url = null;
                if (boxData.code != null) {
                    box.setCode(boxData.code);
                }
                if (boxData.f_pic != null) {
                    img_url = boxData.f_pic.replace('\\', ' ');
                    box.setBox_img(IMG_URL + img_url);
                } else {
                    box.setBox_img(null);
                }
                if (boxData.name != null) {
                    box.setBox_name(boxData.name);
                } else {
                    box.setBox_name(null);
                }
                if (boxData.electricity != null) {
                    box.setBox_dl(boxData.electricity);
                } else {
                    box.setBox_dl(null);
                }

                if (boxData.level != null) {
                    box.setBox_qx(Integer.parseInt(boxData.level));
                } else {
                    box.setBox_qx(0);
                }
                if (boxData.is_defence != null) {
                    if (boxData.is_defence.equals("1")) {
                        box.setIs_bf(true);
                    } else {
                        box.setIs_bf(false);
                    }
                } else {
                    box.setIs_bf(false);
                }
                if (boxData.is_locked != null) {
                    if (boxData.is_locked.equals("1")) {
                        box.setIs_sd(true);
                    } else {
                        box.setIs_sd(false);
                    }
                } else {
                    box.setIs_sd(false);
                }

                if (boxData.id != null) {
                    box.setId(boxData.id);
                } else {
                    box.setId(null);
                }
                myBoxList.add(box);

            }
        }
        footAdapter.notifyDataSetChanged();


    }

    /**
     * 取消多选状态
     */
    public static void cancleLongClick() {
        isLongClick = false;
        adapter.setCheckedLayout(false);
        footAdapter.notifyDataSetChanged();
        top_layout.setVisibility(View.VISIBLE);
        bottom_layout.setVisibility(View.GONE);
        MainActivity.tabLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 检查选中的box
     */
    private void checkIsChecked() {
        idList.clear();
        for (MyBox box : myBoxList) {
            if (box.isImgIsChecked()) {
                idList.add(box.getCode());
            }
        }
    }

    /**
     * 点击事件监听
     *
     * @param v
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_checked_image:
            case R.id.all_checked_text:
                allCheckedClick();
                break;
            case R.id.dsx_list_setting_qx:
                checkIsChecked();
                if (idList.size() != 0) {
                    Intent settingQxIntent = new Intent(getContext(), SettingQxActivity.class);
                    settingQxIntent.putStringArrayListExtra("id", idList);
                    startActivity(settingQxIntent);
                } else {
                    Toast.makeText(getContext(), "还没有选中箱子", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.dsx_list_setting_state:
                checkIsChecked();
                if (idList.size() != 0) {
                    Intent settingStatesIntent = new Intent(getContext(), SettingStatesActivity.class);
                    settingStatesIntent.putStringArrayListExtra("id", idList);
                    startActivity(settingStatesIntent);
                } else {
                    Toast.makeText(getContext(), "还没有选中箱子", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.list_cancel_button:
                if (searchText != null) {
                    searchText.setText("");
                }
                break;
            case R.id.list_ensure_button:
                if (searchText != null) {
                    search = searchText.getText().toString().trim();
                    sendRequest();
                }
                break;
            default:
                break;
        }

    }


    /**
     * 处理全选按钮的点击事件
     */
    private void allCheckedClick() {
        //判断当前全选是否是选中状态
        if (isAllChecked) {
            isAllChecked = false;
            allCheckedImage.setImageResource(R.mipmap.unchecked);
            List<MyBox> boxes = adapter.getMyBoxList();
            for (MyBox box : boxes) {
                box.setImgIsChecked(false);
            }
            footAdapter.notifyDataSetChanged();


        } else {
            isAllChecked = true;
            allCheckedImage.setImageResource(R.mipmap.checked);
            List<MyBox> boxes = adapter.getMyBoxList();
            for (MyBox box : boxes) {
                box.setImgIsChecked(true);
            }
            footAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isInit) {
                String content = preferences.getString("boxInfo", null);
                if (content != null) {
                    if (Util.isGoodJson(content)) {
                        BoxInfo boxInfo = Util.handleBoxInfo(content);
                        initBoxList(boxInfo);
                    } else {
                        sendRequest();
                    }
                } else {
                    //每次fragment创建时还没有网络数据 设置载入背景为可见
                    loodingLayout.setVisibility(View.VISIBLE);
                    loodingErrorLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    sendRequest();
                }
            }
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        /*recyclerView.scrollToPosition(0);*/
        String content = preferences.getString("boxInfo", null);
        if (content != null) {
            if (Util.isGoodJson(content)) {
                BoxInfo boxInfo = Util.handleBoxInfo(content);
                initBoxList(boxInfo);
            } else {
                sendRequest();
            }
        } else {
            //每次fragment创建时还没有网络数据 设置载入背景为可见
            loodingLayout.setVisibility(View.VISIBLE);
            loodingErrorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            sendRequest();
        }
    }
}
