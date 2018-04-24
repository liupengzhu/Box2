package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

public class TaskDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private SharedPreferences preferences;
    private String token;

    private TextView nameText;
    private TextView startText;
    private TextView endText;
    private Button endButton;
    private int id;
    private String name;
    private String createTime;
    private String completedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        name = getIntent().getStringExtra("name");
        createTime = getIntent().getStringExtra("createTime");
        completedTime = getIntent().getStringExtra("completedTime");
        initView();
        initEvent();
    }

    /**
     * 初始化View
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.task_detail_title_bar);
        titleBar.setTextViewText("任务详情");
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        nameText = findViewById(R.id.task_detail_name);
        startText = findViewById(R.id.task_detail_start_time);
        endText = findViewById(R.id.task_detail_end_time);
        endButton = findViewById(R.id.task_detail_button);
        if (name != null) {
            nameText.setText(name);
        }
        if (createTime != null) {
            startText.setText(createTime);
        }
        if (completedTime != null) {
            endText.setText(completedTime);
            endButton.setVisibility(View.GONE);
        } else {
            endButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击事件处理
     */
    private void initEvent() {

        endButton.setOnClickListener(this);

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
    }

    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_detail_button:

                break;
            default:
                break;
        }
    }
}
