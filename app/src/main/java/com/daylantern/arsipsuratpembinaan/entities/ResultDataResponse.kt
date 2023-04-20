package com.daylantern.arsipsuratpembinaan.entities

data class ResultDataResponse<T>(
    val status: Int,
    val messages: String,
    val data: T
)