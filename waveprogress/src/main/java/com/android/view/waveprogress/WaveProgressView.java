package com.hou.videorecruitment.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 水波纹进度条
 * Created by hmj on 17/5/2.
 */

public class WaveProgress extends View {


    private Paint mSrcPaint,//源像素画笔
            mDstPaint,//目标像素画笔
            mTextPaint;// 字体的画笔
    private Path mPath;//路径
    private PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);//遮挡的模式
    private Drawable mMaskDrawable;

    private float mWavesHeight;//水波纹的高度

    public WaveProgress(Context context) {
        this(context, null);
    }

    public WaveProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private boolean isWaterStart = true;// 标志水波纹是否运动
    private float mTextSize = 80;//字体大小
    private int mMoveSpeed = 5;//用户设置的
    private int mWaveColor = Color.GRAY;// 水波纹的颜色
    private int mProgressBackground = Color.WHITE;//进度的背景色
    private int mTextColor = Color.BLACK;//字体颜色
    private int mMaxProgress = 100;//最大进度
    private int mProgress;//当前进度
    private int mWaveCount = 1;// 水波纹的数量
    private Bitmap mBitmap;//遮挡的位图





    private String mTextUnit = "%";//单位
    private int mWidth;//视图的宽
    private int mHeight;//视图的高
    private int mWavesMoveSpeed;//水波纹的移动速度
    private int mWaveAllHeight;//水波纹的整体高度
    private float mEveryHeight;//每刻度的高度

    /**
     * 获取最大刻度
     *
     * @return 最大刻度
     */
    public int getMaxProgress() {
        return mMaxProgress;
    }

    /**
     * 设置最大刻度
     *
     * @param mMaxProgress 刻度
     */
    public void setMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
        measureEveryHeight();
    }

    /**
     * 计算没刻度的高
     */
    private void measureEveryHeight() {
        mEveryHeight = mHeight / (float) mMaxProgress;
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.waveProgress);
        mWaveColor = typedArray.getColor(R.styleable.waveProgress_wave_color, Color.GRAY);
        mMaskDrawable = typedArray.getDrawable(R.styleable.waveProgress_wave_mask_src);
        mProgressBackground = typedArray.getColor(R.styleable.waveProgress_wave_background, Color.WHITE);
        mTextColor = typedArray.getColor(R.styleable.waveProgress_wave_text_color, Color.BLACK);
        isWaterStart = typedArray.getBoolean(R.styleable.waveProgress_wave_started, true);
        mProgress = typedArray.getInteger(R.styleable.waveProgress_wave_progress, 70);
        mMaxProgress = typedArray.getInteger(R.styleable.waveProgress_wave_max_progress, 100);
        mWavesHeight = typedArray.getDimension(R.styleable.waveProgress_waves_height, 50);
        mTextSize = typedArray.getDimension(R.styleable.waveProgress_wave_text_size, 80);
        mMoveSpeed = typedArray.getInteger(R.styleable.waveProgress_wave_move_speed, 5);
    }

    /**
     * drawable 转化
     *
     * @param drawable 指定的drawable
     * @return 返回bitmap图片
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null)
            return null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public WaveProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaveProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mSrcPaint = new Paint();
        mSrcPaint.setStrokeWidth(10);
        mSrcPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mSrcPaint.setAntiAlias(true);// 设置平滑度
        mSrcPaint.setColor(mWaveColor);//设置画笔颜色

        mDstPaint = new Paint();
        mDstPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mDstPaint.setStrokeWidth(10);
        mDstPaint.setAntiAlias(true);// 设置平滑度
        mDstPaint.setColor(mProgressBackground);//设置画笔颜色

        mTextPaint = new Paint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mPath = new Path();
        mBitmap = drawableToBitmap(mMaskDrawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
        measureEveryHeight();
        // 保存对应的宽
        setMeasuredDimension(mWidth, mHeight);
    }
    protected void onDraw(Canvas canvas) {
        if (mWavesMoveSpeed >= mWidth/2) {
            mWavesMoveSpeed = 0;
        }
        mWavesMoveSpeed += mMoveSpeed;
        mPath.reset();
        mWaveAllHeight = (int) ((mMaxProgress - mProgress) * mEveryHeight);
        mPath.moveTo(-mWidth + 2 * mWavesMoveSpeed, mWaveAllHeight);
        //视图水波波浪
        for (int i = 0; i <= 2 * mWaveCount; i++) {
            //控制点的公式  (（1-2n）/ 2n  +  i/n) * mWidth
            int contr = (int) (((1-2 * mWaveCount)/(float)(2* mWaveCount) + i / (float)mWaveCount) * mWidth);
            //结束点的公式 （(1-n + i）/n )*mWidth
            int end = (int) ((1-mWaveCount + i)/(float)(mWaveCount) * mWidth);
            mPath.cubicTo(contr + mWavesMoveSpeed * 2, mWavesHeight + mWaveAllHeight,contr + mWavesMoveSpeed * 2, mWaveAllHeight - mWavesHeight, end + mWavesMoveSpeed * 2, mWaveAllHeight);
        }

        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(0, 0, mWidth, mHeight, mDstPaint);
        canvas.drawPath(mPath, mSrcPaint);
        if (mBitmap != null) {
            //使用CLEAR作为PorterDuffXfermode绘制蓝色的矩形
            mSrcPaint.setXfermode(mMode);
            canvas.drawBitmap(mBitmap, 0, 0, mSrcPaint);
            //最后将画笔去除Xfermode
            mSrcPaint.setXfermode(null);
        }
        canvas.restoreToCount(layerId);
        String str = String.valueOf(mProgress) + mTextUnit;
        Rect rect = new Rect();
        mTextPaint.getTextBounds(str, 0, str.length(), rect);
        canvas.drawText(str, mWidth / 2 - rect.width() / 2, mHeight / 2 - rect.height() / 2, mTextPaint);
        waitMovement();
    }

    private void waitMovement() {
        if (isWaterStart) {
            postInvalidateDelayed(10);
        }
    }

    /**
     * 获取遮罩层
     *
     * @return 遮罩的图片位图
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * 设置遮罩图片
     *
     * @param mBitmap 遮罩的图片位图
     */
    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    /**
     * 获取遮罩的图片
     *
     * @return 图片
     */
    public Drawable getMaskDrawable() {
        return mMaskDrawable;
    }

    /**
     * 设置遮罩的图片
     *
     * @param mMaskDrawable 遮罩的图片
     */
    public void setmMaskDrawable(Drawable mMaskDrawable) {
        this.mMaskDrawable = mMaskDrawable;
        drawableToBitmap(this.mMaskDrawable);
    }

    /**
     * 设置图片
     *
     * @param context 上下文
     * @param resId   图片资源id
     */
    public void setMaskDrawable(Context context, int resId) {
        setmMaskDrawable(ContextCompat.getDrawable(context, resId));
    }

    /**
     * 获取波峰的高度
     *
     * @return 波峰的高度
     */
    public float getWavesHeight() {
        return mWavesHeight;
    }

    /**
     * 设置波峰的高度
     *
     * @param mWavesHeight 波峰的高度
     */
    public void setWavesHeight(float mWavesHeight) {
        this.mWavesHeight = mWavesHeight;
    }

    /**
     * 获取当前水波纹是否运动
     *
     * @return 水波纹运动状态
     */
    public boolean isWaterStart() {
        return isWaterStart;
    }

    /**
     * 设置是否开始运动感水波纹
     *
     * @param waterStart true 标示开始运动  false 表示暂停运动
     */
    public void setWaterStart(boolean waterStart) {
        isWaterStart = waterStart;
        waitMovement();
    }

    /**
     * 获取单位
     *
     * @return 返回单位
     */
    public String getTextUnit() {
        return mTextUnit;
    }

    /**
     * 设置单位
     *
     * @param mTextUnit 单位
     */
    public void setTextUnit(String mTextUnit) {
        this.mTextUnit = mTextUnit;
    }

    /**
     * 获取字体大小
     *
     * @return 返回字体的大小
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * 设置字体的大小
     *
     * @param mTextSize 设置字体的大小
     */
    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    /**
     * 获取水波的移动速度
     *
     * @return 返回波峰的高度
     */
    public int getMoveSpeed() {
        return mMoveSpeed;
    }

    /**
     * 设置水波的移动速度
     *
     * @param mMoveSpeed 波峰高度
     */
    public void setMoveSpeed(int mMoveSpeed) {
        this.mMoveSpeed = mMoveSpeed;
    }

    /**
     * 获取水波的颜色
     *
     * @return 返回水波的颜色
     */
    public int getWaterColor() {
        return mWaveColor;
    }

    /**
     * 设置水波的色值
     *
     * @param mWaterColor 水波的色值
     */
    public void setWaterColor(int mWaterColor) {
        this.mWaveColor = mWaterColor;
    }

    /**
     * 获取进度条的背景色
     *
     * @return
     */
    public int getProgressBackground() {
        return mProgressBackground;
    }

    /**
     * 设置进度条的背景色
     *
     * @param mProgressBackground 背景色
     */
    public void setProgressBackground(int mProgressBackground) {
        this.mProgressBackground = mProgressBackground;
    }

    /**
     * 得到进度字体颜色
     *
     * @return 返回字体颜色的色值
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 设置进度字体颜色
     *
     * @param mTextColor
     */
    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    /**
     * 获取当前进度
     *
     * @return 返回当前进度
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * 设置进度
     *
     * @param mProgress 进度
     */
    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
    }
}
