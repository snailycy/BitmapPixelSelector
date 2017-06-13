package com.snailycy.bitmappixelselector;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.snailycy.bitmappixelselector.bean.BitmapPoint;
import com.snailycy.bitmappixelselector.util.BitmapUtils;
import com.snailycy.bitmappixelselector.util.ScreenUtils;

import java.util.Random;

/**
 * 在背景图的内容区域位置插入一个view
 *
 * @author snailycy
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private ImageView mBgIv;
    private FrameLayout mContentContainer;
    private ProgressBar mProgressBar;
    private int[] bgResIds = new int[]{R.mipmap.bg_demo1, R.mipmap.bg_demo2, R.mipmap.bg_demo3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBgIv = (ImageView) findViewById(R.id.iv_bg);
        mContentContainer = (FrameLayout) findViewById(R.id.content_container);
        findViewById(R.id.btn_change_bg).setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeBg();
    }

    private void changeBg() {
        Toast.makeText(this, "正在改变背景图", Toast.LENGTH_SHORT).show();
        int bgResId = bgResIds[new Random().nextInt(bgResIds.length)];
        mBgIv.setImageResource(bgResId);
        resetContentView();
    }

    private void resetContentView() {
        // 获取ImageView控件区域的图片(原背景图可能大于控件区域)
        mBgIv.setDrawingCacheEnabled(true);
        mBgIv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mBgIv.layout(0, 0, ScreenUtils.getScreenWidth(this), mBgIv.getMeasuredHeight());
        mBgIv.buildDrawingCache(true);
        final Bitmap bgBitmap = mBgIv.getDrawingCache();
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContentContainer.getLayoutParams();
        new AsyncTask<Void, Void, FrameLayout.LayoutParams>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(true);
            }

            @Override
            protected FrameLayout.LayoutParams doInBackground(Void... params) {
                BitmapPoint[] contentPoint = BitmapUtils.getContentFromBitmap(bgBitmap);
                int marginLeft = contentPoint[0].getX();
                int marginTop = contentPoint[0].getY();
                int marginRight = ScreenUtils.getScreenWidth(MainActivity.this) - contentPoint[1].getX();
                int contentHeight = contentPoint[1].getY() - contentPoint[0].getY();
                Log.d(TAG, "margin(l,t,r,b) = (" + marginLeft + "," + marginTop + "," + marginRight + ",0)");
                Log.d(TAG, "contentHeight = " + contentHeight);
                layoutParams.height = contentHeight;
                layoutParams.setMargins(marginLeft, marginTop, marginRight, 0);
                return layoutParams;
            }

            @Override
            protected void onPostExecute(FrameLayout.LayoutParams layoutParams) {
                super.onPostExecute(layoutParams);
                showLoading(false);
                mContentContainer.setLayoutParams(layoutParams);
                mBgIv.setDrawingCacheEnabled(false);
                mContentContainer.removeAllViews();
                LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_content, mContentContainer, true);
            }
        }.execute();
    }

    private void showLoading(boolean isShowing) {
        mProgressBar.setVisibility(isShowing ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_bg:
                changeBg();
                break;
        }
    }
}
