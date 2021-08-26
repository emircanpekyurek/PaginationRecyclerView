package com.pekyurek.emircan.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


fun <T : ViewDataBinding> ViewGroup.inflateDataBinding(
    @LayoutRes layoutResId: Int,
    attachToParent: Boolean = false,
): T {
    return DataBindingUtil.inflate(LayoutInflater.from(context),
        layoutResId,
        this,
        attachToParent)
}


inline fun View.setOnSingleClickListener(throttleTime: Long = 600L, crossinline action: (v: View) -> Unit) {
    setOnClickListener(getOnSingleClickListener(throttleTime, action))
}

inline fun getOnSingleClickListener(throttleTime: Long = 600L, crossinline action: (v: View) -> Unit): View.OnClickListener {
    return object : View.OnClickListener {
        private var firstClickTime: Long = 0

        override fun onClick(v: View) {
            if (System.currentTimeMillis() - firstClickTime > throttleTime) {
                firstClickTime = System.currentTimeMillis()
                action(v)
            }
        }
    }
}