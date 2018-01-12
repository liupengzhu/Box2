package com.example.battery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by sddt on 18-1-12.
 */

public class BatteryView extends View{

    private Paint strokepaint;
    private Path strokePath;
    private Paint paint;
    private int paintColor;
    private float fraction=0.01f;

    private int mWidth;
    private int mHeight;

    public BatteryView(Context context) {
        this(context,null,0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();

        strokePath.moveTo(1,1);
        strokePath.lineTo(mWidth-(mHeight-2)/3/2,1);
        strokePath.lineTo(mWidth-(mHeight-2)/3/2,(mHeight-2)/3 +1);
        strokePath.lineTo(mWidth-1,(mHeight-2)/3 +1);
        strokePath.lineTo(mWidth-1,(mHeight-2)/3*2 +1);
        strokePath.lineTo(mWidth-(mHeight-2)/3/2,(mHeight-2)/3*2 +1);
        strokePath.lineTo(mWidth-(mHeight-2)/3/2,mHeight-2+1);
        strokePath.lineTo(1,mHeight-2+1);
        strokePath.lineTo(1,1);
        canvas.drawPath(strokePath,strokepaint);
        RectF rectF = new RectF(mHeight/6,mHeight/6,  mHeight/6+(mWidth-mHeight/3-mHeight/6)*fraction,mHeight-mHeight/6);
        canvas.drawRect(rectF,paint);

    }



    private void init() {
        paintColor = Color.GREEN;
        strokepaint = new Paint();
        strokePath = new Path();
        strokepaint.setColor(paintColor);
        strokepaint.setAntiAlias(true);
        strokepaint.setStyle(Paint.Style.STROKE);
        strokepaint.setStrokeWidth(1);

        paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);



    }

    public void setPaintColor(int battery) {

        if(battery >= 66&&battery<=100){
            strokepaint.setColor(getResources().getColor(R.color.high));
            paint.setColor(getResources().getColor(R.color.high));
        }else if(battery >= 33&&battery<66){
            strokepaint.setColor(getResources().getColor(R.color.normal));
            paint.setColor(getResources().getColor(R.color.normal));
        }else if(battery>=0&&battery<33){
            strokepaint.setColor(getResources().getColor(R.color.low));
            paint.setColor(getResources().getColor(R.color.low));
        }else {
            strokepaint.setColor(getResources().getColor(R.color.isnull));
            paint.setColor(getResources().getColor(R.color.isnull));
        }
        if(battery==100){
            Log.d(battery+"",battery+"");
        }

        fraction = battery / 100f;


        postInvalidate();
    }


}
