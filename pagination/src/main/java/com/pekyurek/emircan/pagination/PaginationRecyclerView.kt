package com.pekyurek.emircan.pagination


import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.core.content.res.getIntOrThrow
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PaginationRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RecyclerView(context, attrs, defStyle) {

    private lateinit var recyclerPaginationScrollListener: RecyclerPaginationScrollListener

    val paginationFlow = MutableSharedFlow<PaginationStatus>()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PaginationRecyclerView).run {
            addPaginationScrollListener(getStartIndex(this), getPaginationOffset(this))
            recycle()
        }
    }

    private fun getStartIndex(typedArray: TypedArray): Int? {
        return try {
            typedArray.getIntOrThrow(R.styleable.PaginationRecyclerView_startPageIndex).let {
                if (it < 0) null else it
            }
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun getPaginationOffset(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.PaginationRecyclerView_paginationOffset, 0).let {
            if (it < 0) 0 else it
        }
    }

    private fun addPaginationScrollListener(startPageIndex: Int?, paginationOffset: Int) {
        recyclerPaginationScrollListener = object : RecyclerPaginationScrollListener(startPageIndex, paginationOffset) {
            override fun onLoadMore(page: Int?) {
                super.onLoadMore(page)
                loadMore(page)
            }

            override fun onReset() {
                uiScope.launch { paginationFlow.emit(PaginationStatus.Reset) }
            }
        }
        addOnScrollListener(recyclerPaginationScrollListener)
    }

    private fun loadMore(page: Int?) = uiScope.launch {
        paginationFlow.emit(PaginationStatus.Success(page))
    }

    fun setError(errorDescription: String) {
        errorHandler(errorDescription)
    }

    fun setError(@StringRes errorDescriptionId: Int) {
        errorHandler(context.getString(errorDescriptionId))
    }

    private fun errorHandler(errorDescription: String) = uiScope.launch {
        recyclerPaginationScrollListener.loadError = true
        paginationFlow.emit(PaginationStatus.Error(errorDescription))
    }

    fun resetPagination() {
        recyclerPaginationScrollListener.resetPagination()
    }

    fun reloadLastPage() {
        recyclerPaginationScrollListener.reloadLastPage()
    }
}