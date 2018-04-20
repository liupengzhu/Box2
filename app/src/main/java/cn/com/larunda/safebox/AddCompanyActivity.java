package cn.com.larunda.safebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.JsonObject;
import com.larunda.safebox.R;
import com.larunda.titlebar.TitleBar;
import com.larunda.titlebar.TitleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.com.larunda.safebox.util.HttpUtil;
import cn.com.larunda.safebox.util.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCompanyActivity extends AppCompatActivity implements View.OnClickListener {

    private final String URL = Util.URL + "company" + Util.TOKEN;
    private TitleBar titleBar;
    private ImageView pic;
    private EditText nameText;
    private EditText salesAddressText;
    private EditText addressText;
    private EditText faxText;
    private EditText emailText;
    private EditText contactsText;
    private EditText telText;
    private EditText letterText;
    private Button saveButton;
    private String src = "11";
    private SharedPreferences preferences;
    private String token;
    private LoadingDailog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        initView();
        intEvent();
    }

    /**
     * 初始化view
     */
    private void initView() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString("token", null);

        titleBar = findViewById(R.id.add_company_title_bar);
        titleBar.setTextViewText("添加企业");
        titleBar.setRightButtonSrc(0);
        titleBar.setLeftButtonVisible(View.GONE);
        titleBar.setLeftBackButtonVisible(View.VISIBLE);

        pic = findViewById(R.id.add_company_pic);
        nameText = findViewById(R.id.add_company_name);
        salesAddressText = findViewById(R.id.add_company_sales_address);
        addressText = findViewById(R.id.add_company_address);
        faxText = findViewById(R.id.add_company_fax);
        emailText = findViewById(R.id.add_company_email);
        contactsText = findViewById(R.id.add_company_contacts);
        telText = findViewById(R.id.add_company_tel);
        letterText = findViewById(R.id.add_company_letter);
        saveButton = findViewById(R.id.add_company_button);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("上传中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();

    }

    /**
     * 初始化点击事件
     */
    private void intEvent() {

        saveButton.setOnClickListener(this);

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
            case R.id.add_company_button:
                sendPostRequest();
                break;
            default:
                break;
        }
    }

    /**
     * 发送post请求
     */
    private void sendPostRequest() {
        String name = nameText.getText().toString().trim();
        String salesAddress = salesAddressText.getText().toString().trim();
        String address = addressText.getText().toString().trim();
        String fax = faxText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        final String contacts = contactsText.getText().toString().trim();
        String tel = telText.getText().toString().trim();
        String letter = letterText.getText().toString().trim();
        if (!isEmpty(name, salesAddress, address, fax, email, contacts, tel, letter)) {
            dialog.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("f_name", name);
                jsonObject.put("f_pic", src);
                jsonObject.put("f_tel", tel);
                jsonObject.put("f_add", address);
                jsonObject.put("f_letter", letter);
                jsonObject.put("f_fax", fax);
                jsonObject.put("f_sales_add", salesAddress);
                jsonObject.put("f_email", email);
                jsonObject.put("f_contacts", contacts);
                HttpUtil.sendPostRequestWithHttp(URL + token, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final int code = response.code();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                if (code == 200) {
                                    finish();
                                } else if (code == 422) {
                                    Toast.makeText(AddCompanyActivity.this, "名称或简码重复！", Toast.LENGTH_SHORT).show();
                                } else if (code == 401) {
                                    Intent intent = new Intent(AddCompanyActivity.this, LoginActivity.class);
                                    intent.putExtra("token_timeout", "登录超时");
                                    preferences.edit().putString("token", null).commit();
                                    startActivity(intent);
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

    public boolean isEmpty(String name, String salesAddress, String address, String fax, String email, String contacts, String tel, String letter) {
        if (name.isEmpty()) {
            Toast.makeText(this, "名称不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (salesAddress.isEmpty()) {
            Toast.makeText(this, "销售中心不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (address.isEmpty()) {
            Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (fax.isEmpty()) {
            Toast.makeText(this, "传真不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (email.isEmpty()) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (contacts.isEmpty()) {
            Toast.makeText(this, "联系人不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (tel.isEmpty()) {
            Toast.makeText(this, "联系电话不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (letter.isEmpty()) {
            Toast.makeText(this, "简码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
