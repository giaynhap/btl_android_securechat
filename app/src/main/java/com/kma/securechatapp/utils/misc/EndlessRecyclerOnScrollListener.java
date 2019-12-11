package com.kma.securechatapp.utils.misc;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract  class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static String LOG_TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private final int VISIBLE_THRESHOLD = 5;

    private int mPreviousTotal = 0;
    private boolean mLoading = true;
    private int mCurrentPage = 0;
    private LinearLayoutManager mLinearLayoutManager;


    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }

        if (totalItemCount>0)
        if (!mLoading && (totalItemCount - firstVisibleItem - visibleItemCount)
                <= VISIBLE_THRESHOLD) {
            System.out.println( mCurrentPage );
            onLoadMore(++mCurrentPage);

            mLoading = true;
        }
    }

    public  void setLoaded(){
        mLoading = false;
    }
    public void reset() {
        mCurrentPage = 0;
        mPreviousTotal = 0;
        mLoading = true;
    }

    public abstract void onLoadMore(int currentPage);
}