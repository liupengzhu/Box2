package cn.com.larunda.safebox;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class AddEnclosureActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;

    private String id;

    private RelativeLayout enclosureButton;
    private TextView enclosureText;
    private ChooseDialog enclosureDialog;
    private List<String> enclosureData = new ArrayList<>();

    private RelativeLayout positionButton;
    private TextView positionText;
    private ChooseDialog positionDialog;
    private List<String> positionData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enclosure);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getStringExtra("id");
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
        enclosureButton.setOnClickListener(this);
        enclosureDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                if (enclosureText.getText().toString().trim().equals(enclosureData.get(positon))) {
                    positionText.setText("请选择区域内外");
                    enclosureDialog.cancel();
                } else {
                    enclosureText.setText(enclosureData.get(positon));
                    enclosureDialog.cancel();
                }
            }
        });
        positionButton.setOnClickListener(this);
        positionDialog.setOnClickListener(new ChooseDialog.OnClickListener() {
            @Override
            public void OnClick(View v, int positon) {
                positionText.setText(positionData.get(positon));
                positionDialog.cancel();
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        enclosureData.add("阳澄湖国际科创园1");
        enclosureData.add("阳澄湖国际科创园2");
        enclosureData.add("阳澄湖国际科创园3");
        enclosureData.add("阳澄湖国际科创园4");

        positionData.add("内");
        positionData.add("外");
    }

    /**
     * 初始化view
     */
    private void initView() {

        enclosureButton = findViewById(R.id.add_enclosure_enclosure);
        enclosureText = findViewById(R.id.add_enclosure_enclosure_text);
        enclosureDialog = new ChooseDialog(this, enclosureData);

        positionButton = findViewById(R.id.add_enclosure_position);
        positionText = findViewById(R.id.add_enclosure_position_text);
        positionDialog = new ChooseDialog(this, positionData);

        titleBar = findViewById(R.id.add_enclosure_title_bar);
        titleBar.setTextViewText("添加区域");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
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
            case R.id.add_enclosure_enclosure:
                enclosureDialog.show();
                break;
            case R.id.add_enclosure_position:
                if (isCheckedEnclosure()) {
                    positionDialog.show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 判断是否选择区域的方法
     *
     * @return
     */
    private boolean isCheckedEnclosure() {
        if (enclosureText.getText().toString().trim().equals("请选择区域")) {
            Toast.makeText(this, "请先选择区域", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
