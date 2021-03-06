package com.example.administrator.gtd;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asd on 2/20/2017.
 */
public class UnfoldButton extends FloatingActionButton {
    private static final String TAG = "UnfoldButton";

    private static final int RECOVER_ROTATE = -1;//恢复旋转之前的状态
    private static final int UNFOLDING = 2;//菜单展开状态
    private static final int FOLDING = 1;//菜单折叠状态
    int flag = FOLDING;
    private boolean mRotatable = true;//图标是否应该旋转
    private Context mContext;
    private ViewGroup mRootView;//父view
    private FrameLayout mBackground; //菜单背景幕布

    private float mAlpha = 1f;//透明度
    private int length = 200;//子view展开的距离
    private float mScale = 0.8f;//展开之后的缩放比例
    private int mDuration = 400;//动画时长
    private static final int DO_ROTATE = 1;//旋转动画

    List<Map<String, Object>> elementList = new ArrayList<>();//保存添加的button
    private int totalAngle = 90;

    public UnfoldButton(final Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        //绘制完成后再调用
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                init();
                freshElement();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);//取消监听
            }
        });

        //点击展开菜单
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expendMenu();
            }
        });
    }

    /**
     * 初始化  要在view绘制完成后调用
     */
    private void init() {

        mRootView = (ViewGroup) getParent();
        mBackground = new FrameLayout(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                mRootView.getWidth(),
                mRootView.getHeight());
        mBackground.setLayoutParams(params);
        mBackground.setBackgroundColor(getResources().getColor(R.color.background2));
        mBackground.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expendMenu();
            }
        });
        mBackground.setAlpha(0);
        mBackground.setVisibility(INVISIBLE);
        mRootView.addView(mBackground);

    }

    /**
     * 通过addElement添加的button，在这里才是真正的添加到mBackground中
     */
    private void freshElement() {

        for (final Map<String, Object> element : elementList) {
            FloatingActionButton b = new FloatingActionButton(mContext);
            b.setImageResource((int) element.get("img"));//图标
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            //使添加的button偏移到unfoldButton
            lp.leftMargin = getLeft();
            lp.topMargin = getTop();
            b.setLayoutParams(lp);
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnClickListener listener = (OnClickListener) element.get("listener");
                    if (listener != null)
                        listener.onClick(v);
                    expendMenu();//缩回菜单
                }
            });
            b.setVisibility(INVISIBLE);
            b.setBackgroundTintList(ColorStateList.valueOf((int) element.get("color")));//背景颜色
            mBackground.addView(b);//添加
        }

    }


    /**
     * 改变mBackground的状态  显示或者是隐藏
     */
    private void changeBackgroudStatus() {
        ObjectAnimator alpha;
        if (flag == FOLDING) {//处于折叠状态
            alpha = ObjectAnimator.ofFloat(mBackground, "alpha", mAlpha);
            mBackground.setVisibility(VISIBLE);
        } else {
            alpha = ObjectAnimator.ofFloat(mBackground, "alpha", 0);
            alpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBackground.setVisibility(INVISIBLE);
                }
            });
        }
        alpha.setDuration(mDuration);
        alpha.start();
    }

    /**
     * 折叠与展开菜单
     * 如果button展开角度不对  要修改这个
     */
    private void expendMenu() {
        int count = mBackground.getChildCount();
        changeBackgroudStatus();//改变mBackground的状态
        if (flag == FOLDING) {//折叠状态  要展开
            for (int i = 0; i < count; i++) {
                View view = mBackground.getChildAt(i);
                view.setVisibility(VISIBLE);
                //开始平移  第一个参数是view 第二个是角度
                setTranslation(view, totalAngle / (count - 1) * (i - 0));
            }
            //开始旋转
            if (mRotatable) setRotateAnimation(this, DO_ROTATE);
            flag = UNFOLDING;
        } else {
            setBackTranslation();
            flag = FOLDING;
            //开始反向旋转 恢复原来的样子
            if (mRotatable) setRotateAnimation(this, RECOVER_ROTATE);
        }
    }


    /**
     * 添加button
     * 由于调用的时候一般都是在onCreate里面调用，所以直接添加到mBackground会有空指针异常
     * 所以先加入到一个链表，然后等绘制完成后再调用freshElement()添加
     *
     * @param imgSrc   菜单的图标
     * @param listener 菜单的点击事件
     * @param color    菜单按钮的背景颜色
     */
    public void addElement(int imgSrc, int color, final OnClickListener listener) {
        Map<String, Object> map = new HashMap<>();
        map.put("img", imgSrc);
        map.put("listener", listener);
        map.put("color", getResources().getColor(color));
        elementList.add(map);
    }



    /**
     * 设置旋转动画
     *
     * @param view
     * @param flag
     */
    public void setRotateAnimation(View view, int flag) {
        ObjectAnimator rotate = null;
        if (flag == DO_ROTATE)
            rotate = ObjectAnimator.ofFloat(view, "rotation", 135);
        else rotate = ObjectAnimator.ofFloat(view, "rotation", 0);
        rotate.setDuration(mDuration);
        rotate.start();
    }


    /**
     * 菜单展开动画  缩放+透明度+平移
     *
     * @param view
     * @param angle
     */
    public void setTranslation(View view, int angle) {
        int x = (int) (length * Math.sin(Math.toRadians(angle)));
        int y = (int) (length * Math.cos(Math.toRadians(angle)));
        Log.d("ICE", "angle" + angle + "y:" + y);
        ObjectAnimator tX = ObjectAnimator.ofFloat(view, "translationX", -x);
        ObjectAnimator tY = ObjectAnimator.ofFloat(view, "translationY", -y);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", mScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", mScale);

        AnimatorSet set = new AnimatorSet();
        set.play(tX).with(tY).with(alpha);
        set.play(scaleX).with(scaleY).with(tX);
        set.setDuration(mDuration);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    /**
     * 菜单缩回动画  与上面相反
     */
    private void setBackTranslation() {
        final int count = mBackground.getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = mBackground.getChildAt(i);
            ObjectAnimator tX = ObjectAnimator.ofFloat(view, "translationX", 0);
            ObjectAnimator tY = ObjectAnimator.ofFloat(view, "translationY", 0);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0);//透明度 0为完全透明
            AnimatorSet set = new AnimatorSet(); //动画集合
            set.play(tX).with(tY).with(alpha);
            set.setDuration(mDuration); //持续时间
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
            //动画完成后 设置为不可见
            final int finalI = i;
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(INVISIBLE);
                }
            });
        }
    }



    //设置方法
    public void setmAlpha(float mAlpha) {
        this.mAlpha = mAlpha;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setmScale(float mScale) {
        this.mScale = mScale;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public void setmRotatable(boolean mRotatable) {
        this.mRotatable = mRotatable;
    }

    public void setAngle(int angle) {
        this.totalAngle = angle;
    }
}