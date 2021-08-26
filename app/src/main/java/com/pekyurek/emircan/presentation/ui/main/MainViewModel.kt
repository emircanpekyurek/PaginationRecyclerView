package com.pekyurek.emircan.presentation.ui.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pekyurek.emircan.data.repository.DataSource
import com.pekyurek.emircan.data.repository.Person
import com.pekyurek.emircan.pagination.PaginationStatus

class MainViewModel : ViewModel() {

    val loading = MutableLiveData<Boolean>()

    val errorServiceResponse = MutableLiveData<String>()
    val paginationNewData = MutableLiveData<List<Person>>()
    val emptyList = MutableLiveData<Boolean>()

    val loadPage = MutableLiveData<String?>()
    val paginationResetResult = MutableLiveData<Any>()
    val paginationErrorResult = MutableLiveData<String>()

    fun onPaginationResult(paginationStatus: PaginationStatus) {
        when (paginationStatus) {
            is PaginationStatus.Success -> {
                loadPage.postValue(paginationStatus.page.toString())
            }
            is PaginationStatus.Reset -> {
                paginationResetResult.postValue(Any())
            }
            is PaginationStatus.Error -> {
                paginationErrorResult.postValue(paginationStatus.errorDescription)
            }
        }
    }

    fun loadData(page: String? = null) {
        loading.postValue(true)
        DataSource().fetch(page) { fetchResponse, fetchError ->
            val isFirstPage = page == null
            when {
                fetchResponse != null -> onSuccessResponse(isFirstPage, fetchResponse.people)
                fetchError != null -> onErrorResponse(isFirstPage, fetchError.errorDescription)
            }
            loading.postValue(false)
        }
    }

    @VisibleForTesting
    fun onSuccessResponse(isFirstPage: Boolean, peopleList: List<Person>) {
        val firstPageIsEmpty = isFirstPage && peopleList.isEmpty()
        emptyList.postValue(firstPageIsEmpty)
        if (!firstPageIsEmpty) {
            paginationNewData.postValue(peopleList)
        }
    }

    @VisibleForTesting
    fun onErrorResponse(isFirstPage: Boolean, errorDescription: String) {
        emptyList.postValue(isFirstPage)
        errorServiceResponse.postValue(errorDescription)
    }

}