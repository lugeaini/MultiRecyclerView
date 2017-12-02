package cn.chenxulu.lib.widget.recycler;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author xulu
 */
public class RefreshHeaderView extends LinearLayout {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_RELEASE_TO_REFRESH = 1;
    public static final int STATE_REFRESHING = 2;
    public static final int STATE_DONE = 3;

    private LinearLayout mContainer;
    private ImageView mArrowImageView;
    private ImageView mProgressBar;

    private int mState = STATE_NORMAL;

    public int mMeasuredHeight;

    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.pull_recyclerview_header_view, null);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        setLayoutParams(layoutParams);

        setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.listview_header_arrow);
        mProgressBar = (ImageView) findViewById(R.id.listview_header_progressbar);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
        System.out.println("mMeasuredHeight:" + mMeasuredHeight);
    }

    public void setState(int state) {
        if (state == mState) {
            return;
        }
        switch (state) {
            case STATE_NORMAL:
                mArrowImageView.setVisibility(View.VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setImageResource(R.drawable.top_loading1);

                hideLoading();
                break;
            case STATE_RELEASE_TO_REFRESH:
                mArrowImageView.setVisibility(View.VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setImageResource(R.drawable.top_loading2);

                hideLoading();
                break;
            case STATE_REFRESHING:
                // 显示进度
                showRefreshAnimation();

                mProgressBar.setVisibility(View.VISIBLE);
                smoothScrollTo(mMeasuredHeight);
                break;
            case STATE_DONE:
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.INVISIBLE);

                hideLoading();
                break;
            default:
                break;
        }
        mState = state;
    }

    public int getState() {
        return mState;
    }

    public void refreshComplete() {
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
            }
        }, 200);
    }

    public void setVisibleHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LayoutParams layoutParams = (LayoutParams) mContainer.getLayoutParams();
        layoutParams.height = height;
        mContainer.setLayoutParams(layoutParams);
    }

    public int getVisibleHeight() {
        LayoutParams layoutParams = (LayoutParams) mContainer.getLayoutParams();
        return layoutParams.height;
    }

    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) {
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        // not visible.
        if (height == 0) {
            isOnRefresh = false;
        }
        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        if (mState != STATE_REFRESHING) {
            smoothScrollTo(0);
        }

        if (mState == STATE_REFRESHING) {
            int destHeight = mMeasuredHeight;
            smoothScrollTo(destHeight);
        }

        return isOnRefresh;
    }

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    private void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setImageResource(R.drawable.pull_recyclerview_header_loading_icon);
        Animation second = AnimationUtils.loadAnimation(getContext(), R.anim.xrecyclerview_anim_rotate);
        mProgressBar.startAnimation(second);
    }

    private void hideLoading() {
        mProgressBar.clearAnimation();
        mProgressBar.setImageDrawable(null);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showRefreshAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mArrowImageView.clearAnimation();
                // for view height
                mArrowImageView.setVisibility(View.INVISIBLE);
                // 此处调用第二个动画播放方法
                showLoading();
            }
        };
        AnimationDrawable animFirst = (AnimationDrawable) getResources().getDrawable(R.drawable.pull_recyclerview_header_loading_anim);
        mArrowImageView.setImageDrawable(animFirst);
        if (animFirst != null) {
            // 已知帧间隔时间相同
            int duration = animFirst.getDuration(0) * animFirst.getNumberOfFrames();
            Handler handler = new Handler();
            handler.postDelayed(runnable, duration);

            animFirst.start();
        }
        mArrowImageView.setVisibility(View.VISIBLE);
    }

}