package com.pekyurek.emircan.presentation.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.pekyurek.emircan.R
import com.pekyurek.emircan.databinding.ActivityMainBinding
import com.pekyurek.emircan.extension.showToast
import com.pekyurek.emircan.presentation.ui.base.BaseActivity
import com.pekyurek.emircan.presentation.ui.main.adapter.PersonAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutResId: Int = R.layout.activity_main

    private val viewModel: MainViewModel by viewModels()

    private val personAdapter: PersonAdapter by lazy { PersonAdapter { binding.rvPerson.reloadLastPage() } }

    override fun initBinding() {
        super.initBinding()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun initViews() {
        initSwipeRefreshLayout()
        initRecyclerView()
    }

    private fun initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener { binding.rvPerson.resetPagination() }
    }

    private fun initRecyclerView() {
        binding.rvPerson.apply {
            adapter = personAdapter
            lifecycleScope.launch { paginationFlow.collect { viewModel.onPaginationResult(it) } }
        }
    }

    override fun setObservers() {
        viewModel.loadPage.observe(this) { viewModel.loadData(it) }

        viewModel.paginationResetResult.observe(this) {
            personAdapter.clearData()
            viewModel.loadData()
        }

        viewModel.paginationErrorResult.observe(this) { errorDescription ->
            showToast(errorDescription)
            personAdapter.retryButtonVisibility = true
        }

        viewModel.errorServiceResponse.observe(this) { binding.rvPerson.setError(it) }

        viewModel.paginationNewData.observe(this) { personAdapter.addData(it) }

        viewModel.loading.observe(this) { loading ->
            binding.swipeRefreshLayout.isRefreshing = loading
            personAdapter.retryButtonClickable = !loading
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        viewModel.loadData()
    }
}