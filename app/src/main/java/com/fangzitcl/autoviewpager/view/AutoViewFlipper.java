package com.fangzitcl.autoviewpager.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.fangzitcl.autoviewpager.R;
import com.fangzitcl.autoviewpager.model.BasePagerModel;
import com.fangzitcl.autoviewpager.model.Gravity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * class_name: AutoViewFlipper
 * package_name: com.fangzitcl.autoviewpager
 * acthor: Fang_QingYou
 * time: 2015/12/22 21:09
 */
public class AutoViewFlipper extends RelativeLayout {

    private final static int mViewFlipperID = 1 * 100;
    private final static int mRadioGroupID = 2 * 100;
    private final static int mTitleID = 3 * 100;

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    private int showTime = 3 * 1000; // 显示时间
    private final static String showNext = "ShowNext";

    private Context mContext;
    private LayoutParams mLayoutParams;

    private ViewFlipper mViewFlipper; //  充当 viewPager
    private RadioGroup mRadioGroup; //  指示器容器
    private TextView mTitle; // 标题


    private boolean showIndicator; // 指示器显示标记 ， true 显示
    private boolean showTitle; // 标题显示标记 ， true 显示
    private boolean isLoop; // 默认循环


    private Gravity indicatorGravity; // 默认底部居右


    private float defaultBottomHeight = 20;// 默认底部高度 20dp
    private float defaultTitleSize = 12;// 默认字体大小 12sp
    private int currentIndex; // 当前位置
    private int count; // 总数

    private int startX; // 滑动的开始位置

    private int sensitive = 50; // 默认灵敏度 为50dp

    private ArrayList<BasePagerModel> mArrayList;
    private OnItemClickListener mListerner;

    private Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (showNext.equals(msg.obj)) {
                showNext();
            }
        }
    };

    private int titleBackground;
    private int indicatorBackground;
    private int indicatorSelector;


    public AutoViewFlipper(Context context) {
        this(context, null, 0);
    }

    public AutoViewFlipper(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoViewFlipper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        initView();

        TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AutoViewFlipper);

        setShowTitle(mTypedArray.getBoolean(R.styleable.AutoViewFlipper_show_title, true));
        setShowIndicator(mTypedArray.getBoolean(R.styleable.AutoViewFlipper_show_indicator, true));
        setLoop(mTypedArray.getBoolean(R.styleable.AutoViewFlipper_loop, true));
        setIndicatorGravity(Gravity.isGravity(mTypedArray.getInt(R.styleable.AutoViewFlipper_gravity, Gravity.right.getValue())));
        setTitleBackground(mTypedArray.getColor(R.styleable.AutoViewFlipper_title_background, getResources().getColor(android.R.color.transparent)));
        setIndicatorBackground(mTypedArray.getColor(R.styleable.AutoViewFlipper_indicator_background, getResources().getColor(android.R.color.transparent)));
        sensitive = mTypedArray.getDimensionPixelOffset(R.styleable.AutoViewFlipper_sensitive, valueToDp(sensitive));
        showTime = mTypedArray.getInteger(R.styleable.AutoViewFlipper_showTime, showTime);
        setIndicatorSelector(mTypedArray.getResourceId(R.styleable.AutoViewFlipper_showTime, R.drawable.indicator_bg_selector));


        mTypedArray.recycle();
    }

    // 初始化布局
    private void initView() {

        initViewFlipper();
        initRadioGroup();
        initTitle();

    }

    private void initViewFlipper() {
        // 初始化 mViewFlipper
        mViewFlipper = new ViewFlipper(mContext);
        mViewFlipper.setId(mViewFlipperID);
        mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mViewFlipper, mLayoutParams);
    }

    private void initRadioGroup() {
        // 初始化 mRadioGroup
        mRadioGroup = new RadioGroup(mContext);
        mRadioGroup.setId(mRadioGroupID);
        mRadioGroup.setBackgroundColor(Color.TRANSPARENT);
        mRadioGroup.setOrientation(LinearLayout.HORIZONTAL);

        if (Gravity.left.equals(indicatorGravity)) {

            mTitle.setVisibility(GONE);
            mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, valueToDp(defaultBottomHeight));
            mRadioGroup.setGravity(android.view.Gravity.LEFT);

        } else if (Gravity.center.equals(indicatorGravity)) {

            mTitle.setVisibility(GONE);
            mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, valueToDp(defaultBottomHeight));
            mRadioGroup.setGravity(android.view.Gravity.CENTER);

        } else if (Gravity.right.equals(indicatorGravity)) {

            mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, valueToDp(defaultBottomHeight));
            mRadioGroup.setGravity(android.view.Gravity.RIGHT);

        }

        mLayoutParams.addRule(ALIGN_PARENT_BOTTOM); // 右下角
        mLayoutParams.addRule(ALIGN_PARENT_RIGHT); // 右下角
        addView(mRadioGroup, mLayoutParams);
    }

    private void initTitle() {
        // 初始化 mTitle
        mTitle = new TextView(mContext);
        mTitle.setId(mTitleID);
        mRadioGroup.setBackgroundColor(Color.TRANSPARENT);
        mTitle.setSingleLine(); // 单行
        mTitle.setTextColor(Color.WHITE); // 单行
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTitleSize);
        mTitle.setEllipsize(TextUtils.TruncateAt.END); // 省略号在结尾
        mTitle.setPadding(20, 0, 20, 0);

        mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, valueToDp(defaultBottomHeight));
        mLayoutParams.addRule(LEFT_OF, mRadioGroupID); // 在mRadioGroupID 的左边,底部对齐
        mLayoutParams.addRule(ALIGN_PARENT_BOTTOM); // 在mRadioGroupID 的左边,底部对齐
        addView(mTitle, mLayoutParams);
    }

    // 设置指示器的位置
    private void setIndicator() {
        removeView(mRadioGroup);
        initRadioGroup();
    }

    /**
     * 设置显示数据
     *
     * @param arrayList
     */
    public void setShowData(ArrayList<BasePagerModel> arrayList) {
        pauseTimer();
        setIndicator();

        mArrayList = arrayList;
        if (mArrayList != null && mArrayList.size() > 0) {
            ImageView mImageView = null;
            RadioButton mRadioButton = null;

            int i = 0;
            for (BasePagerModel model : mArrayList) {
                mImageView = new ImageView(mContext);
                mImageView.setLayoutParams(mLayoutParams);
                mImageView.setScaleType(ScaleType.CENTER_CROP);
                Picasso.with(mContext).load(mArrayList.get(i).getImagePath()).into(mImageView);// TODO 图片

                mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                mViewFlipper.addView(mImageView, mLayoutParams);


                if (getShowIndicator()) {

                    mRadioButton = new RadioButton(mContext);
                    mRadioButton.setButtonDrawable(indicatorSelector);
                    mRadioButton.setEnabled(false);
                    mRadioGroup.addView(mRadioButton);

                }
                i++;
            }

            refreshData(currentIndex);
            resumeTimer();
            count = mArrayList.size();
        }
    }


    public int getSensitive() {
        return sensitive;
    }

    public void setSensitive(int sensitive) {
        this.sensitive = valueToDp(sensitive);
        Log.e("test", " sensitive == " + sensitive);
    }

    private void sendMessage() {
        Message message = new Message();
        message.obj = showNext;
        handler.sendMessage(message);
    }


    private void showNext() {
        if (getLoop() && currentIndex >= count - 1) {
            //             无限循环状态下，到达最后一个，再次执行setShowNext 需要回到第一个
            currentIndex = 0;
        } else if (currentIndex >= count - 1) {
            //             不循环状态下执行到最后一个，不继续执行
            currentIndex = mArrayList.size() - 1;
            if (timer != null) {
                timer.cancel();
            }
            return;
        } else {
            // 不论循环、不循环中间位置执行setShowNext ，都需要跳到下一个
            currentIndex++;
        }

        mViewFlipper.showNext();
        refreshData(currentIndex);
    }


    private void showPrevious() {
        if (getLoop() && currentIndex <= 0) {
            // 循环状态下 在第一个位置执行此方法，要回到最后一个位置
            currentIndex = mArrayList.size() - 1;
        } else if (currentIndex <= 0) {

            // 不循环状态下 在第一个位置执行此方法 不继续执行
            currentIndex = 0;
            if (timer != null) {
                timer.cancel();
            }
            return;
        } else {
            currentIndex--;
        }

        mViewFlipper.showPrevious();
        refreshData(currentIndex);
    }


    public boolean onTouchEvent(MotionEvent event) {
        // 不让父控件干扰
        getParent().requestDisallowInterceptTouchEvent(true);
        pauseTimer();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                break;

            case MotionEvent.ACTION_UP:
                int tag = (int) (event.getX() - startX);

                if (tag > sensitive) {
                    // 向左滑动
                    showPrevious();
                } else if (tag < -sensitive) {
                    // 向右滑动
                    showNext();
                } else if (tag == 0) {
                    if (mListerner != null) {
                        mListerner.onItemClickListener(currentIndex);
                    }
                }
                break;

        }
        resumeTimer();
        return true; // 自己消费
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getCount() {
        return count;
    }

    public Gravity getIndicatorGravity() {
        return indicatorGravity;
    }

    public float getRawSize(int unit, float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

    public int valueToDp(float value) {
        return (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, value);
    }

    /**
     * 根据当前位置,重新设置数据
     *
     * @param currentIndex
     */
    private void refreshData(int currentIndex) {

        if (getShowTitle()) {
            mTitle.setText(mArrayList.get(currentIndex).getTitle());
        }
        if (getShowIndicator()) {
            ((RadioButton) mRadioGroup.getChildAt(currentIndex)).setChecked(true);
        }
    }

    /**
     * 是否移动
     *
     * @param startX
     * @param x
     * @return 根据返回值做判断, 0为点击事件, (
     */
    private float isMove(int startX, float x) {
        return x - startX - valueToDp(sensitive);
    }

    /**
     * 设置标题
     *
     * @param showTitle 默认为显示(true)
     */
    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    /**
     * 设置指示器
     *
     * @param showIndicator 默认为显示(true)
     */
    public void setShowIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
    }

    /**
     * 设置循环
     *
     * @param loop 默认为显示(true)
     */
    public void setLoop(boolean loop) {
        this.isLoop = loop;
    }

    public boolean getLoop() {
        return isLoop;
    }

    public void setIndicatorGravity(Gravity indicatorGravity) {
        this.indicatorGravity = indicatorGravity;
    }

    public void setTitleBackground(int titleBackground) {
        this.titleBackground = titleBackground;
    }

    public void setIndicatorBackground(int indicatorBackground) {
        this.indicatorBackground = indicatorBackground;
    }

    public void setIndicatorSelector(int indicatorSelector) {
        this.indicatorSelector = indicatorSelector;
    }

    interface OnItemClickListener {
        void onItemClickListener(int index);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        if (listener != null) {
            mListerner = listener;
        }
    }

    private void pauseTimer() {
        if (timer != null) {
            this.timer.cancel();
        }
    }

    public boolean getShowTitle() {
        return showTitle;
    }

    public boolean getShowIndicator() {
        return showIndicator;
    }

    private void resumeTimer() {
        if (getLoop()) {
            if (timer != null) {
                this.timer.cancel();
                this.timer = new Timer();
                this.timer.schedule(getTask(), showTime, showTime);
            } else {
                this.timer = new Timer();
                this.timer.schedule(getTask(), showTime, showTime);
            }
        }
    }

    @NonNull
    private TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
                sendMessage();
            }
        };
    }


}

