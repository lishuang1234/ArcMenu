package com.ls.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.ls.arcmenu.R;

/**
 * Created by ls on 15-6-15.
 * 整体思路:
 * 1.自定义ViewGroup,构造函数获取配置值
 * 2.onLayout()确定子View包括一个功能按钮的位置,同时为功能按钮绑定监听
 * 3.功能按钮按下,处理本身动画(旋转),处理各个子Menu显示动画(移动和旋转),为各个MenuItem设置监听与回调,更新状态
 * 4.按下Menu时,设置各个MenuItem消失状态.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {
    private Position mPosition = Position.LEFT_TOP;
    private int mRadius = 100;//默认值为100dp
    private State mState = State.CLOSE;
    private OnMenuItemClickListener onMenuItemClickListener;
    private static final String TAG = "ArcMenu";

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    public void onClick(View v) {

        View view = findViewById(R.id.id_button);
        if (view == null) {
            view = getChildAt(0);
        }
        rotateView(view, 0, 270, 500);
        toggleMenu(500);
    }

    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    public enum State {
        OPEN, CLOSE
    }

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //dp转像素
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRadius, getResources().getDisplayMetrics());
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenu, defStyleAttr, 0);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.ArcMenu_position:
                    int value = typedArray.getInt(attr, 0);//获取配置值
                    switch (value) {
                        case 0:
                            mPosition = Position.LEFT_TOP;
                            break;
                        case 1:
                            mPosition = Position.RIGHT_TOP;
                            break;
                        case 2:
                            mPosition = Position.LEFT_BOTTOM;
                            break;
                        case 3:
                            mPosition = Position.RIGHT_BOTTOM;
                            break;
                    }
                    break;
                case R.styleable.ArcMenu_radius:
                    mRadius = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int count = getChildCount();
            layoutButton();
            for (int i = 0; i < count - 1; i++) {
                View view = getChildAt(i + 1);
                view.setVisibility(View.GONE);
                int cl = (int) (mRadius * Math.sin((Math.PI) / 2 / (count - 2) * i));
                int ct = (int) (mRadius * (Math.cos((Math.PI) / 2 / (count - 2) * i)));
                int childWidth = view.getMeasuredWidth();
                int childHeight = view.getMeasuredHeight();

                if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - childWidth - ct;
                }
                if (mPosition == Position.RIGHT_BOTTOM || mPosition == Position.RIGHT_TOP) {
                    cl = getMeasuredWidth() - childHeight - cl;
                }
                view.layout(cl, ct, cl + childWidth, ct + childHeight);
                Log.e(TAG + " onLayout", childWidth + ", " + childHeight);
            }
        }
    }

    private void layoutButton() {
        View button = getChildAt(0);
        button.setOnClickListener(this);
        int buttonWidth = button.getMeasuredWidth();
        int buttonHeight = button.getMeasuredHeight();
        int l = 0;
        int t = 0;
        switch (mPosition) {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - buttonWidth;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - buttonHeight;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - buttonWidth;
                t = getMeasuredHeight() - buttonHeight;
                break;
        }
        button.layout(l, t, l + buttonWidth, t + buttonHeight);
        Log.e(TAG + " layoutButton", buttonWidth + ", " + buttonHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void rotateView(View view, float from, float to, int duration) {
        RotateAnimation rotateAnimation = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(true);
        view.startAnimation(rotateAnimation);
    }

    /**
     * 处理子Menu显示/隐藏动画
     */
    public void toggleMenu(int duration) {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View view = getChildAt(i + 1);
            view.setVisibility(View.VISIBLE);
            int xFlag = 1;
            int yFlag = 1;

            if (mPosition == Position.LEFT_TOP) {
                xFlag = -1;
                yFlag = -1;
            }
            if (mPosition == Position.LEFT_BOTTOM) {
                xFlag = -1;
            }
            if (mPosition == Position.RIGHT_TOP) {
                yFlag = -1;
            }

            int cl = (int) (mRadius * Math.sin((Math.PI) / 2 / (count - 2) * i));
            int ct = (int) (mRadius * Math.cos((Math.PI) / 2 / (count - 2) * i));


            /**float fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
             float toXDelta, 这个参数表示动画结束的点离当前View X坐标上的差值；
             float fromYDelta, 这个参数表示动画开始的点离当前View Y坐标上的差值；
             float toYDelta)这个参数表示动画结束的点离当前View Y坐标上的差值；
             */
            AnimationSet animationSet = new AnimationSet(true);
            Animation animation = null;
            if (mState == State.CLOSE) {//显示
                animationSet.setInterpolator(new OvershootInterpolator(2f));
                animation = new TranslateAnimation(xFlag * cl, 0, yFlag * ct, 0);
                view.setClickable(true);
                view.setFocusable(true);
            } else {//隐藏
                animation = new TranslateAnimation(0, xFlag * cl, 0, yFlag * ct);
                view.setClickable(false);
                view.setFocusable(false);
            }

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mState == State.CLOSE) {//动画结束,根据状态设置可见性
                        view.setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            animation.setFillAfter(true);
            animation.setDuration(duration);
            animation.setStartOffset((i * 100) / count);
            RotateAnimation rotate = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(duration);
            rotate.setFillAfter(true);
            animationSet.addAnimation(rotate);
            animationSet.addAnimation(animation);
            view.startAnimation(animationSet);

            final int index = i + 1;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMenuItemClickListener != null)
                        onMenuItemClickListener.onMenuItemClick(index, v);
                    changeState();
                    menuItemAnin(index - 1);
                }
            });
        }
        changeState();
    }

    /**
     * 处理子Menu被点击之后隐藏动画
     */
    private void menuItemAnin(int index) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View childView = getChildAt(i + 1);
            if (i == index) {//被点击Item
                childView.startAnimation(scaleBigAnim(300));
            } else {
                childView.startAnimation(scaleSmallAnim(300));
            }
            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }

    /**
     * 缩小消失
     */
    private Animation scaleSmallAnim(int durationMillis) {

        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);
        return anim;
    }

    /**
     * 放大，透明度降低
     */
    private Animation scaleBigAnim(int durationMillis) {
        AnimationSet animationset = new AnimationSet(true);
        /**ScaleAnimation参数说明:
         * float fromX 动画起始时 X坐标上的伸缩尺寸
         float toX 动画结束时 X坐标上的伸缩尺寸
         float fromY 动画起始时Y坐标上的伸缩尺寸
         float toY 动画结束时Y坐标上的伸缩尺寸
         int pivotXType 动画在X轴相对于物件位置类型
         float pivotXValue 动画相对于物件的X坐标的开始位置
         int pivotYType 动画在Y轴相对于物件位置类型
         float pivotYValue 动画相对于物件的Y坐标的开始位置
         * */
        Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        Animation alphaAnimation = new AlphaAnimation(1, 0);
        animationset.addAnimation(anim);
        animationset.addAnimation(alphaAnimation);
        animationset.setDuration(durationMillis);
        animationset.setFillAfter(true);
        return animationset;
    }

    /**
     * 更新显示/隐藏状态
     */
    private void changeState() {
        mState = (mState == State.CLOSE ? State.OPEN
                : State.CLOSE);
    }


    public interface OnMenuItemClickListener {
        void onMenuItemClick(int index, View view);
    }
}
