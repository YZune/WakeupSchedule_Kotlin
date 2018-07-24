package com.suda.yzune.wakeupschedule.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class JudgeNestedScrollView extends NestedScrollView {

    private boolean isNeedScroll = true;

    public JudgeNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public JudgeNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JudgeNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                return isNeedScroll;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /*
    改方法用来处理NestedScrollView是否拦截滑动事件
     */
    public void setNeedScroll(boolean isNeedScroll) {
        this.isNeedScroll = isNeedScroll;
    }
}
