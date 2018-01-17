package com.example.titlebar;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
    private TitleListener mTitleListener;
    private Context mcontext;
    private boolean isSetClick = false;

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
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSetClick) {
                    mTitleListener.onLeftButtonClickListener(v);
                }
            }
        });
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSetClick) {
                    mTitleListener.onRightButtonClickListener(v);
                }
            }
        });
        leftBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSetClick) {
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

    public void setLeftButtonSrc(int src) {
        if (src != 0) {
            leftButton.setVisibility(VISIBLE);
            leftButton.setBackground(mcontext.getResources().getDrawable(src));
        } else {
            leftButton.setVisibility(INVISIBLE);
        }

    }

    public void setTextViewText(String title) {
        if (title != null && title != "") {
            textView.setVisibility(VISIBLE);
            textView.setText(title);
        } else {
            textView.setVisibility(INVISIBLE);
        }

    }

    public void setRightButtonSrc(int src) {
        if (src != 0) {
            rightButton.setVisibility(VISIBLE);
            rightButton.setBackground(mcontext.getResources().getDrawable(src));
        } else {
            rightButton.setVisibility(INVISIBLE);
        }
    }

    public void setOnClickListener(TitleListener titleListener) {
        mTitleListener = titleListener;
        isSetClick = true;
    }


}
