package com.gavin.myslidemenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 项目名称：MySlideMenu
 * 类描述：
 * 创建人：GaVin
 * 创建时间：2016/10/18 0018 23:54
 * 修改人：GaVin
 * 修改时间：2016/10/18 0018 23:54
 * 修改备注：修复了打开菜单后，主界面的ListView仍然可以获取焦点滑动的问题
 */

public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private  SlideMenu mSlideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        mSlideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mSlideMenu != null && mSlideMenu.getCurrentState() == SlideMenu.SlideState.Open) {
//            如果菜单栏打开，应该拦截
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSlideMenu != null && mSlideMenu.getCurrentState() == SlideMenu.SlideState.Open) {
//            如果菜单栏打开，应该消费
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //消费并关闭菜单栏
                mSlideMenu.closeSlideMenu();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

}
