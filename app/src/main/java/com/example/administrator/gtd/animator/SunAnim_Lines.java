package com.example.administrator.gtd.animator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class SunAnim_Lines extends View {
    private Paint mPaint;// 绘图画笔
    private double tmpAngle= 0; //临时角度变量
    private int linesNum=0;

    // 构造方法(初始化画笔)
    public SunAnim_Lines(Context context, AttributeSet attrs) {
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
        tmpAngle= 0;

        //ArrayList<Float> list=new ArrayList<>();
        float[] list=new float[linesNum*4];
        int n=0;
        for (int i=0;i<linesNum;i++){
            float startX=(float)((R+55)* Math.sin(tmpAngle)+getWidth()/2);
            float startY=(float)(getHeight()/2-(R+55)* Math.cos(tmpAngle));
            float endX=(float)((R+105)* Math.sin(tmpAngle)+getWidth()/2);
            float endY=(float)(getHeight()/2-(R+105)* Math.cos(tmpAngle));
            list[n]=startX;
            list[n+1]=startY;
            list[n+2]=endX;
            list[n+3]=endY;
            n=n+4;
            tmpAngle=tmpAngle+Math.PI/4;

        }
        canvas.drawLines(list,mPaint);

    }

    public int getLinesNum() {
        return linesNum;
    }

    public void setLinesNum(int linesNum) {
        this.linesNum = linesNum;
        invalidate();
    }
}
