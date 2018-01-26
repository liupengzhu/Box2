package cn.com.larunda.safebox;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.selfdialog.ChooseDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

public class BindingUserActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;

    private RelativeLayout companyButton;
    private TextView companyText;
    private ChooseDialog companyDialog;
    private List<String> companyData = new ArrayList<>();

    private RelativeLayout departmentButton;
    private TextView departmentText;
    private ChooseDialog departmentDialog;
    private List<String> departmentData = new ArrayList<>();

    private RelativeLayout personButton;
    private TextView personText;
    private ChooseDialog personDialog;
    private List<String> personData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initData();
        initView();
        initEvent();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
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

        companyButton.setOnClickListener(this);
        companyDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                if (companyText.getText().toString().trim().equals(companyData.get(positon))) {
                    companyDialog.cancel();
                } else {
                    companyText.setText(companyData.get(positon));
                    departmentText.setText("请选择部门");
                    personText.setText("请选择姓名");
                    companyDialog.cancel();
                }
            }
        });
        departmentButton.setOnClickListener(this);

        personButton.setOnClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        companyData.add("家乐福");
        companyData.add("朗润达");
        companyData.add("沃尔玛");
    }

    /**
     * 初始化view
     */
    private void initView() {

        companyButton = findViewById(R.id.binding_user_company);
        companyText = findViewById(R.id.binding_user_company_text);
        companyDialog = new ChooseDialog(this, companyData);

        departmentButton = findViewById(R.id.binding_user_department);
        departmentText = findViewById(R.id.binding_user_department_text);

        personButton = findViewById(R.id.binding_user_person);
        personText = findViewById(R.id.binding_user_person_text);

        titleBar = findViewById(R.id.binding_user_title_bar);
        titleBar.setTextViewText("绑定用户");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.binding_user_company:
                companyDialog.show();
                break;
            case R.id.binding_user_department:
                if (isCheckedCompany()) {
                    departmentDialog = new ChooseDialog(BindingUserActivity.this, departmentData);
                    departmentDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                        @Override
                        public void OnClick(View v, int positon) {
                            if (departmentText.getText().toString().trim().equals(departmentData.get(positon))) {
                                departmentDialog.cancel();
                            } else {
                                departmentText.setText(departmentData.get(positon));
                                personText.setText("请选择姓名");
                                departmentDialog.cancel();
                            }
                        }
                    });
                    departmentDialog.show();
                }
                break;

            case R.id.binding_user_person:
                if (isCheckedDepartment()) {
                    personDialog = new ChooseDialog(BindingUserActivity.this, personData);
                    personDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
                        @Override
                        public void OnClick(View v, int positon) {
                            personText.setText(personData.get(positon));
                            personDialog.cancel();
                        }
                    });
                    personDialog.show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 判断是否选择部门方法
     *
     * @return
     */
    private boolean isCheckedDepartment() {
        if (companyText.getText().toString().trim().equals("请选择单位")) {
            Toast.makeText(this, "请先选择单位", Toast.LENGTH_SHORT).show();
            return false;
        } else if (departmentText.getText().toString().trim().equals("请选择部门")) {
            Toast.makeText(this, "请先选择部门", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            personData.add("张三1");
            personData.add("张三2");
            personData.add("张三3");
            personData.add("张三4");
            personData.add("张三5");
            personData.add("张三6");
            personData.add("张三7");
            personData.add("张三8");
            return true;
        }
    }

    /**
     * 判断是否选择公司
     *
     * @return
     */
    private boolean isCheckedCompany() {
        if (companyText.getText().toString().trim().equals("请选择单位")) {
            Toast.makeText(this, "请先选择单位", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (companyText.getText().toString().trim().equals("家乐福")) {
                departmentData.clear();
                departmentData.add("导购员");
            } else if (companyText.getText().toString().trim().equals("朗润达")) {
                departmentData.clear();
                departmentData.add("软件部");
            } else if (companyText.getText().toString().trim().equals("沃尔玛")) {
                departmentData.clear();
                departmentData.add("采购部");
            }
            return true;
        }
    }
}
