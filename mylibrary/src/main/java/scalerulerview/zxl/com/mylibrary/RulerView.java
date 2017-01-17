package scalerulerview.zxl.com.mylibrary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.math.BigDecimal;

/******
 *
 */
public class RulerView extends SurfaceView {
    private Context mContext;
    private float mShortLineHeight;//短线的高度
    private float mShortLineWidth;//短线宽度
    private float mHighLineWidth;//长线宽度
    private int mSmallPartitionCount;//长线间隔数量
    private float mIndicatorHalfWidth;//三角指示器宽度一半
    private float mIndicatorTextTopMargin;
    private float mLineTopMargin;
    private int mStartValue;//起止数值
    private int mEndValue;
    private int mPartitionValue;//大刻度差值
    private float mPartitionWidth;//大刻度宽度
    private int mOriginValue;//初始值和精确刻度
    private int mOriginValueSmall;
    private int mCurrentValue;//当前值
    private int mValueTextsize;//大刻度字体大小
    private int mScaleTextsize;//大刻度字体大小
    protected int mMinVelocity;//滑动速度
    private Paint mBgPaint;//背景画笔
    private Paint mShortLinePaint;//短线画笔
    private Paint mHighLinePaint;//长线画笔
    private Paint mIndicatorTxtPaint;//三角指示器画笔
    private Paint mIndicatorViewPaint;//刻度数字画笔
    private Paint mValuePaint;//当前值画笔
    private Paint mDanweiPaint;//单位画笔
    //往右边去能偏移的最大值
    private float mRightOffset;
    //往左边去能偏移的最大值
    private float mLeftOffset;
    //移动的距离
    private float mMoveX = 0f;

    private float mWidth, mHeight;

    private Scroller mScroller;
    protected VelocityTracker mVelocityTracker;

    private OnValueChangeListener listener;

    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public void setValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        mScroller = new Scroller(context);

        mMinVelocity = ViewConfiguration.get(getContext())
                .getScaledMinimumFlingVelocity();

        initValue(context,attrs);

        initPaint();

    }
    int bgColor,keduColor,valueColor;
    String danwei;
    private void initPaint() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(bgColor);

        mShortLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShortLinePaint.setColor(Color.WHITE);
        mShortLinePaint.setStrokeWidth(mShortLineWidth);

        mHighLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighLinePaint.setColor(Color.WHITE);
        mHighLinePaint.setStrokeWidth(mHighLineWidth);

        mIndicatorTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorTxtPaint.setColor(keduColor);
        mIndicatorTxtPaint.setTextSize(mScaleTextsize);

        mIndicatorViewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorViewPaint.setColor(Color.WHITE);

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(valueColor);
        mValuePaint.setStrokeWidth(mHighLineWidth);
        mValuePaint.setTextSize(mValueTextsize);

        mDanweiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDanweiPaint.setColor(Color.DKGRAY);
        mDanweiPaint.setStrokeWidth(mHighLineWidth);
        mDanweiPaint.setTextSize(18);
    }

    private void initValue(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        mIndicatorHalfWidth = Utils.convertDpToPixel(mContext, 11.5f);
        mHighLineWidth = Utils.convertDpToPixel(mContext, 2f);
        mShortLineWidth = Utils.convertDpToPixel(mContext, 1f);
        mLineTopMargin = Utils.convertDpToPixel(mContext, 0.33f);
        mIndicatorTextTopMargin = Utils.convertDpToPixel(mContext, 21f);

        mSmallPartitionCount = ta.getInteger(R.styleable.RulerView_rv_item_cell_count,10);
        mOriginValue = ta.getInteger(R.styleable.RulerView_rv_rule_default_value,100);
        mOriginValueSmall = ta.getInteger(R.styleable.RulerView_rv_rule_default_cell,3);
        mPartitionValue = ta.getInteger(R.styleable.RulerView_rv_item_value,1);
        mStartValue = ta.getInteger(R.styleable.RulerView_rv_rule_start,50);
        mEndValue = ta.getInteger(R.styleable.RulerView_rv_rule_end,250);

        bgColor=ta.getColor(R.styleable.RulerView_rv_rule_color,Color.argb(255, 251, 221, 0));
        keduColor=ta.getColor(R.styleable.RulerView_rv_item_font_color,Color.WHITE);
        valueColor=ta.getColor(R.styleable.RulerView_rv_value_font_color,Color.argb(255, 236, 61, 52));
        mScaleTextsize = (int) ta.getDimension(R.styleable.RulerView_rv_item_font_size,26f);
        mValueTextsize = (int) ta.getDimension(R.styleable.RulerView_rv_value_font_size,46f);
        mShortLineHeight=(int) ta.getDimension(R.styleable.RulerView_rv_short_height,16.5f);
        mPartitionWidth=(int) ta.getDimension(R.styleable.RulerView_rv_item_width,89f);
        danwei=ta.getString(R.styleable.RulerView_rv_danwei);
        if (TextUtils.isEmpty(danwei)) danwei="cm";
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawIndicator(canvas);
        drawLinePartition(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    /**
     * 画背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0+H, mWidth, mHeight+H, mBgPaint);
    }

    private final int H = 80;

    /**
     * 画指示器
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        Path path = new Path();
        path.moveTo(mWidth / 2 - mIndicatorHalfWidth, 0+H);
        path.lineTo(mWidth / 2, mIndicatorHalfWidth+H);
        path.lineTo(mWidth / 2 + mIndicatorHalfWidth, 0+H);
        canvas.drawPath(path, mIndicatorViewPaint);
    }

    private float mOffset = 0f;

    private void drawLinePartition(Canvas canvas) {
        //计算半个屏幕能有多少个partition
        int halfCount = (int) (mWidth / 2 / mPartitionWidth);
        //根据偏移量计算当前应该指向什么值
        mCurrentValue = mOriginValue - (int) (mMoveX / mPartitionWidth) * mPartitionValue;
        //相对偏移量是多少, 相对偏移量就是假设不加入数字来指示位置， 范围是0 ~ mPartitionWidth的偏移量
        mOffset = mMoveX - (int) (mMoveX / mPartitionWidth) * mPartitionWidth;
        if (null != listener) {
            float count = -(mOffset / (mPartitionWidth / mSmallPartitionCount));
            int v=new BigDecimal(count).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            float value=mCurrentValue + v * (float) mPartitionValue / mSmallPartitionCount;
            String valStr=Float.toString(value);
            Rect bounds = new Rect();
            mValuePaint.getTextBounds(valStr, 0, valStr.length(), bounds);
            canvas.drawText(valStr, mWidth / 2-bounds.width()/2,bounds.height(),mValuePaint);

            Rect bounds1 = new Rect();
            mDanweiPaint.getTextBounds(danwei, 0, danwei.length(), bounds1);
            canvas.drawText(danwei, mWidth / 2+bounds.width()/2+20,bounds1.height()+5,mDanweiPaint);
            listener.onValueChange(value);
        }

        // draw high line and  short line
        for (int i = -halfCount - 1; i <= halfCount + 1; i++) {
            int val = mCurrentValue + i * mPartitionValue;
            //只绘出范围内的图形
            if (val >= mStartValue && val <= mEndValue) {
                //画长的刻度
                float startx = mWidth / 2 + mOffset + i * mPartitionWidth;
                if (startx > 0 && startx < mWidth) {
                    canvas.drawLine(mWidth / 2 + mOffset + i * mPartitionWidth, 0 + mLineTopMargin+H,
                            mWidth / 2 + mOffset + i * mPartitionWidth, 0 + mLineTopMargin + 2 * mShortLineHeight+H, mHighLinePaint);

                    //画刻度值
                    canvas.drawText(val + "", mWidth / 2 + mOffset + i * mPartitionWidth - mIndicatorTxtPaint.measureText(val + "") / 2,
                            0 + mLineTopMargin + 2 * mShortLineHeight + mIndicatorTextTopMargin + Utils.calcTextHeight(mIndicatorTxtPaint, val + "")+H, mIndicatorTxtPaint);
                }

                //画短的刻度
                if (val != mEndValue) {
                    for (int j = 1; j < mSmallPartitionCount; j++) {
                        float start_x = mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount;
                        if (start_x > 0 && start_x < mWidth) {
                            //偶数个的话最中间的短线画1.5
                            if (mSmallPartitionCount % 2 == 0 && j == mSmallPartitionCount / 2 && mSmallPartitionCount / 2 > 1) {
                                canvas.drawLine(mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount, 0 + mLineTopMargin+H,
                                        mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount, 0 + mLineTopMargin + 1.5f * mShortLineHeight+H, mShortLinePaint);
                            } else {
                                canvas.drawLine(mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount, 0 + mLineTopMargin+H,
                                        mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount, 0 + mLineTopMargin + mShortLineHeight+H, mShortLinePaint);
                            }
                        }
                    }
                }

            }

        }
    }

    private boolean isActionUp = false;
    private float mLastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float xPosition = event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isActionUp = false;
                mScroller.forceFinished(true);
                if (null != animator) {
                    animator.cancel();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isActionUp = false;
                float off = xPosition - mLastX;

                if ((mMoveX <= mRightOffset) && off < 0 || (mMoveX >= mLeftOffset) && off > 0) {

                } else {
                    mMoveX += off;
                    postInvalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isActionUp = true;
                f = true;
                countVelocityTracker(event);
                return false;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private ValueAnimator animator;

    private boolean isCancel = false;

    private void startAnim() {
        isCancel = false;
        float smallWidth = mPartitionWidth / mSmallPartitionCount;
        float neededMoveX;
        if (mMoveX < 0) {
            neededMoveX = (int) (mMoveX / smallWidth - 0.5f) * smallWidth;
        } else {
            neededMoveX = (int) (mMoveX / smallWidth + 0.5f) * smallWidth;
        }
        animator = new ValueAnimator().ofFloat(mMoveX, neededMoveX);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isCancel) {
                    mMoveX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCancel = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private boolean f = true;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            float off = mScroller.getFinalX() - mScroller.getCurrX();
            off = off * functionSpeed();
            if ((mMoveX <= mRightOffset) && off < 0) {
                mMoveX = mRightOffset;
            } else if ((mMoveX >= mLeftOffset) && off > 0) {
                mMoveX = mLeftOffset;
            } else {
                mMoveX += off;
                if (mScroller.isFinished()) {
                    startAnim();
                } else {
                    postInvalidate();
                    mLastX = mScroller.getFinalX();
                }
            }

        } else {
            if (isActionUp && f) {
                startAnim();
                f = false;

            }
        }
    }

    /**
     * 控制滑动速度
     *
     * @return
     */
    private float functionSpeed() {
        return 0.2f;
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000, 3000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
        } else {

        }
    }
/************以下方法备用**********/
    public void setStartValue(int mStartValue) {
        this.mStartValue = mStartValue;
    }

    public void setEndValue(int mEndValue) {
        this.mEndValue = mEndValue;
    }

    public void setPartitionValue(int mPartitionValue) {
        this.mPartitionValue = mPartitionValue;
    }

    public void setPartitionWidthInDP(float mPartitionWidth) {
        this.mPartitionWidth = Utils.convertDpToPixel(mContext, mPartitionWidth);
    }

    public void setSmallPartitionCount(int mSmallPartitionCount) {
        this.mSmallPartitionCount = mSmallPartitionCount;
    }

    public void setOriginValue(int mOriginValue) {
        this.mOriginValue = mOriginValue;
    }

    public void setOriginValueSmall(int small) {
        this.mOriginValueSmall = small;
    }

    public void notifyView() {
        mMoveX = -mOriginValueSmall * (mPartitionWidth / mSmallPartitionCount);
        mRightOffset = -1 * (mEndValue - mOriginValue) * mPartitionWidth / mPartitionValue;
        mLeftOffset = -1 * (mStartValue - mOriginValue) * mPartitionWidth / mPartitionValue;
        invalidate();
    }
}

