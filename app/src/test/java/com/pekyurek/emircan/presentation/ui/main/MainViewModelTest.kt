package com.pekyurek.emircan.presentation.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pekyurek.emircan.data.repository.Person
import com.pekyurek.emircan.pagination.PaginationStatus
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

internal class MainViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    private val dummyList = listOf(Person(1, "Mehmet Yılmaz"), Person(2, "Ahmet Şimşek"))

    @Before
    fun setUp() {
        viewModel = MainViewModel()
    }

    @Test
    fun `success pagination result`() {
        //Given
        val pageNo = 2
        val paginationStatus = PaginationStatus.Success(pageNo)

        //When
        viewModel.onPaginationResult(paginationStatus)

        //Then
        assert(viewModel.loadPage.value == pageNo.toString())
        assert(viewModel.paginationErrorResult.value == null)
        assert(viewModel.paginationResetResult.value == null)
    }

    @Test
    fun `reset pagination result`() {
        //Given
        val paginationStatus = PaginationStatus.Reset

        //When
        viewModel.onPaginationResult(paginationStatus)

        //Then
        assert(viewModel.loadPage.value == null)
        assert(viewModel.paginationErrorResult.value == null)
        assert(viewModel.paginationResetResult.value is Any)
    }

    @Test
    fun `error pagination result`() {
        //Given
        val errorDescription = "test error"
        val paginationStatus = PaginationStatus.Error(errorDescription)

        //When
        viewModel.onPaginationResult(paginationStatus)

        //Then
        assert(viewModel.loadPage.value == null)
        assert(viewModel.paginationErrorResult.value == errorDescription)
        assert(viewModel.paginationResetResult.value == null)
    }


    @Test
    fun `success response, empty list, first page`() {
        //Given
        val firstPage = true
        val list = emptyList<Person>()

        //When
        viewModel.onSuccessResponse(firstPage, list)

        //Then
        assert(viewModel.emptyList.value == true)
        assert(viewModel.paginationNewData.value == null)
    }

    @Test
    fun `success response, empty list, not first page`() {
        //Given
        val firstPage = false
        val list = emptyList<Person>()

        //When
        viewModel.onSuccessResponse(firstPage, list)

        //Then
        assert(viewModel.emptyList.value == false)
        assert(viewModel.paginationNewData.value == list)
    }

    @Test
    fun `success response, not empty list, first page`() {
        //Given
        val firstPage = true
        val list = dummyList

        //When
        viewModel.onSuccessResponse(firstPage, list)

        //Then
        assert(viewModel.emptyList.value == false)
        assert(viewModel.paginationNewData.value == list)
    }

    @Test
    fun `success response, not empty list, not first page`() {
        //Given
        val firstPage = false
        val list = emptyList<Person>()

        //When
        viewModel.onSuccessResponse(firstPage, list)

        //Then
        assert(viewModel.emptyList.value == false)
        assert(viewModel.paginationNewData.value == list)
    }

    @Test
    fun `error response for first page`() {
        //Given
        val firstPage = true
        val errorDescription = "test error"

        //When
        viewModel.onErrorResponse(firstPage, errorDescription)

        //Then
        assert(viewModel.emptyList.value == true)
        assert(viewModel.errorServiceResponse.value == errorDescription)
    }

    @Test
    fun `error response for not first page`() {
        //Given
        val firstPage = false
        val errorDescription = "test error"

        //When
        viewModel.onErrorResponse(firstPage, errorDescription)

        //Then
        assert(viewModel.emptyList.value == false)
        assert(viewModel.errorServiceResponse.value == errorDescription)
    }
}