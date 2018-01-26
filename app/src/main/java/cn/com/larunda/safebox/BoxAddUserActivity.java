package cn.com.larunda.safebox;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.larunda.safebox.adapter.BoxAddUserAdapter;
import cn.com.larunda.safebox.recycler.BoxAddUser;

public class BoxAddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private Button addButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BoxAddUserAdapter adapter;
    private List<BoxAddUser> boxAddUserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_add_user);

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
        addButton.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (int i = 0; i < 7; i++) {
            BoxAddUser boxAddUser = new BoxAddUser("张三" + i, "家乐福", "采购员", "189652156471");
            boxAddUserList.add(boxAddUser);
        }
    }

    /**
     * 初始化View
     */
    private void initView() {

        titleBar = findViewById(R.id.box_add_user_title_bar);
        titleBar.setTextViewText("");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        addButton = findViewById(R.id.box_add_user_add_button);

        recyclerView = findViewById(R.id.box_add_user_recycler);
        manager = new LinearLayoutManager(this);
        adapter = new BoxAddUserAdapter(this, boxAddUserList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_add_user_add_button:
                Intent bindingUserIntent = new Intent(this, BindingUserActivity.class);
                startActivity(bindingUserIntent);
                break;
            default:
                break;

        }

    }
}
