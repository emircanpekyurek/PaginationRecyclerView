package com.pekyurek.emircan.pagination

sealed class PaginationStatus {
    data class Success(val page: Int?) : PaginationStatus()
    data class Error(val errorDescription: String) : PaginationStatus()
    object Reset : PaginationStatus()
}