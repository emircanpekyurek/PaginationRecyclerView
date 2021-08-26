package com.pekyurek.emircan.pagination.extension

fun Int?.orZero(): Int {
    return this ?: 0
}