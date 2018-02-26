package com.example.circletextview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by sddt on 18-2-24.
 */

public class CircleTextView extends View {
    private static final int BACKGROUND_CIRCLE_COLOR = Color.GRAY;
    private static final int CIRCLE_COLOR = Color.GREEN;

    private int backgroundCircleColor;
    private int circleColor;
    private float circleRadius;
    private float circleStroke;
    private int numberColor;
    private int textColor;
    private float numberSize;
    private float textSize;

    private float numberPadding;
    private float textPadding;
    private float circleAngle;

    private Paint backgroundPaint;
    private Paint circlePaint;
    private Paint numberPaint;
    private Paint textPaint;

    private String number;
    private String text;

    private ValueAnimator mAnimExpand;//展开动画
    protected float mAnimExpandHintFraction;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);
        if (array != null) {
            backgroundCircleColor = array.getColor(R.styleable.CircleTextView_backgroundCircleColor, BACKGROUND_CIRCLE_COLOR);
            circleColor = array.getColor(R.styleable.CircleTextView_circleColor, CIRCLE_COLOR);
            circleRadius = array.getDimension(R.styleable.CircleTextView_circleRadius,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getContext().getResources().getDisplayMetrics())
            );
            circleStroke = array.getDimension(R.styleable.CircleTextView_circleStroke,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics())
            );
            numberColor = array.getColor(R.styleable.CircleTextView_numberColor, BACKGROUND_CIRCLE_COLOR);

            textColor = array.getColor(R.styleable.CircleTextView_textColor, BACKGROUND_CIRCLE_COLOR);

            numberSize = array.getDimension(R.styleable.CircleTextView_numberSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getContext().getResources().getDisplayMetrics())
            );
            textSize = array.getDimension(R.styleable.CircleTextView_textSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getContext().getResources().getDisplayMetrics())
            );
            textPadding = array.getDimension(R.styleable.CircleTextView_textPadding,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getContext().getResources().getDisplayMetrics())
            );
            numberPadding = array.getDimension(R.styleable.CircleTextView_numberPadding,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics())
            );
            circleAngle = array.getFloat(R.styleable.CircleTextView_circleAngle, 0);
            number = array.getString(R.styleable.CircleTextView_number);
            text = array.getString(R.styleable.CircleTextView_text);
            array.recycle();
        }
        if (number == null) {
            number = "25";
        }
        if (text == null) {
            text = "正在运行";
        }
        init();
        //初始化动画
        initAnim();
    }

    private void initAnim() {
        mAnimExpand = ValueAnimator.ofFloat(0, 1);
        mAnimExpand.setDuration(1000);
        mAnimExpand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimExpandHintFraction = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimExpand.start();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        //初始化 抗抖动
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(circleStroke);
        backgroundPaint.setColor(backgroundCircleColor);


        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circleStroke);
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);

        numberPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(numberColor);
        numberPaint.setTextSize(numberSize);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (wMode == MeasureSpec.EXACTLY) {
            width = wSize;
        } else {
            width = (int) (getPaddingLeft() + getPaddingRight() + circleRadius * 2 + circleStroke + circleStroke / 2 + 1);
        }
        if (hMode == MeasureSpec.EXACTLY) {
            height = hSize;
        } else {
            height = (int) (getPaddingTop() + getPaddingBottom() + circleRadius * 2 + circleStroke + circleStroke / 2 + 1);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(new RectF(circleStroke / 2, circleStroke / 2,
                        circleRadius * 2 + circleStroke, circleRadius * 2 + circleStroke),
                0, 360, false, backgroundPaint);
        canvas.drawArc(new RectF(circleStroke / 2, circleStroke / 2,
                        circleRadius * 2 + circleStroke, circleRadius * 2 + circleStroke),
                -90, circleAngle * mAnimExpandHintFraction, false, circlePaint);
        Rect numberRect = new Rect();
        numberPaint.getTextBounds(number, 0, number.length(), numberRect);
        canvas.drawText(number, circleStroke / 2 + circleStroke / 4 + circleRadius - numberRect.width() / 2,
                circleStroke / 2 + circleStroke / 4 + circleRadius - numberPadding, numberPaint);
        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        canvas.drawText(text, circleStroke / 2 + circleStroke / 4 + circleRadius - textRect.width() / 2,
                circleStroke / 2 + circleStroke / 4 + circleRadius + textRect.height() + textPadding, textPaint);
    }

    public int getBackgroundCircleColor() {
        return backgroundCircleColor;
    }

    public void setBackgroundCircleColor(int backgroundCircleColor) {
        this.backgroundCircleColor = backgroundCircleColor;
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    public float getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
    }

    public float getCircleStroke() {
        return circleStroke;
    }

    public void setCircleStroke(float circleStroke) {
        this.circleStroke = circleStroke;
    }

    public int getNumberColor() {
        return numberColor;
    }

    public void setNumberColor(int numberColor) {
        this.numberColor = numberColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getNumberSize() {
        return numberSize;
    }

    public void setNumberSize(float numberSize) {
        this.numberSize = numberSize;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getNumberPadding() {
        return numberPadding;
    }

    public void setNumberPadding(float numberPadding) {
        this.numberPadding = numberPadding;
    }

    public float getTextPadding() {
        return textPadding;
    }

    public void setTextPadding(float textPadding) {
        this.textPadding = textPadding;
    }

    public float getCircleAngle() {
        return circleAngle;
    }

    public void setCircleAngle(float circleAngle) {
        this.circleAngle = circleAngle;
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    public Paint getCirclePaint() {
        return circlePaint;
    }

    public void setCirclePaint(Paint circlePaint) {
        this.circlePaint = circlePaint;
    }

    public Paint getNumberPaint() {
        return numberPaint;
    }

    public void setNumberPaint(Paint numberPaint) {
        this.numberPaint = numberPaint;
    }

    public Paint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
