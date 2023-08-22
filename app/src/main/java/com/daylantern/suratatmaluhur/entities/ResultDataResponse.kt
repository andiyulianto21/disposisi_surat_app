package com.daylantern.suratatmaluhur.entities

data class ResultDataResponse<T>(
    val status: Int,
    val message: String? = null,
    val data: T
)

data class ResultListDataResponse<T>(
    val status: Int,
    val message: String? = null,
    val data: List<T>
)