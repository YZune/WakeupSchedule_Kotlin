package com.suda.yzune.wakeupschedule.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

public class LoginButton extends View {

    int width;
    int height;

    int resetWidth;
    int resetHeight;

    float circleAngle; //圆形矩形的角度大小
    int default_move_distance; //默认需要移动的距离
    int actual_move_distance; //实际需要移动的距离
    String btnString = "登录";
    int rect_to_angle_duration = 000;
    int rect_to_circle_duration = 300;
    int change_text_duration = 3000;
    private Paint paint;
    private Paint textPaint;
    private Paint smilePaint;
    boolean isCircle = false; //是否开始画圆弧
    boolean isEye = false; //是否开始画眼睛
    boolean isBegin = false;
    int startAngle;
    int point_move_up_distance;

    RectF rectf = new RectF();
    RectF textRect = new RectF(); //放文字的矩形
    RectF arcRect = new RectF(); //笑脸弧度

    AnimatorSet animatorSet = new AnimatorSet();
    AnimatorSet animatorSet_reverse = new AnimatorSet();
    ValueAnimator animator_rect_to_angle;
    ValueAnimator animator_rect_to_central_circle;
    ValueAnimator animator_central_circle_to_rect;
    ValueAnimator animator_arc_to_rotate;
    ValueAnimator animator_point_move_up;

    OnAnimationButtonClickListener onAnimationButtonClickListener;

    public boolean isBegin() {
        return isBegin;
    }

    public LoginButton(Context context) {
        super(context);
//        initPaint();
    }

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAnimationButtonClickListener != null) {
                    onAnimationButtonClickListener.onAnimationStart();
                }
            }
        });

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (onAnimationButtonClickListener != null) {
                    onAnimationButtonClickListener.onAnimationFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (onAnimationButtonClickListener != null) {
                    onAnimationButtonClickListener.onAnimationCancel();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public LoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        initPaint();
    }

    public void setAnimationButtonListener(OnAnimationButtonClickListener onAnimationButtonClickListener) {
        this.onAnimationButtonClickListener = onAnimationButtonClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = resetWidth = w;
        height = resetHeight = h;

        default_move_distance = (w - h) / 2;

        initAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw_oval_to_circle(canvas);
        drawText(canvas);

        if (isCircle) {
            draw_arc_to_smile(canvas);
        }
        if (isEye) {
            draw_point_move_up(canvas);
        }

    }


    private void initPaint() {

        LinearGradient linearGradient = new LinearGradient(
                0, 0,
                480, 480,
                new int[]{Color.parseColor("#fd807f"), Color.parseColor("#eb3161")}, null,
                Shader.TileMode.MIRROR
        );

        paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //paint.setColor(bg_color);
        paint.setShader(linearGradient);
        //paint.setShadowLayer(100, 50, 50, Color.argb(128, 249, 94, 94));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(48);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        smilePaint = new Paint();
        smilePaint.setStyle(Paint.Style.STROKE);
        smilePaint.setColor(Color.WHITE);
        smilePaint.setStrokeWidth(10);
        smilePaint.setAntiAlias(false);


    }

    private void initAnimation() {
        setAnimator_rect_to_angle();
        setAnimator_rect_to_central_circle();
        setAnimator_central_circle_to_rect();
        setAnimator_arc_to_rotate();
        //setAnimator_point_move_up();

        animatorSet
                .play(animator_rect_to_central_circle)
                .before(animator_arc_to_rotate)
                //.before(animator_point_move_up)
                .after(animator_rect_to_angle);

        animatorSet_reverse
                .play(animator_central_circle_to_rect);
    }

    /**
     * 设置矩形变成弧度矩形的动画
     */
    private void setAnimator_rect_to_angle() {
        animator_rect_to_angle = ValueAnimator.ofFloat(height / 2, height / 2);
        animator_rect_to_angle.setDuration(rect_to_angle_duration);
        animator_rect_to_angle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                circleAngle = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
    }


    /**
     * 设置弧度矩形到终点的动画
     */
    private void setAnimator_rect_to_central_circle() {
        animator_rect_to_central_circle = ValueAnimator.ofInt(0, default_move_distance);
        animator_rect_to_central_circle.setDuration(rect_to_circle_duration);
        animator_rect_to_central_circle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                actual_move_distance = (int) valueAnimator.getAnimatedValue();

                int alpha = 255 - (actual_move_distance * 255) / default_move_distance;

                textPaint.setAlpha(alpha);

                if (actual_move_distance >= default_move_distance) {
                    isCircle = true;
                }

                invalidate();
            }
        });
    }

    /**
     * 设置反转
     */
    private void setAnimator_central_circle_to_rect() {
        animator_central_circle_to_rect = ValueAnimator.ofInt(default_move_distance, 0);
        animator_central_circle_to_rect.setDuration(rect_to_circle_duration);
        animator_central_circle_to_rect.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                actual_move_distance = (int) valueAnimator.getAnimatedValue();

                int alpha = (default_move_distance * 255) / default_move_distance;

                textPaint.setAlpha(alpha);

                if (actual_move_distance < default_move_distance) {
                    isCircle = false;
                }

                invalidate();
            }
        });
    }

    /**
     * 加载旋转动画
     */
    private void setAnimator_arc_to_rotate() {
        animator_arc_to_rotate = ValueAnimator.ofInt(0, 360);
        animator_arc_to_rotate.setDuration(1000);
        animator_arc_to_rotate.setRepeatCount(ValueAnimator.INFINITE);
        animator_arc_to_rotate.setInterpolator(new BounceInterpolator());
        //animator_arc_to_rotate.setInterpolator(new LinearInterpolator());
        animator_arc_to_rotate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                startAngle = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
    }

    private void setAnimator_point_move_up() {
        animator_point_move_up = ValueAnimator.ofInt(0, 20);
        animator_point_move_up.setDuration(1500);
        animator_point_move_up.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                point_move_up_distance = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
    }

    /**
     * 绘制长方形变成圆形
     *
     * @param canvas 画布
     */
    private void draw_oval_to_circle(Canvas canvas) {

        rectf.left = actual_move_distance;
        rectf.top = 0;
        rectf.right = width - actual_move_distance;
        rectf.bottom = height;

        //画圆角矩形
        canvas.drawRoundRect(rectf, height / 2, height / 2, paint);

    }

    /**
     * 圆弧旋转最后转为下巴
     *
     * @param canvas
     */
    private void draw_arc_to_smile(Canvas canvas) {

        arcRect.left = width / 2 - height / 4;
        arcRect.right = width / 2 + height / 4;
        arcRect.top = height / 4;
        arcRect.bottom = height * 3 / 4;

        canvas.drawArc(arcRect, startAngle, 180, false, smilePaint);
    }

    /**
     * 笑脸眼睛上移
     *
     * @param canvas
     */
    private void draw_point_move_up(Canvas canvas) {
        int pointLeftX = width / 2 - height / 4;
        int pointRightX = width / 2 + height / 4;
        canvas.drawPoint(pointLeftX + 10, height / 2 - point_move_up_distance, smilePaint);
        canvas.drawPoint(pointRightX - 10, height / 2 - point_move_up_distance, smilePaint);
    }


    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawText(Canvas canvas) {
        textRect.left = 0;
        textRect.top = 0;
        textRect.right = width;
        textRect.bottom = height;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (int) ((textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2);
        //文字绘制到整个布局的中心位置
        canvas.drawText(btnString, textRect.centerX(), baseline, textPaint);
    }

    /**
     * 开启动画
     */
    public void start() {
        isBegin = true;
        animatorSet.start();
    }

    /**
     * 取消动画
     */
    public void reset() {
        animatorSet.end();
        isBegin = false;
        resetWH();
    }

    public void setBtnString(String str){
        btnString = str;
        invalidate();
    }

    public void resetWH() {
        animatorSet_reverse.start();
        isCircle = false;
        isEye = false;
        isBegin = false;
        invalidate();
//        width = resetWidth;
//        height = resetHeight;
//        actual_move_distance = 0;
//        circleAngle = 0;
//        textPaint.setAlpha(255);
//        invalidate();

    }


    public interface OnAnimationButtonClickListener {

        void onAnimationStart(); //动画开始

        void onAnimationFinish(); //动画完成

        void onAnimationCancel(); //动画取消
    }
}
