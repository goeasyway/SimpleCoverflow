package net.goeasyway.coverflow;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by lan on 16/11/10.
 *
 * Android面试一天一题（Day 30：老外的自定义View面试题）中所提到案例的简单实现:
 * http://www.jianshu.com/p/96b9f38319c1
 */
public class CoverflowView extends ViewGroup {

    private int touchSlop;
    private float xLastDown;
    private float xLastMove;

    private int leftBorder;
    private int rightBorder;
    private int childWidth;

    private Scroller scroller;

    private Adapter adapter;
    private int selection = 0;

    private int viewWidth;


    public CoverflowView(Context context) {
        this(context, null);
    }

    public CoverflowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoverflowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        touchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        scroller = new Scroller(context);
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        reset();
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setSelection(int selection) {
        if (adapter == null) {
            throw new IllegalStateException("Error: Adapter is null.");
        }
        if (selection < 0 || selection > (adapter.getCount() - 1)) {
            throw new IllegalArgumentException("Position index must be in range of adapter values (0 - getCount()-1)");
        }
        this.selection = selection;
        reset();
    }

    public int getSelection() {
        return selection;
    }

    private void reset() {
        if (adapter == null || adapter.getCount() == 0) {
            return;
        }

        removeAllViewsInLayout();

        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View view = adapter.getView(i, null, this);
            addViewInLayout(view, 0, new LayoutParams(320, 320), true);
        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            measureChild(v, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int painterPosX = l;
        int painterPosY = t;

        int childCount = getChildCount();
        // 使用了比较简单直接的做法(当然不是最优化的做法),直接水平线性布局所有的子View
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int width = childView.getMeasuredWidth();
            int height = childView.getMeasuredHeight();

            childView.layout(painterPosX, painterPosY, painterPosX + width, painterPosY + height);
            painterPosX += width;
            childWidth = width;
        }

        leftBorder = l - viewWidth/2;
        rightBorder = painterPosX + viewWidth/2;

        scrollToSelectionCenter();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xLastDown = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                xLastMove = ev.getRawX();
                float deltaX = xLastMove - xLastDown;
                if (Math.abs(deltaX) > touchSlop) {
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float xMove = event.getRawX();
                int deltaX = (int) (xLastMove - xMove);
                if ((getScrollX() + deltaX) < leftBorder) {
                    scrollTo(leftBorder, 0);
                } else if ((getScrollX() + childWidth + deltaX) > rightBorder) {
                    scrollTo(rightBorder - childWidth, 0);
                }
                scrollBy(deltaX, 0); // 你不得不自己试一下scrollBy与scrollTo的区别,动手写一下比你去背面试题有用多了。
                xLastMove = xMove;
                updateSelection();
                break;
            case MotionEvent.ACTION_UP:
                scrollToSelectionCenter();
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 根据当间移动的位置确定被先中的Items
     */
    private void updateSelection() {
        selection = (getScrollX() + viewWidth/2 ) / childWidth;
        if (selection < 0) {
            selection = 0;
        } else if (selection > adapter.getCount() - 1) {
            selection = adapter.getCount() - 1;
        }
    }

    /**
     * 将选中的Item在ViewGroup的中央显示
     */
    private void scrollToSelectionCenter() {
        if (adapter == null || adapter.getCount() == 0) {
            return;
        }
        if (selection < 0 || selection > adapter.getCount() - 1) {
            return;
        }
        View view = getChildAt(selection);
        int dx =  (selection * childWidth +  childWidth / 2) - getScrollX() - viewWidth/2;
        scroller.startScroll(getScrollX(), 0, dx, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {

            View view = getChildAt(i);
            if (i == selection) {
                view.setScaleX(1f);
                view.setScaleY(1f);
                view.setAlpha(1f);
            } else { // 没有选中的Item缩小并变半透明
                view.setScaleX(0.75f);
                view.setScaleY(0.75f);
                view.setAlpha(0.35f);
            }
        }
    }
}
