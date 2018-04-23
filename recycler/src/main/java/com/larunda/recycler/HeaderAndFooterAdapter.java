package com.larunda.recycler;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sddt on 18-3-28.
 */

public class HeaderAndFooterAdapter<T extends RecyclerView.Adapter> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int BASE_ITEM_TYPE_HEADER = 100000;
    private static int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    private T mRealAdapter;

    public HeaderAndFooterAdapter(Context context, T adapter) {
        mRealAdapter = adapter;
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }


    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }

    public T getmRealAdapter() {
        return mRealAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //        如果是头部
        if (isHeaderType(viewType)) {
            int headerPosition = mHeaderViews.indexOfKey(viewType);
            View headerView = mHeaderViews.valueAt(headerPosition);
            return createHeaderAndFooterViewHolder(headerView);
        }
//        如果是尾部
        if (isFooterType(viewType)) {
            int footerPosition = mFooterViews.indexOfKey(viewType);
            View footerView = mFooterViews.valueAt(footerPosition);
            return createHeaderAndFooterViewHolder(footerView);
        }
        return mRealAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFooterViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return mRealAdapter.getItemViewType(position - getHeadersCount());
    }

    private int getRealItemCount() {
        return mRealAdapter.getItemCount();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        mRealAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(BASE_ITEM_TYPE_HEADER++, view);
        int index = mHeaderViews.indexOfValue(view);
        notifyItemInserted(index);
    }

    public void addFooterView(View view) {
        mFooterViews.put(BASE_ITEM_TYPE_FOOTER++, view);
        int index = mFooterViews.indexOfValue(view) + getHeadersCount() + getRealItemCount();
        notifyItemInserted(index);
    }

    public void removeHeaderView(View view) {
        int index = mHeaderViews.indexOfValue(view);
        if (index < 0) return;
        mHeaderViews.removeAt(index);
        notifyItemRemoved(index);
    }

    public void removeFooterView(View view) {
        int index = mFooterViews.indexOfValue(view);
        if (index < 0) return;
        mFooterViews.removeAt(index);
        index = index + getHeadersCount() + getRealItemCount();
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    protected boolean isHeaderType(int viewType) {
        return mHeaderViews.indexOfKey(viewType) >= 0;
    }

    protected boolean isFooterType(int viewType) {
        return mFooterViews.indexOfKey(viewType) >= 0;
    }

    private RecyclerView.ViewHolder createHeaderAndFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view) {
        };
    }

    public int indexOfHeader(View view) {
        return mHeaderViews.indexOfValue(view);
    }


    public int indexOfFooter(View view) {
        return mFooterViews.indexOfValue(view) + getHeadersCount() + getRealItemCount();
    }

    protected boolean isHeaderPosition(int position) {
        return position < getHeadersCount();
    }

    protected boolean isFooterPosition(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    /**
     * 解决GridLayoutManager问题
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRealAdapter.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeaderPosition(position) || isFooterPosition(position))
                        return gridLayoutManager.getSpanCount();
                    return 1;
                }
            });
        }

    }

    /**
     * 解决瀑布流布局问题
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mRealAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderPosition(position) || isFooterPosition(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;
                layoutParams.setFullSpan(true);
            }
        } else {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;
                layoutParams.setFullSpan(false);
            }
        }
    }
}
