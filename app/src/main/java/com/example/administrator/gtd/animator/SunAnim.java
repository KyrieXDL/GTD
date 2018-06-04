package com.example.administrator.gtd.animator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class SunAnim extends View {
    private Paint mPaint;// 绘图画笔

    private float startAngle= -90;
    private float tmpAngle= 0; //临时角度变量

    // 构造方法(初始化画笔)
    public SunAnim(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
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

        float startX=(R+5)* (float)Math.sin(tmpAngle)+getWidth()/2;
        float startY=getHeight()/2-(R+5)* (float)Math.cos(tmpAngle);
        float endX=(R+15)* (float)Math.sin(tmpAngle)+getWidth()/2;
        float endY=getHeight()/2-(R+15)* (float)Math.cos(tmpAngle);

        RectF rectF=new RectF(x,y,x+2*R,y+2*R);
        // RectF rectF=new RectF(150,150,350, 350);
        canvas.drawArc(rectF,startAngle,tmpAngle,false,mPaint);
        //canvas.drawLine(startX,startY,endX,endY,mPaint);
        //canvas.drawLine(getWidth()/2,getHeight()/2,getWidth()/2+10,getHeight()/2+10,mPaint);


    }


    public float getTmpAngle() {
        return tmpAngle;
    }

    public void setTmpAngle(float tmpAngle) {
        this.tmpAngle = tmpAngle;
        invalidate();
    }
}
