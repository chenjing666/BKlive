package com.biaoke.bklive.imagecycleview;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;


/**
 * 滚动的TextView
 */
public class ScrollTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {

    private Context mContext;

    //mInUp,mOutUp分别构成向下翻页的进出动画
    private Rotate3dAnimation mInUp;
    private Rotate3dAnimation mOutUp;

    private boolean toRun;

    private static int RUN = 9527;

    /**
     * 滚动间隔
     */
    private int mScrollInterval = 3000;


    /**
     * 轮播文字数组
     */
    private String[] mItems;

    /**
     * 当前显示的位置
     */
    private int mPosition;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();

    }

    private void init() {

        setFactory(this);

        mInUp = createAnim(true, true);
        mOutUp = createAnim(false, true);

        setInAnimation(mInUp);//当View显示时动画资源ID
        setOutAnimation(mOutUp);//当View隐藏是动画资源ID。

    }

    private Rotate3dAnimation createAnim( boolean turnIn, boolean turnUp){

        Rotate3dAnimation rotation = new Rotate3dAnimation(turnIn, turnUp);
        rotation.setDuration(300);//执行动画的时间
        rotation.setFillAfter(false);//是否保持动画完毕之后的状态
        rotation.setInterpolator(new AccelerateInterpolator());//设置加速模式

        return rotation;
    }


    //这里返回的TextView，就是我们看到的View,可以设置自己想要的效果
    public View makeView() {

        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(20);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(Color.BLACK);
        return textView;

    }

    /**
     * 设置轮播文字数组
     */
    public void setItems(String[] mItems){

        if(mItems == null){
            throw new IllegalArgumentException("轮播文字数组不能为null！");
        }

        this.mItems = mItems;
        mPosition = 0;

        if(mItems.length > 0){
            setText(mItems[mPosition]);
        }else{
            throw new IllegalArgumentException("轮播文字数组没有内容！");
        }
    }

    /**
     * 开始滚动
     */
    public void startScroll(){

        checkArray();

        toRun = true;
        handler.sendEmptyMessageDelayed(RUN,mScrollInterval);
    }

    /**
     * 开始滚动
     * @param mScrollInterval 再次滚动间隔，默认3000ms
     */
    public void startScroll(int mScrollInterval){

        checkArray();

        this.mScrollInterval = mScrollInterval;

        toRun = true;
        handler.sendEmptyMessageDelayed(RUN,mScrollInterval);
    }


    /**
     * 设置再次滚动间隔
     * @param mScrollInterval 滚动持续时间，默认300ms
     */
    public void setScrollInterval(int mScrollInterval){

        this.mScrollInterval = mScrollInterval;

    }

    /**
     * 获取当前位置
     * @return
     */
    public int getCurrentPosition(){
        return mPosition;
    }


    /**
     * 停止滚动
     */
    public void stopScroll(){
        toRun = false;
    }


    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == RUN) {

                if(toRun){

                    mPosition++;
                    if(mPosition == mItems.length){
                        mPosition = 0;
                    }
                    setText(mItems[mPosition]);
                    handler.sendEmptyMessageDelayed(RUN,mScrollInterval);
                }
            }
        }
    };

    /**
     * 校验数组是否为空
     */
    private void checkArray(){

        if(mItems == null){
            throw new IllegalArgumentException("没有设置轮播文字数组或轮播文字数组不能为null！");
        }

        if(mItems.length < 1){
            throw new IllegalArgumentException("轮播文字数组没有内容！");
        }

    }

    class Rotate3dAnimation extends Animation {
        private float mCenterX;
        private float mCenterY;
        private final boolean mTurnIn;
        private final boolean mTurnUp;
        private Camera mCamera;

        public Rotate3dAnimation(boolean turnIn, boolean turnUp) {
            mTurnIn = turnIn;
            mTurnUp = turnUp;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
            mCenterY = getHeight() ;
            mCenterX = getWidth() ;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            final float centerX = mCenterX ;
            final float centerY = mCenterY ;
            final Camera camera = mCamera;
            final int derection = mTurnUp ? 1: -1;

            final Matrix matrix = t.getMatrix();

            camera.save();
            if (mTurnIn) {
                camera.translate(0.0f, derection *mCenterY * (interpolatedTime - 1.0f), 0.0f);
            } else {
                camera.translate(0.0f, derection *mCenterY * (interpolatedTime), 0.0f);
            }
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }

}
