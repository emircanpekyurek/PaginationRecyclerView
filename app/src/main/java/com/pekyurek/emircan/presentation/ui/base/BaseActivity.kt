package com.pekyurek.emircan.presentation.ui.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<Binding : ViewBinding> : AppCompatActivity() {

    protected abstract val layoutResId: Int
    protected lateinit var binding: Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initViews()
        setObservers()
        onInit(savedInstanceState)
    }

    @CallSuper
    open fun initBinding() {
        binding = DataBindingUtil.setContentView(this, layoutResId)
    }

    open fun initViews() {}

    open fun setObservers() {}

    open fun onInit(savedInstanceState: Bundle?) {}

}