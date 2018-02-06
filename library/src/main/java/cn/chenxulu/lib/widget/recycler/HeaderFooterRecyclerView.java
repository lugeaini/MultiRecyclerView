package cn.chenxulu.lib.widget.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xulu
 */
public class HeaderFooterRecyclerView extends RecyclerView {
    private static final int HEADER_INIT_INDEX = 20000;
    private static final int FOOTER_INIT_INDEX = 30000;

    private List<View> mHeaderViews = new ArrayList<>();
    private List<View> mFooterViews = new ArrayList<>();
    private WrapAdapter mWrapAdapter;

    /**
     * 每个header\footer必须有不同的type,不然滚动的时候顺序会变化
     */
    private List<Integer> mHeaderTypes = new ArrayList<>();
    private List<Integer> mFooterTypes = new ArrayList<>();

    /**
     * adapter没有数据的时候显示,类似于listView的emptyView
     */
    private View mEmptyView;

    private final AdapterDataObserver mDataObserver = new DataObserver();

    public HeaderFooterRecyclerView(Context context) {
        this(context, null);
    }

    public HeaderFooterRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderFooterRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View view) {
        mHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void clearHeaderViews(){
        mHeaderViews.clear();
        mHeaderTypes.clear();
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void addFooterView(View view) {
        mFooterTypes.add(FOOTER_INIT_INDEX + mFooterViews.size());
        mFooterViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void clearFooterViews(){
        mFooterViews.clear();
        mFooterTypes.clear();
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 根据header的ViewType判断是哪个header
     */
    private View getHeaderViewByType(int itemType) {
        return mHeaderViews.get(itemType - HEADER_INIT_INDEX);
    }

    /**
     * 根据footer的ViewType判断是哪个footer
     */
    private View getFooterViewByType(int itemType) {
        return mFooterViews.get(itemType - FOOTER_INIT_INDEX);
    }

    /**
     * 判断一个type是否为HeaderType
     */
    private boolean isHeaderType(int itemViewType) {
        return mHeaderViews.size() > 0 && mHeaderTypes.contains(itemViewType);
    }

    /**
     * 判断一个type是否为FooterType
     */
    private boolean isFooterType(int itemViewType) {
        return mFooterViews.size() > 0 && mFooterTypes.contains(itemViewType);
    }

    /**
     * 判断是否是RecyclerView保留的itemViewType
     */
    private boolean isReservedItemViewType(int type) {
        return mFooterTypes.contains(type) || mHeaderTypes.contains(type);
    }

    public boolean isHeaderOrFooter(int position) {
        if (mWrapAdapter != null) {
            return mWrapAdapter.isHeader(position) || mWrapAdapter.isFooter(position);
        }
        return false;
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

    private class DataObserver extends AdapterDataObserver {
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

    private class WrapAdapter extends Adapter<ViewHolder> {

        private Adapter adapter;

        public WrapAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        public Adapter getOriginalAdapter() {
            return this.adapter;
        }

        public boolean isHeader(int position) {
            return mHeaderViews.size() > 0 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            return mFooterViews.size() > 0 && position > mHeaderViews.size() + adapter.getItemCount() - 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (isFooterType(viewType)) {
                return new SimpleViewHolder(getFooterViewByType(viewType));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position) || isFooter(position)) {
                return;
            }
            int realPosition = position - mHeaderViews.size();
            if (realPosition < adapter.getItemCount()) {
                adapter.onBindViewHolder(holder, realPosition);
            }
        }

        @Override
        public int getItemCount() {
            return mHeaderViews.size() + adapter.getItemCount() + mFooterViews.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeader(position)) {
                return mHeaderTypes.get(position);
            }
            if (isFooter(position)) {
                return mFooterTypes.get(position - mHeaderViews.size() - adapter.getItemCount());
            }

            int realPosition = position - mHeaderViews.size();
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
            if (position > mHeaderViews.size() - 1) {
                int realPosition = position - mHeaderViews.size();
                if (realPosition < adapter.getItemCount()) {
                    return adapter.getItemId(realPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                if (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition())) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                    p.setFullSpan(true);
                }
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
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

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

}