package com.example.movieproject.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(private val layoutManager: RecyclerView.LayoutManager) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        var firstVisibleItemPosition = 0
        when (layoutManager) {
            is GridLayoutManager -> firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            is LinearLayoutManager -> firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        }
        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                firstVisibleItemPosition >= 0
            ) {
                isLoading = true
                loadMoreItems()
            }
        }
    }
    protected abstract fun loadMoreItems()
    abstract val isLastPage: Boolean
    abstract var isLoading: Boolean
}
