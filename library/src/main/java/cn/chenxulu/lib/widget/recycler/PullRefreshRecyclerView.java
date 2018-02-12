package cn.chenxulu.lib.widget.recycler;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.chenxulu.lib.widget.recycler.utils.MathUtil;

/**
 * @author xulu
 */
public class PullRefreshRecyclerView extends RecyclerView {
    private static final int TYPE_REFRESH_HEADER = 10000;
    private static final int TYPE_LOAD_MORE_FOOTER = 10001;
    private static final int HEADER_INIT_INDEX = 10002;

    private static final float DRAG_RATE = 3;

    private boolean isLoadingData = false;

    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private WrapAdapter mWrapAdapter;
    private float mLastY = -1;

    private boolean pullRefreshEnabled = true;
    private boolean loadMoreEnabled = false;

    /**
     * 每个header必须有不同的type,不然滚动的时候顺序会变化
     */
    private List<Integer> sHeaderTypes = new ArrayList<>();

    /**
     * adapter没有数据的时候显示,类似于listView的emptyView
     */
    private View mEmptyView;

    private RefreshHeaderView mRefreshHeaderView;
    private View mFootView;

    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();

    public PullRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mRefreshHeaderView = new RefreshHeaderView(getContext());

        mFootView = View.inflate(getContext(), R.layout.pull_recyclerview_footer_view, null);
        mFootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void addHeaderView(View view) {
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 根据header的ViewType判断是哪个header
     */
    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        return mHeaderViews.get(itemType - HEADER_INIT_INDEX);
    }

    /**
     * 判断一个type是否为HeaderType
     */
    private boolean isHeaderType(int itemViewType) {
        return mHeaderViews.size() > 0 && sHeaderTypes.contains(itemViewType);
    }

    /**
     * 判断是否是XRecyclerView保留的itemViewType
     */
    private boolean isReservedItemViewType(int type) {
        return type == TYPE_REFRESH_HEADER || type == TYPE_LOAD_MORE_FOOTER || sHeaderTypes.contains(type);
    }

    public boolean isHeaderOrFooter(int position) {
        if (mWrapAdapter != null) {
            return mWrapAdapter.isHeader(position) || mWrapAdapter.isFooter(position) || mWrapAdapter.isRefreshHeader(position);
        }
        return false;
    }

    public void refresh() {
        if (pullRefreshEnabled && mLoadingListener != null) {
            mRefreshHeaderView.setState(RefreshHeaderView.STATE_REFRESHING);
            mLoadingListener.onRefresh();
        }
    }

    public void refreshComplete() {
        mRefreshHeaderView.refreshComplete();
        isLoadingData = false;
        loadMoreEnabled = false;
    }

    public void loadMoreComplete() {
        isLoadingData = false;
        loadMoreEnabled = false;
    }

    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
    }

    public void setLoadMoreEnabled(boolean enabled) {
        loadMoreEnabled = enabled;
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);

        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    /**
     * 避免用户自己调用getAdapter() 引起的ClassCastException
     */
    @Override
    public Adapter getAdapter() {
        if (mWrapAdapter != null) {
            return mWrapAdapter.getOriginalAdapter();
        }
        return null;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if(mWrapAdapter != null){
            if (layout instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) layout);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (mWrapAdapter.isHeader(position) || mWrapAdapter.isFooter(position) || mWrapAdapter.isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state != RecyclerView.SCROLL_STATE_IDLE || mLoadingListener == null) {
            return;
        }

        if (isLoadingData || !loadMoreEnabled) {
            return;
        }

        LayoutManager mLayoutManager = getLayoutManager();
        int firstVisibleItemPosition;
        int lastVisibleItemPosition;
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mLayoutManager;
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findFirstVisibleItemPositions(into);
            firstVisibleItemPosition = MathUtil.findMin(into);

            layoutManager.findLastVisibleItemPositions(into);
            lastVisibleItemPosition = MathUtil.findMax(into);
        } else {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mLayoutManager;
            firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        }

        System.out.println("firstVisibleItemPosition:" + firstVisibleItemPosition);
        System.out.println("lastVisibleItemPosition:" + lastVisibleItemPosition);
        System.out.println("count:" + mWrapAdapter.getItemCount());

        if (lastVisibleItemPosition < mWrapAdapter.getItemCount() - 1) {
            return;
        }

        if (mRefreshHeaderView.getState() < RefreshHeaderView.STATE_REFRESHING) {
            isLoadingData = true;
            mLoadingListener.onLoadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!pullRefreshEnabled) {
            return super.onTouchEvent(ev);
        }
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop()) {
                    mRefreshHeaderView.onMove(deltaY / DRAG_RATE);
                    if (mRefreshHeaderView.getVisibleHeight() > 0 && mRefreshHeaderView.getState() < RefreshHeaderView.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                // reset
                mLastY = -1;
                if (isOnTop()) {
                    if (mRefreshHeaderView.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener.onRefresh();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean isOnTop() {
        return mRefreshHeaderView.getParent() != null;
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
            //empty_view 显示与隐藏
            if (mWrapAdapter != null && mEmptyView != null) {
                if (mWrapAdapter.adapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter adapter;

        public WrapAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        public RecyclerView.Adapter getOriginalAdapter() {
            return this.adapter;
        }

        public boolean isHeader(int position) {
            return position >= 1 && position < mHeaderViews.size() + 1;
        }

        public boolean isFooter(int position) {
            if (loadMoreEnabled) {
                return position == getItemCount() - 1;
            } else {
                return false;
            }
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mRefreshHeaderView);
            } else if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (viewType == TYPE_LOAD_MORE_FOOTER) {
                return new SimpleViewHolder(mFootView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SimpleViewHolder) {
                return;
            }
            int realPosition = position - (getHeadersCount() + 1);
            if (realPosition < adapter.getItemCount()) {
                adapter.onBindViewHolder(holder, realPosition);
            }
        }

        @Override
        public int getItemCount() {
            int count = 1 + getHeadersCount() + adapter.getItemCount();
            if (loadMoreEnabled) {
                count++;
            }
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                position = position - 1;
                return sHeaderTypes.get(position);
            }
            if (isFooter(position)) {
                return TYPE_LOAD_MORE_FOOTER;
            }

            int realPosition = position - (getHeadersCount() + 1);
            if (realPosition < adapter.getItemCount()) {
                int type = adapter.getItemViewType(realPosition);
                if (isReservedItemViewType(type)) {
                    throw new IllegalStateException("XRecyclerView require itemViewType in adapter should be less than 10000");
                }
                return type;
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (position >= getHeadersCount() + 1) {
                int adjPosition = position - (getHeadersCount() + 1);
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position) || isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }

            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                if (isHeader(holder.getLayoutPosition()) || isRefreshHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition())) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                    p.setFullSpan(true);
                }
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private LoadingListener mLoadingListener;

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {
        /**
         * 下拉刷新
         */
        void onRefresh();

        /**
         * 加载更多
         */
        void onLoadMore();
    }

}