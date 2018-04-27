package cn.com.larunda.safebox;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.larunda.safebox.R;
import com.larunda.selfdialog.DateDialog;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import cn.com.larunda.safebox.adapter.TaskLogAdapter;
import cn.com.larunda.safebox.util.BaseActivity;

public class TaskWarningActivity extends BaseActivity implements View.OnClickListener {
    private TitleBar titleBar;
    private int id;
    private SharedPreferences preferences;
    private String token;

    private EditText searchText;
    private ImageView cancelButton;
    private TextView ensureButton;
    private String search = "";
    private DateDialog dateDialog;
    private int maxPage;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_warning);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        id = getIntent().getIntExtra("id", 0);
        initView();
        initEvent();
    }

    /**
     * 初始化view
     */
    private void initView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.warning_title_bar);
        titleBar.setTextViewText("警报列表");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        /*recyclerView = findViewById(R.id.task_log_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new TaskLogAdapter(this, taskLogList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);*/

        searchText = findViewById(R.id.warning_search_edit);
        cancelButton = findViewById(R.id.warning_cancel_button);
        ensureButton = findViewById(R.id.warning_ensure_button);
        dateDialog = new DateDialog(this);
    }

    /**
     *
     */
    private void initEvent() {

        cancelButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);
        searchText.setOnClickListener(this);

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

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search = searchText.getText().toString().trim();
                //sendRequest();
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
        dateDialog.setOnCancelClickListener(new DateDialog.OnCancelClickListener() {
            @Override
            public void OnClick(View view) {
                dateDialog.cancel();
            }
        });
        dateDialog.setOnOkClickListener(new DateDialog.OnOkClickListener() {
            @Override
            public void OnClick(View view, String date) {
                searchText.setText(date + "");
                dateDialog.cancel();
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
            case R.id.warning_cancel_button:
                if (searchText != null) {
                    searchText.setText("");
                }
                break;
            case R.id.warning_ensure_button:
                if (searchText != null) {
                    search = searchText.getText().toString().trim();
                    //sendRequest();
                }
                break;
            case R.id.warning_search_edit:
                dateDialog.show();
                break;
            default:
                break;
        }
    }
}