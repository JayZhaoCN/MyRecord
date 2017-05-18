package com.hfut.zhaojiabao.myrecord;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jay on 2016/12/30.
 * pop layout
 */

//TODO Alpha and Scale
public class PopLayout extends ViewGroup implements View.OnClickListener{
    private static final int TOP_MARGIN = 100;
    private static final int TEXT_SIZE = 13;

    private Context mContext;
    //TODO 待解决
    //private int mChildCount;

    private boolean isExpanded = false;

    public static final int STYLE_POP = 0;
    public static final int STYLE_SCALE = 1;

    //default style is STYLE_POP
    private int mStyle = STYLE_SCALE;

    private SparseArray<String> mTips = new SparseArray<>();
    private SparseArray<TextView> mTipsTv = new SparseArray<>();

    private ArrayList<View> mIcons = new ArrayList<>();

    private ValueAnimator mAnimator;
    private ArrayList<Integer> mTranslatesY = new ArrayList<>();

    public PopLayout(Context context) {
        this(context, null);
    }

    public PopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs, defStyleAttr);
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.PopLayout, 0, defStyleAttr);
        mStyle = ta.getInt
                (R.styleable.PopLayout_animation_type, STYLE_POP);
        ta.recycle();
    }

    //TODO 不知道这一步操作是否可以避免ValueAnimator导致的内存泄漏
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimator != null) {
            mAnimator.removeAllUpdateListeners();
            mAnimator.end();
            mAnimator.cancel();
        }
    }

    public void initPopAnim() {

        if(mAnimator != null) {
            mAnimator.removeAllUpdateListeners();
        }

        //TODO 为什么两次添加的mTranslatesY不一样？导致展开动画不正常
        if(mTranslatesY.size() == 0) {
            View bottomView = mIcons.get(mIcons.size() - 1);
            for (int i = 0; i < mIcons.size() - 1; i++) {
                mTranslatesY.add(i, (int) (bottomView.getY() - mIcons.get(i).getY()));
            }
        }

        if(!isExpanded) {
            displayIcons();
            mAnimator = ValueAnimator.ofFloat(1, 0);
            mAnimator.setDuration(270);
            mAnimator.setInterpolator(new OvershootInterpolator());
            isExpanded = true;
        } else {
            mAnimator = ValueAnimator.ofFloat(0, 1);
            mAnimator.setDuration(270);
            mAnimator.setInterpolator(new DecelerateInterpolator(2));
            isExpanded = false;
        }

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                for(int i=0; i<mIcons.size() - 1; i++) {
                    mIcons.get(i).setTranslationY(mTranslatesY.get(i) * (float)valueAnimator.getAnimatedValue());
                    if(mTipsTv.get(i) == null) {
                        continue;
                    }
                    mTipsTv.get(i).setTranslationY(mTranslatesY.get(i) * (float)valueAnimator.getAnimatedValue());
                    mTipsTv.get(i).setAlpha(1 - (float)valueAnimator.getAnimatedValue());

                    //Log.i("JAYTEST1", "Y: " + mTipsTv.get(0).getY() + " TranslationY: " + mTipsTv.get(0).getTranslationY());
                }
            }
        });

        mAnimator.start();
    }

    private SparseIntArray mTranslatesX = new SparseIntArray();

    public void initScaleAnim() {

        if(mAnimator != null) {
            mAnimator.removeAllUpdateListeners();
        }

        if(mTranslatesX.size() == 0) {
            for(int i=0; i<mIcons.size() - 1; i++) {
                if(mTipsTv.get(i) != null) {
                    //这里也要改成getLeft()，不能用getX()
                    mTranslatesX.put(i, mIcons.get(i).getLeft() - mTipsTv.get(i).getLeft());
                }
            }
        }

        final boolean flag = isExpanded;
        if(!isExpanded) {
            displayIcons();
            mAnimator = ValueAnimator.ofFloat(0, 1);
            mAnimator.setDuration(270);
            mAnimator.setInterpolator(new OvershootInterpolator());
            isExpanded = true;
        } else {
            mAnimator = ValueAnimator.ofFloat(1, 0);
            mAnimator.setDuration(270);
            mAnimator.setInterpolator(new DecelerateInterpolator(2));
            isExpanded = false;
        }

        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                    for(View child : mIcons) {
                        child.setClickable(!flag);
                    }
                mIcons.get(mIcons.size() - 1).setClickable(true);
            }
        });

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                for(int i=0; i<mIcons.size() - 1; i++) {
                    mIcons.get(i).setScaleX((float) valueAnimator.getAnimatedValue());
                    mIcons.get(i).setScaleY((float) valueAnimator.getAnimatedValue());

                    if(flag) {
                        mIcons.get(i).setAlpha((float)valueAnimator.getAnimatedValue());
                    }

                    if(mTipsTv.get(i) != null) {
                        mTipsTv.get(i).setTranslationX(mTranslatesX.get(i)*(1 - (float)valueAnimator.getAnimatedValue()));

                        float alpha;
                        if(!flag) {
                            alpha = (float)valueAnimator.getAnimatedValue();
                        } else {
                            alpha = (float) ((float)valueAnimator.getAnimatedValue() - 0.3);
                        }
                        if(alpha < 0) {
                            alpha = 0;
                        }

                        mTipsTv.get(i).setAlpha(alpha);
                    }
                }
            }
        });
        mAnimator.start();
    }


    public void setTips(int index, String tips) {
        //TODO 到底谁动了mChildCount?
        if(index < 0 || (mTips.get(index) != null && mTips.get(index).equals(tips))) {
            Log.e("JAYTEST", "index illegal or already added.");
            return;
        }

        if(mTips.get(index) != null) {
            mTips.put(index, tips);
            mTipsTv.get(index).setText(tips);
            //这里需要及时做一次刷新，否则Tips显示位置不正确
            mTipsTv.get(index).requestLayout();
        } else {
            mTips.put(index, tips);
            TextView tipTv = new TextView(mContext);
            tipTv.setAlpha(0);
            tipTv.setText(tips);
            mTipsTv.put(index, tipTv);
            tipTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.tips_background));
            tipTv.setPadding(25, 5, 25, 5);
            tipTv.setTextColor(Color.WHITE);
            tipTv.setTextSize(TEXT_SIZE);
            addView(mTipsTv.get(index));
        }

        requestLayout();
    }

    private int getTextWidth(String str) {
        Paint paint = new Paint();
        paint.setTextSize(Utils.sp2px(mContext, TEXT_SIZE));
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.width();
    }

    private int getTextHeight(String str) {
        Paint paint = new Paint();
        paint.setTextSize(Utils.sp2px(mContext, TEXT_SIZE));
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.height();
    }

    public void hideIcons() {
        for(int i=0; i<mIcons.size(); i++) {
            mIcons.get(i).setAlpha(0);
        }

        for(int i=0; i<mTipsTv.size(); i++) {
            if(mTipsTv.get(i) != null) {
                mTipsTv.get(i).setAlpha(0);
            }
        }
    }

    public void displayIcons() {
        for(int i=0; i<mIcons.size(); i++) {
            mIcons.get(i).setAlpha(1);
        }

        for(int i=0; i<mTipsTv.size(); i++) {
            if(mTipsTv.get(i) != null) {
                mTipsTv.get(i).setAlpha(1);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("JAYTEST", "onMeasure");
        mIcons.clear();
        View childView;
        int totalHeight = 0;
        int maxWidth = 0;
        int childWidth = 0;
        PopLayoutParams lp;
        int maxIconWidth = 0;

        //这里的多次循环是不可避免的，因为onMeasure方法需要调用多次才能得到每个child的宽高
        for(int i=0; i<getChildCount(); i++) {
            if(!(getChildAt(i) instanceof TextView)) {
                mIcons.add(getChildAt(i));
                maxIconWidth = Math.max(maxIconWidth, getChildAt(i).getMeasuredWidth());
            }
        }

        for(int i=0; i<mIcons.size(); i++) {

            if(i == mIcons.size() - 1 && !mIcons.get(i).hasOnClickListeners()) {
                mIcons.get(i).setId(R.id.bottom_view);
                mIcons.get(i).setOnClickListener(this);
            }
            childView = mIcons.get(i);
            totalHeight += childView.getHeight();
            lp = (PopLayoutParams) childView.getLayoutParams();

            childWidth += childView.getWidth() + lp.getMarginStart() + lp.getMarginEnd();
            String tip = mTips.get(i);
            if(tip != null) {
                childWidth = getTextWidth(tip) + maxIconWidth + 150;

            }
            maxWidth = Math.max(childWidth, maxWidth);

            if(i == 0) {
                totalHeight += lp.topMargin;
            }
            totalHeight += lp.bottomMargin;
            childWidth = 0;
        }
        totalHeight += TOP_MARGIN;
        int width = View.resolveSize(maxWidth, widthMeasureSpec);
        int height = View.resolveSize(totalHeight, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int getIconMaxWidth() {
        int maxWidth = 0;
        for(View view : mIcons) {
            maxWidth = Math.max(view.getWidth(), maxWidth);
        }
        return maxWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View childView;
        int topLocation = TOP_MARGIN;
        PopLayoutParams lp;
        int left;
        for(int i=0; i<getChildCount(); i++) {
            childView = getChildAt(i);
            if(childView instanceof TextView) {
               continue;
            }
            lp = (PopLayoutParams) childView.getLayoutParams();
            topLocation += lp.topMargin;

            if(mTipsTv.size() == 0) {
                left = (getWidth() - childView.getMeasuredWidth()) / 2;
            } else {
                left = getWidth() - childView.getWidth() - (getIconMaxWidth() - childView.getWidth())/2;
            }
            childView.layout(left, topLocation,
                    left + childView.getMeasuredWidth(), topLocation + childView.getMeasuredHeight());
            topLocation += (childView.getMeasuredHeight() + lp.bottomMargin);
        }

        if(mTipsTv.size() != 0) {
            int minIconX = Integer.MAX_VALUE;
            for(int i=0; i<mIcons.size(); i++) {
                minIconX = Math.min(minIconX, mIcons.get(i).getLeft());
            }
            for (int i = 0; i < mIcons.size(); i++) {
                TextView tips = mTipsTv.get(i);
                if(tips == null)
                    continue;
                // 要理解决定View位置的几个因素: x = left + translationX, y = top + translationY
                //所以这里不能用mIcons.get(i).getY()，而应该用mIcons.get(i).getTop()
                int top = (mIcons.get(i).getMeasuredHeight() - tips.getMeasuredHeight()) / 2;
                tips.layout(minIconX - tips.getMeasuredWidth() - 10, top + mIcons.get(i).getTop(),
                        minIconX - 10, top + mIcons.get(i).getTop() + tips.getMeasuredHeight());
            }
        }

    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new PopLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new PopLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PopLayoutParams(getContext(), attrs);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bottom_view:
                if(mListener != null) {
                    if(!isExpanded) {
                        mListener.onExpand();
                    } else {
                        mListener.onCollapse();
                    }
                }
                if(mStyle == STYLE_POP) {
                    initPopAnim();
                } else {
                    initScaleAnim();
                }
                break;
            default:
                break;
        }
    }

    private static class PopLayoutParams extends MarginLayoutParams {

        PopLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        PopLayoutParams(int width, int height) {
            super(width, height);
        }

        PopLayoutParams(LayoutParams source) {
            super(source);
        }
    }

    private OnExpandListener mListener;

    public void setOnExpandListener(OnExpandListener listener) {
        this.mListener = listener;
    }

    public interface OnExpandListener {
        void onExpand();
        void onCollapse();
    }
}
