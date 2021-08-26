package com.pekyurek.emircan.pagination


import androidx.annotation.CallSuper
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pekyurek.emircan.pagination.extension.orZero
import java.lang.IllegalStateException

abstract class RecyclerPaginationScrollListener(private val startPageIndex: Int? = null) : RecyclerView.OnScrollListener() {

    private var lastVisibleItemPosition: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var previousTotalItemCount: Int = 0

    private var currentPage = startPageIndex

    private var loading = true

    var loadError : Boolean = false

    @CallSuper
    open fun onLoadMore(page: Int?) {
        loadError = false
    }

    abstract fun onReset()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (loadError) return
        val layoutManager = recyclerView.layoutManager ?: return
        visibleItemCount = layoutManager.childCount
        totalItemCount = layoutManager.itemCount

        lastVisibleItemPosition = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is StaggeredGridLayoutManager -> getLastVisibleItem(layoutManager.findLastVisibleItemPositions(null))
            else -> throw IllegalStateException("wrong layout manager")
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && ((visibleItemCount + lastVisibleItemPosition) >= totalItemCount)) {
            currentPage = currentPage.orZero() + 1
            onLoadMore(currentPage)
            loading = true
        }
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            maxSize = if (i == 0 || lastVisibleItemPositions[i] > maxSize) {
                lastVisibleItemPositions[i]
            } else continue
        }
        return maxSize
    }

    fun resetPagination() {
        currentPage = startPageIndex
        previousTotalItemCount = 0
        loading = true
        loadError = false
        onReset()
    }

    fun reloadLastPage() {
        onLoadMore(currentPage)
    }
}