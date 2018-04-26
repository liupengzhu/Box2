package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.larunda.safebox.R;
import com.larunda.selfdialog.TimeDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.larunda.safebox.adapter.DestinationAreaAdapter;
import cn.com.larunda.safebox.adapter.DestinationPersonAdapter;
import cn.com.larunda.safebox.db.AreaBean;
import cn.com.larunda.safebox.db.CityBean;
import cn.com.larunda.safebox.db.DBManager;
import cn.com.larunda.safebox.db.ProvinceBean;
import cn.com.larunda.safebox.recycler.Area;
import cn.com.larunda.safebox.recycler.Person;
import cn.com.larunda.safebox.util.ActivityCollector;
import cn.com.larunda.safebox.util.BaseActivity;
import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddDestinationActivity extends BaseActivity implements View.OnClickListener {

    private static final int ADD_ENCLOSURE = 1;
    private OptionsPickerView pvOptions;//地址选择器
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<CityBean>> options2Items = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<AreaBean>>> options3Items = new ArrayList<>();//区
    private ArrayList<String> Provincestr = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> Citystr = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<String>>> Areastr = new ArrayList<>();//区


    private int id;
    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;
    private RelativeLayout originButton;
    private TextView originCityText;
    private RelativeLayout destinationButton;
    private TextView destinationCityText;
    private String type;

    private TimeDialog timeDialog;
    private RelativeLayout timeButton;
    private TextView timeText;

    private EditText originText;
    private EditText destinationText;
    private EditText intervalText;
    private Button addButton;

    private RecyclerView areaGroup;
    private RelativeLayout areaAddButton;
    private DestinationAreaAdapter areaAdapter;
    private LinearLayoutManager areaManager;
    private List<Area> areaList = new ArrayList<>();

    private RecyclerView personGroup;
    private RelativeLayout personAddButton;
    private DestinationPersonAdapter personAdapter;
    private LinearLayoutManager personManager;
    private List<Person> personList = new ArrayList<>();
    private LoadingDailog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_add_destination);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        initData();
        initView();
        initEvent();
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.add_destination_title_bar);
        titleBar.setTextViewText("添加目的地");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        originButton = findViewById(R.id.add_destination_origin_button);
        originCityText = findViewById(R.id.add_destination_origin_city_text);
        destinationButton = findViewById(R.id.add_destination_destination_button);
        destinationCityText = findViewById(R.id.add_destination_destination_city_text);

        timeButton = findViewById(R.id.add_destination_time_button);
        timeText = findViewById(R.id.add_destination_time);
        timeDialog = new TimeDialog(this);

        areaGroup = findViewById(R.id.add_destination_area_layout);
        areaAddButton = findViewById(R.id.add_destination_area_add);
        areaAdapter = new DestinationAreaAdapter(this, areaList);
        areaManager = new LinearLayoutManager(this);
        areaGroup.setAdapter(areaAdapter);
        areaGroup.setLayoutManager(areaManager);

        personGroup = findViewById(R.id.add_destination_person_layout);
        personAddButton = findViewById(R.id.add_destination_person_add);
        personAdapter = new DestinationPersonAdapter(this, personList);
        personManager = new LinearLayoutManager(this);
        personGroup.setAdapter(personAdapter);
        personGroup.setLayoutManager(personManager);

        originText = findViewById(R.id.add_destination_origin_text);
        destinationText = findViewById(R.id.add_destination_destination_text);
        intervalText = findViewById(R.id.add_destination_interval);
        addButton = findViewById(R.id.add_destination_button);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

        addButton.setOnClickListener(this);

        personAdapter.setItemOnclickListener(new DestinationPersonAdapter.ItemOnclickListener() {
            @Override
            public void nameOnclick(View v, int position) {

            }

            @Override
            public void dynamicOnclick(View v, int position) {
                Person person = personList.get(position);
                person.setUseDynamic(!person.isUseDynamic());
                personAdapter.notifyItemChanged(position);
            }

            @Override
            public void fingerprintOnclick(View v, int position) {
                Person person = personList.get(position);
                person.setUseFingerprint(!person.isUseFingerprint());
                personAdapter.notifyItemChanged(position);
            }

            @Override
            public void pwdOnclick(View v, int position) {
                Person person = personList.get(position);
                person.setUsePwd(!person.isUsePwd());
                personAdapter.notifyItemChanged(position);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count, int position) {
                Person person = personList.get(position);
                person.setPwd(s.toString());
            }
        });

        areaAddButton.setOnClickListener(this);
        personAddButton.setOnClickListener(this);

        destinationButton.setOnClickListener(this);
        originButton.setOnClickListener(this);

        timeButton.setOnClickListener(this);
        timeDialog.setOnCancelClickListener(new TimeDialog.OnCancelClickListener() {
            @Override
            public void OnClick(View view) {
                timeDialog.cancel();
            }
        });
        timeDialog.setOnOkClickListener(new TimeDialog.OnOkClickListener() {
            @Override
            public void OnClick(View view, String date, String time) {
                timeText.setText(date + " " + time);
                timeDialog.cancel();
            }
        });

        titleBar.setOnClickListener(new TitleListener() {
            @Override
            public void onLeftButtonClickListener(View v) {
            }

            @Override
            public void onLeftBackButtonClickListener(View v) {
                finish();
            }

            @Override
            public void onRightButtonClickListener(View v) {
            }
        });

        //为RecycleView绑定触摸事件
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动事件
                Collections.swap(areaList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                areaAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //侧滑事件
                areaList.remove(viewHolder.getAdapterPosition());
                areaAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                areaAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return true;
            }
        });
        helper.attachToRecyclerView(areaGroup);

        //为RecycleView绑定触摸事件
        ItemTouchHelper helper2 = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动事件
                Collections.swap(personList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                personAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //侧滑事件
                personList.remove(viewHolder.getAdapterPosition());
                personAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                personAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return true;
            }
        });
        helper2.attachToRecyclerView(personGroup);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_destination_origin_button:
                type = "origin";
                pvOptions.show();
                break;
            case R.id.add_destination_destination_button:
                type = "destination";
                pvOptions.show();
                break;
            case R.id.add_destination_time_button:
                timeDialog.show();
                break;
            case R.id.add_destination_area_add:
                /*Intent enclosureIntent = new Intent(this, AddEnclosureActivity.class);
                enclosureIntent.putExtra("id", id);
                startActivityForResult(enclosureIntent, ADD_ENCLOSURE);*/
                addArea("盐城市", "外");
                break;
            case R.id.add_destination_person_add:
                /*Intent enclosureIntent = new Intent(this, AddEnclosureActivity.class);
                enclosureIntent.putExtra("id", id);
                startActivityForResult(enclosureIntent, ADD_ENCLOSURE);*/
                addPerson();
                break;
            case R.id.add_destination_button:
                sendPostReques();
                break;
            default:
                break;
        }
    }

    /**
     * 发送网络请求
     */
    private void sendPostReques() {
        String originCity = originCityText.getText().toString().trim();
        String origin = originText.getText().toString().trim();
        String destinationCity = destinationCityText.getText().toString().trim();
        String destination = destinationText.getText().toString().trim();
        String interval = intervalText.getText().toString().trim();
        String time = timeText.getText().toString().trim();
        if (!isEmpty(originCity, origin, destinationCity, destination, interval, time)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("f_origin", origin);
                jsonObject.put("f_origin_city", originCity);
                jsonObject.put("f_destination", destination);
                jsonObject.put("f_destination_city", destinationCity);
                jsonObject.put("f_release_time", time);
                jsonObject.put("f_upload_interval", interval);
                dialog.show();
                HttpUtil.sendPostRequestWithHttp(Util.URL + "task/" + id + "/process" + Util.TOKEN
                        + token, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                Toast.makeText(AddDestinationActivity.this, "网络异常!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String content = response.body().string();
                        final int code = response.code();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                if (code == 200) {
                                    setResult(RESULT_OK);
                                    finish();
                                } else if (code == 401) {
                                    Intent intent = new Intent(AddDestinationActivity.this, LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
                                    ActivityCollector.finishAllActivity();
                                } else if (code == 422) {
                                    try {
                                        JSONObject js = new JSONObject(content);
                                        Toast.makeText(AddDestinationActivity.this, js.get("message") + "", Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(AddDestinationActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否为空
     *
     * @param originCity
     * @param origin
     * @param destinationCity
     * @param destination
     * @param interval
     * @param time
     * @return
     */
    private boolean isEmpty(String originCity, String origin, String destinationCity, String destination, String interval, String time) {
        if (originCity.isEmpty()) {
            Toast.makeText(this, "始发地城市不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (origin.isEmpty()) {
            Toast.makeText(this, "始发地详细地址不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (destinationCity.isEmpty()) {
            Toast.makeText(this, "目的地城市不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (destination.isEmpty()) {
            Toast.makeText(this, "目的地详细地址不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (interval.isEmpty()) {
            Toast.makeText(this, "通讯间隔不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (Integer.parseInt(interval) > 1800 || Integer.parseInt(interval) < 30) {
            Toast.makeText(this, "通讯间隔必须在30-1800之间", Toast.LENGTH_SHORT).show();
            return true;
        } else if (time.isEmpty()) {
            Toast.makeText(this, "截止时间不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    /**
     * 添加view
     */
    private void addArea(String name, String type) {
        Area area = new Area();
        area.setName(name);
        long m = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        area.setTime(formatter.format(m));
        area.setType(type);
        areaList.add(area);
        areaAdapter.notifyDataSetChanged();
    }

    private void addPerson() {
        Person person = new Person();
        personList.add(person);
        personAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        // 获取数据库
        SQLiteDatabase db = DBManager.getdb(getApplication());
        //省
        Cursor cursor = db.query("province", null, null, null, null, null,
                null);
        while (cursor.moveToNext()) {
            int pro_id = cursor.getInt(0);
            String pro_code = cursor.getString(1);
            String pro_name = cursor.getString(2);
            String pro_name2 = cursor.getString(3);
            ProvinceBean provinceBean = new ProvinceBean(pro_id, pro_code, pro_name, pro_name2);
            options1Items.add(provinceBean);//添加一级目录
            Provincestr.add(cursor.getString(2));
            //查询二级目录，市区
            Cursor cursor1 = db.query("city", null, "province_id=?", new String[]{pro_id + ""}, null, null,
                    null);
            ArrayList<CityBean> cityBeanList = new ArrayList<>();
            ArrayList<String> cityStr = new ArrayList<>();
            //地区集合的集合（注意这里要的是当前省份下面，当前所有城市的地区集合我去）
            ArrayList<ArrayList<AreaBean>> options3Items_03 = new ArrayList<>();
            ArrayList<ArrayList<String>> options3Items_str = new ArrayList<>();
            while (cursor1.moveToNext()) {
                int cityid = cursor1.getInt(0);
                int province_id = cursor1.getInt(1);
                String code = cursor1.getString(2);
                String name = cursor1.getString(3);
                String provincecode = cursor1.getString(4);
                CityBean cityBean = new CityBean(cityid, province_id, code, name, provincecode);
                //添加二级目录
                cityBeanList.add(cityBean);
                cityStr.add(cursor1.getString(3));
                //查询三级目录
                Cursor cursor2 = db.query("area", null, "city_id=?", new String[]{cityid + ""}, null, null,
                        null);
                ArrayList<AreaBean> areaBeanList = new ArrayList<>();
                ArrayList<String> areaBeanstr = new ArrayList<>();
                while (cursor2.moveToNext()) {
                    int areaid = cursor2.getInt(0);
                    int city_id = cursor2.getInt(1);
//                    String code0=cursor2.getString(2);
                    String areaname = cursor2.getString(3);
                    String citycode = cursor2.getString(4);
                    if (!areaname.equals("市辖区")) {
                        AreaBean areaBean = new AreaBean(areaid, city_id, areaname, citycode);
                        areaBeanList.add(areaBean);
                        areaBeanstr.add(cursor2.getString(3));
                    }
                }
                options3Items_str.add(areaBeanstr);//本次查询的存储内容
                options3Items_03.add(areaBeanList);
            }
            options2Items.add(cityBeanList);//增加二级目录数据
            Citystr.add(cityStr);
            options3Items.add(options3Items_03);//添加三级目录
            Areastr.add(options3Items_str);
        }
        //选项选择器
        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPro_name() + " "
                        + options2Items.get(options1).get(options2).getName() + " "
                        + options3Items.get(options1).get(options2).get(options3).getName();
                if (type != null && type.equals("origin")) {
                    originCityText.setText(tx);
                } else if (type != null && type.equals("destination")) {
                    destinationCityText.setText(tx);
                }
            }
        }).build();
        //设置三级联动效果
        pvOptions.setPicker(Provincestr, Citystr, Areastr);
        //设置选择的三级单位
//        pvOptions.setLabels("省", "市", "区");
        pvOptions.setTitleText("选择城市");
        //设置默认选中的三级项
        pvOptions.setSelectOptions(0, 0, 0);
    }
}
