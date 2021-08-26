package com.pekyurek.emircan.presentation.util

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("setHasFixedSize")
fun setHasFixedSize(recyclerView: RecyclerView, value: Boolean) {
    recyclerView.setHasFixedSize(value)
}

@BindingAdapter("isVisible")
fun isVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}
