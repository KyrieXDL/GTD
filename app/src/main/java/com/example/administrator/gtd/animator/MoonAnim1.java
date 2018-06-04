package com.example.administrator.gtd.animator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class MoonAnim1 extends View {
    // 设置需要用到的变量
    private Paint mPaint;// 绘图画笔

    private float startAngle= -90;
    private float tmpAngle= 0; //临时角度变量

    // 构造方法(初始化画笔)
    public MoonAnim1(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
    }

    // 复写onDraw()从而实现绘制逻辑
    // 绘制逻辑:先在初始点画圆,通过监听当前坐标值(currentPoint)的变化,每次变化都调用onDraw()重新绘制圆,从而实现圆的平移动画效果
    @Override
    protected void onDraw(Canvas canvas) {
        float R=getWidth()/8;
        float x=3*(getWidth()/8);
        float y=(getHeight()-2*R )/2;
        startAngle= -90;

        RectF rectF=new RectF(x,y,x+2*R,y+2*R);
        canvas.drawArc(rectF,startAngle,tmpAngle,false,mPaint);

    }


    public float getTmpAngle() {
        return tmpAngle;
    }

    public void setTmpAngle(float tmpAngle) {
        this.tmpAngle = tmpAngle;
        invalidate();
    }
}

