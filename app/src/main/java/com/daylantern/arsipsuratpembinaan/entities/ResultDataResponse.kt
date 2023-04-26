package com.daylantern.arsipsuratpembinaan.entities

data class ResultDataResponse<T>(
    val status: Int,
    val messages: String? = null,
    val data: T
)

data class ResultListDataResponse<T>(
    val status: Int,
    val messages: String? = null,
    val data: List<T>
)