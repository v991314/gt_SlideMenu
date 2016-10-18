package com.gavin.myslidemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gavin.myslidemenu.view.MyLinearLayout;
import com.gavin.myslidemenu.view.SlideMenu;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mSlideMenu";
    private ListView mMenuListView;
    private ListView mMainListView;
    private SlideMenu mSlideMenu;
    private ImageView mImageView;
    private MyLinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//找寻控件
        setListener();//设置监听回调
        setAdapter();//设置菜单栏和主界面的适配数据
    }

    private void setListener() {
        mLinearLayout.setSlideMenu(mSlideMenu);
        mSlideMenu.setOnSlideStateChangeListener(new SlideMenu.OnSlideStateChangeListener() {
            @Override
            public void onOpen() {
//                Log.e(TAG, "onOpen: ");
            }

            @Override
            public void onClose() {
//                Log.e(TAG, "onClose: ");
//                这里采用的菜单栏有ListView实现，需要关闭时重置listview的位置，以便下次打开在顶部。
                mMenuListView.smoothScrollToPosition(0);
//                给主界面的头像设置抖动的动画，当打开菜单时，实现平移动画，
                ViewPropertyAnimator.animate(mImageView).translationXBy(15)
                        .setInterpolator(new CycleInterpolator(3))//设置循环插值器,循环3次
                        .setDuration(500)//持续5秒
                        .start();
            }

            /*当菜单栏正在打开或者关闭时，给头像设置百分比透明度*/
            @Override
            public void onSliding(float fraction) {
                ViewHelper.setAlpha(mImageView, 1 - fraction);
            }
        });
    }


    private void setAdapter() {
        mMenuListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (android.widget.TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }

        });
        mMainListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (android.widget.TextView) super.getView(position, convertView, parent);
                //先缩小view
                ViewHelper.setScaleX(textView, 0.9f);
                ViewHelper.setScaleY(textView, 0.9f);
                //以属性动画的形式放大
                ViewPropertyAnimator.animate(textView).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(textView).scaleY(1).setDuration(350).start();

                return textView;
            }

        });
    }

    private void initView() {
        mMenuListView = (ListView) findViewById(R.id.menu_listview);
        mMainListView = (ListView) findViewById(R.id.main_listview);
        mSlideMenu = (SlideMenu) findViewById(R.id.SlideMenu);
        mImageView = (ImageView) findViewById(R.id.iv_head);
        mLinearLayout = (MyLinearLayout) findViewById(R.id.my_layout);
    }
}
