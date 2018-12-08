package com.znt.vodbox.view.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * RecyclerView滑动监听
 * Created by yangle on 2017/10/12.
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    // 用来标记是否正在向上滑动
    private boolean isSlidingUpward = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        int lastPosition = 0;
        //当前状态为停止滑动状态SCROLL_STATE_IDLE时
        if(newState == RecyclerView.SCROLL_STATE_IDLE){
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if(layoutManager instanceof GridLayoutManager){
                //通过LayoutManager找到当前显示的最后的item的position
                lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            }else if(layoutManager instanceof LinearLayoutManager){
                lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }else if(layoutManager instanceof StaggeredGridLayoutManager){
                //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
                lastPosition = findMax(lastPositions);
            }

            //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
            //如果相等则说明已经滑动到最后了
            if(lastPosition == recyclerView.getLayoutManager().getItemCount()-1){
                onLoadMore();
            }
        }

        // 当不滑动时
        /*if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            // 获取最后一个完全显示的itemPosition
            //int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int lastItemPosition = manager.findLastCompletelyVisibleItemPositions();
            int itemCount = manager.getItemCount();

            // 判断是否滑动到了最后一个item，并且是向上滑动
            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                // 加载更多
                onLoadMore();
            }
        }*/
    }

    //找到数组中的最大值
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        isSlidingUpward = dy > 0;
    }

    /**
     * 加载更多回调
     */
    public abstract void onLoadMore();
}
