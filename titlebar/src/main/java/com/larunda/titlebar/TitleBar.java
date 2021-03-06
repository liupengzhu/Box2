package com.larunda.titlebar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by sddt on 18-1-12.
 */

public class TitleBar extends LinearLayout {


    private Button leftButton;
    private Button rightButton;
    private Button leftBackButton;
    private TextView textView;
    private TextView rightText;
    private TitleListener mTitleListener;
    private Context mcontext;
    private TitleBarOnClickListener titleBarOnClickListener;

    public TitleBar(@NonNull Context context) {
        this(context, null, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext = context;
        LayoutInflater.from(context).inflate(R.layout.title, this);
        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);
        textView = findViewById(R.id.title_text);
        leftBackButton = findViewById(R.id.left_back_button);
        rightText = findViewById(R.id.right_text);
        rightText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleBarOnClickListener != null) {
                    titleBarOnClickListener.onClick(v);
                }
            }
        });
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleListener != null) {
                    InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(mcontext.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    mTitleListener.onLeftButtonClickListener(v);
                }
            }
        });
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleListener != null) {
                    mTitleListener.onRightButtonClickListener(v);
                }
            }
        });
        leftBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleListener != null) {
                    InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(mcontext.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    mTitleListener.onLeftBackButtonClickListener(v);
                }
            }
        });

    }

    public void setLeftButtonVisible(int visible) {
        leftButton.setVisibility(visible);
    }

    public void setLeftBackButtonVisible(int visible) {
        leftBackButton.setVisibility(visible);
    }

    public void setRightButtonVisible(int visible) {
        rightButton.setVisibility(visible);
    }

    public void setRightTextVisible(int visible) {
        rightText.setVisibility(visible);
    }

    public void setLeftButtonSrc(int src) {
        if (src != 0) {
            leftButton.setVisibility(VISIBLE);
            leftButton.setBackground(mcontext.getResources().getDrawable(src));
        } else {
            leftButton.setVisibility(GONE);
        }

    }

    public void setTextViewText(String title) {
        if (title != null && title != "") {
            textView.setVisibility(VISIBLE);
            textView.setText(title);
        } else {
            textView.setVisibility(GONE);
        }

    }

    public void setRightTextViewText(String text) {
        if (text != null && text != "") {
            textView.setVisibility(VISIBLE);
            textView.setText(text);
        } else {
            textView.setVisibility(GONE);
        }

    }

    public void setRightButtonSrc(int src) {
        if (src != 0) {
            rightButton.setVisibility(VISIBLE);
            rightButton.setBackground(mcontext.getResources().getDrawable(src));
        } else {
            rightButton.setVisibility(GONE);
        }
    }


    public void setOnClickListener(TitleListener titleListener) {
        mTitleListener = titleListener;
    }

    public interface TitleBarOnClickListener {
        void onClick(View v);
    }

    public void setTitleBarOnClickListener(TitleBarOnClickListener titleBarOnClickListener) {
        this.titleBarOnClickListener = titleBarOnClickListener;
    }
}
