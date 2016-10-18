package com.gavin.myslidemenu.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.gavin.myslidemenu.ColorUtil;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by GaVin on 2016/9/25 0025.
 */

public class SlideMenu extends FrameLayout {

    private View mMenuView;//菜单的view
    private View mMainView;//主界面的view
    private ViewDragHelper mViewDragHelper;
    private int mWidth;
    private float dragRange;//拖拽范围
    private FloatEvaluator mFloatEvaluator;

    public SlideMenu(Context context) {
        super(context);
        init();
    }


    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }


    /*定义菜单栏的状态*/
    enum SlideState {
        Open, Close;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //简单的界面处理
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("slideMenu only have tow child");
        }
        mMenuView = getChildAt(0);
        mMainView = getChildAt(1);
    }

    /*
    * 该方法在onMeasure之后执行，可以初始化自己和子View的宽和高*/
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        dragRange = mWidth * 0.6f;//初始化拖拽范围（主界面宽的0.6倍）

    }

    /*让ViewDragHelper进行拦截*/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /*让ViewDragHelper进行消费*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private SlideState currentState = SlideState.Close;//菜单默认关闭

    /*初始化*/
    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, callback);
        mFloatEvaluator = new FloatEvaluator();//计算变化百分比,Float的计算器

    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         * child: 当前触摸的子View
         * return: true:就捕获并解析 false：不处理
         * 作用：获取可以移动的子View
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mMainView || child == mMenuView;
        }

        /*
        * 获取水平方向的拖拽范围*/
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        /*
         *控制child在水平方向的移动 left:
		 * 表示ViewDragHelper认为你想让当前child的left改变的值,left=chile.getLeft()+dx dx:
		 * 本次child水平方向移动的距离 return: 表示你真正想让child的left变成的值
         * 作用：用来控制view水平方向的移动的实际距离
         * */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mMainView) {
                if (left < 0) {
                    left = 0;//限制主界面的左边left作用只能滑到0.
                } else if (left > dragRange) {
                    left = (int) dragRange;//限制主界面的右边
                }
            }
            return left;
        }

        /*
        * 伴随动画*/
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mMenuView) {
                //固定住menuView（menuView移动多少，mainView移动多少）
                mMenuView.layout(0, 0, mMenuView.getMeasuredWidth(), mMenuView.getMeasuredHeight());
                //让mainView移动起来
                int newLeft = mMainView.getLeft() + dx;
                if (newLeft < 0) {
                    newLeft = 0;//限制mainView的左边
                } else if (newLeft > dragRange) {
                    newLeft = (int) dragRange;//限制mainView的右边
                }
//                移动mainView
                mMainView.layout(newLeft, mMainView.getTop() + dy,
                        mMainView.getRight() + dx, mMainView.getBottom() + dy);
            }
            //计算滑动的百分比
            float fraction = mMainView.getLeft() / dragRange;
            //执行伴随动画
            executeAnim(fraction);
            //更改状态，回调mOnSlideStateChangeListener的对应方法
            if (fraction == 0 && currentState != SlideState.Close) {
                currentState = SlideState.Close;
                if (mOnSlideStateChangeListener != null) {
                    mOnSlideStateChangeListener.onClose();
                }
            } else if (fraction == 1 && currentState != SlideState.Open) {
                currentState = SlideState.Open;
                if (mOnSlideStateChangeListener != null) {
                    mOnSlideStateChangeListener.onOpen();
                }
            }
            if (mOnSlideStateChangeListener != null) {
                mOnSlideStateChangeListener.onSliding(fraction);
            }
        }

        /*
        * view释放的时候*/
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mMainView.getLeft() < dragRange / 2) {
                //主界面在菜单显示的左半边
                closeSlideMenu();

            } else {
                //在菜单显示的右半边
                openSlideMenu();
            }
//            横向移动的速度大于200的时候打开或者关闭菜单栏。（处理用户的微滑动）
            if (xvel > 200 && currentState != SlideState.Open) {
                openSlideMenu();
            } else if (xvel < -200 && currentState != SlideState.Close) {
                closeSlideMenu();
            }
        }
    };

    /*关闭菜单栏*/
    public void closeSlideMenu() {
        mViewDragHelper.smoothSlideViewTo(mMainView, 0, mMainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /*打开菜单栏*/
    public void openSlideMenu() {

        mViewDragHelper.smoothSlideViewTo(mMainView, (int) dragRange, mMainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /*
    * 执行伴随动画*/
    private void executeAnim(float fraction) {
        //fraction:0-1
        //缩小mainView到原来的0.8倍
        float scaleMainValue = mFloatEvaluator.evaluate(fraction, 1f, 0.85f);
        ViewHelper.setScaleX(mMainView, scaleMainValue);
        ViewHelper.setScaleY(mMainView, scaleMainValue);
        //移动menuView
        float TranslationValue = mFloatEvaluator.evaluate(fraction, -mMenuView.getMeasuredWidth() / 2, 0);
        ViewHelper.setTranslationX(mMenuView, TranslationValue);
        // 放大menuView
        float ScaleMenuValue = mFloatEvaluator.evaluate(fraction, 0.5f, 1f);
        ViewHelper.setScaleX(mMenuView, ScaleMenuValue);
        ViewHelper.setScaleY(mMenuView, ScaleMenuValue);
        //设置menuView的透明度
        float AlphaMenuValue = mFloatEvaluator.evaluate(fraction, 0.3f, 1f);
        ViewHelper.setAlpha(mMenuView, AlphaMenuValue);
        //设置slideMenu的背景添加黑色的遮罩层
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }


    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    private OnSlideStateChangeListener mOnSlideStateChangeListener;

    public void setOnSlideStateChangeListener(OnSlideStateChangeListener mOnSlideStateChangeListener) {
        this.mOnSlideStateChangeListener = mOnSlideStateChangeListener;
    }

    public interface OnSlideStateChangeListener {
        //打开菜单的回调
        void onOpen();

        //关闭菜单的回调
        void onClose();

        //正在拖拽菜单的回调
        void onSliding(float fraction);

    }

    /*获取当前状态*/
    public SlideState getCurrentState() {
        return currentState;
    }


}
