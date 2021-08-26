package com.pekyurek.emircan.presentation.ui.main.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.pekyurek.emircan.R
import com.pekyurek.emircan.data.repository.Person
import com.pekyurek.emircan.databinding.ItemPersonBinding
import com.pekyurek.emircan.databinding.ItemRetryBinding
import com.pekyurek.emircan.extension.inflateDataBinding
import com.pekyurek.emircan.extension.setOnSingleClickListener
import com.pekyurek.emircan.pagination.PaginationRecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.IllegalStateException

class PersonAdapter(private val onRetryClicked: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = mutableListOf<Person>()
    private var attachedRecyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
    }

    var retryButtonVisibility = false
        set(value) {
            if (field == value) return
            field = value
            if (field) {
                notifyItemInserted(itemCount)
            } else {
                notifyItemRemoved(itemCount)
            }
        }

    var retryButtonClickable = true

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> holder.bind(list[position])
            is RetryViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            retryButtonVisibility && position == itemCount - 1 -> VIEW_TYPE_RETRY_BUTTON
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> ItemViewHolder(parent.inflateDataBinding(R.layout.item_person))
            VIEW_TYPE_RETRY_BUTTON -> RetryViewHolder(parent.inflateDataBinding(R.layout.item_retry))
            else -> throw IllegalStateException("wrong view type")
        }
    }

    override fun getItemCount(): Int = if (retryButtonVisibility) list.size + 1 else list.size

    @ExperimentalCoroutinesApi
    fun addData(person: List<Person>) {
        if (person.isEmpty()) {
            setPaginationRecyclerViewError(R.string.empty_list)
            return
        }
        val oldSize = list.size
        val distinctList = (list + person).distinctBy { it.id }
        val newSize = distinctList.size
        if (oldSize == newSize) {
            setPaginationRecyclerViewError(R.string.no_different_items_try_again)
        } else if (newSize > oldSize) {
            insertNewData(distinctList.subList(oldSize, newSize), oldSize)
        }
    }

    private fun insertNewData(person: List<Person>, notifyInsertIndex: Int) {
        retryButtonVisibility = false
        list.addAll(person)
        notifyItemInserted(notifyInsertIndex)
    }

    @ExperimentalCoroutinesApi
    private fun setPaginationRecyclerViewError(@StringRes errorTextResId: Int) {
        (attachedRecyclerView as? PaginationRecyclerView)?.setError(errorTextResId)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        retryButtonVisibility = false
        list.clear()
        notifyDataSetChanged()
    }

    class ItemViewHolder(private val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.person = person
            binding.executePendingBindings()
        }
    }

    inner class RetryViewHolder(private val binding: ItemRetryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.btnError.setOnSingleClickListener {
                if (!retryButtonClickable) return@setOnSingleClickListener
                onRetryClicked.invoke()
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_RETRY_BUTTON = 1
    }
}