package com.yuyh.library;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 气泡布局
 */
public class BubbleRelativeLayout extends RelativeLayout {

    /**
     * 气泡尖角方向
     */
    public enum BubbleLegOrientation {
        TOP, LEFT, RIGHT, BOTTOM, NONE
    }

    public static int PADDING = 30;  //填充
    public static int TRIANG_SIDE_LENGTH = 50;  //等腰三角形的腰长
    public static int LEG_HALF_BASE = 30;    // 三角形的偏移量
    public static float STROKE_WIDTH = 8.0f;
    public static float CORNER_RADIUS = 28.0f;
    public static int SHADOW_COLOR = Color.argb(100, 0, 0, 0);
    /**/
    public static float MIN_LEG_DISTANCE = PADDING + LEG_HALF_BASE;

    private Paint mFillPaint = null;
    private final Path mPath = new Path();
    private final Path mBubbleLegPrototype = new Path();
    private final Paint mPaint = new Paint(Paint.DITHER_FLAG);

    private float mBubbleLegOffset = 0.75f;
    private BubbleLegOrientation mBubbleOrientation = BubbleLegOrientation.LEFT;

    public BubbleRelativeLayout(Context context) {
        this(context, null);
    }

    public BubbleRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {

        //setGravity(Gravity.CENTER);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.bubblePopupWindowLayout);

            try {
                PADDING = a.getDimensionPixelSize(R.styleable.bubblePopupWindowLayout_padding, PADDING);
                TRIANG_SIDE_LENGTH = a.getDimensionPixelSize(R.styleable.bubblePopupWindowLayout_triangleSideLength, TRIANG_SIDE_LENGTH);
                SHADOW_COLOR = a.getInt(R.styleable.bubblePopupWindowLayout_shadowColor, SHADOW_COLOR);
                LEG_HALF_BASE = a.getDimensionPixelSize(R.styleable.bubblePopupWindowLayout_halfBaseOfLeg, LEG_HALF_BASE);
                MIN_LEG_DISTANCE = PADDING + LEG_HALF_BASE;
                STROKE_WIDTH = a.getFloat(R.styleable.bubblePopupWindowLayout_strokeWidth, STROKE_WIDTH);
                CORNER_RADIUS = a.getFloat(R.styleable.bubblePopupWindowLayout_cornerRadius, CORNER_RADIUS);
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }

        mPaint.setColor(SHADOW_COLOR);
        mPaint.setStyle(Style.FILL);
        mPaint.setStrokeCap(Cap.BUTT);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        //注释掉，影响圆角对称
        //mPaint.setPathEffect(new CornerPathEffect(CORNER_RADIUS));

        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        }

        mFillPaint = new Paint(mPaint);
        mFillPaint.setColor(Color.WHITE);
        mFillPaint.setShader(new LinearGradient(100f, 0f, 100f, 200f, Color.WHITE, Color.WHITE, TileMode.CLAMP));

        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, mFillPaint);
        }
        /*设置阴影*/
        // mPaint.setShadowLayer(2f, 2F, 5F, SHADOW_COLOR);

        renderBubbleLegPrototype();

        setPadding(PADDING, PADDING, PADDING, PADDING);

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 尖角path
     */
    private void renderBubbleLegPrototype() {
        /**
         * 画一个等腰三角形， 因为y轴从0开始向负方向画线，看不到这条线，会被隐藏一半， 所以看到的事一个直角三角形
         *
         *
         */
        mBubbleLegPrototype.moveTo(0, 0);
        mBubbleLegPrototype.lineTo(TRIANG_SIDE_LENGTH * 1.5f, -TRIANG_SIDE_LENGTH / 1.5f); //向负方向画路径，目前会被被隐藏
        mBubbleLegPrototype.lineTo(TRIANG_SIDE_LENGTH * 1.5f, TRIANG_SIDE_LENGTH / 1.5f);
        mBubbleLegPrototype.close();//封闭路径

    }

    /**
     * 设置尖角方向和 偏移量
     *
     * @param bubbleOrientation
     * @param bubbleOffset
     */
    public void setBubbleParams(final BubbleLegOrientation bubbleOrientation, final float bubbleOffset) {
        mBubbleLegOffset = bubbleOffset;
        mBubbleOrientation = bubbleOrientation;
    }

    /**
     * 根据显示方向，获取尖角位置矩阵（主要用于处理尖角，尖角的方向）
     *
     * @param width
     * @param height
     * @return
     */
    private Matrix renderBubbleLegMatrix(final float width, final float height) {
        //偏移量
        final float offset = Math.max(mBubbleLegOffset, MIN_LEG_DISTANCE);

        float dstX = 0;
        float dstY = Math.min(offset, height - MIN_LEG_DISTANCE);
        final Matrix matrix = new Matrix();

        switch (mBubbleOrientation) {

            case TOP:
                //水平方向移动
                dstX = Math.min(offset, width - MIN_LEG_DISTANCE);
                dstY = 0;
                matrix.postRotate(90);  // 旋转90
                break;

            case RIGHT:
                dstX = width;
                dstY = Math.min(offset, height - MIN_LEG_DISTANCE);
                matrix.postRotate(180);
                break;

            case BOTTOM:
                dstX = Math.min(offset, width - MIN_LEG_DISTANCE);
                dstY = height;
                matrix.postRotate(270);
                break;
            default:
                break;
        }

        matrix.postTranslate(dstX, dstY); //移动
        return matrix;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final float width = canvas.getWidth();
        final float height = canvas.getHeight();

        mPath.rewind();
        mPath.addRoundRect(new RectF(PADDING, PADDING, width - PADDING, height - PADDING), CORNER_RADIUS, CORNER_RADIUS, Direction.CW);
        mPath.addPath(mBubbleLegPrototype, renderBubbleLegMatrix(width, height));

        //画一个灰色背景的图形
        canvas.drawPath(mPath, mPaint);

        /**
         * 缩小画布后再画一个白色背景的图形，这样就形成了一个有灰边，白色填充的不规则气泡图形。
         */
        //把画布缩小：以中心点为轴进行缩小。
        canvas.scale((width - STROKE_WIDTH) / width, (height - STROKE_WIDTH) / height, width / 2f, height / 2f);
        canvas.drawPath(mPath, mFillPaint);
    }
}
