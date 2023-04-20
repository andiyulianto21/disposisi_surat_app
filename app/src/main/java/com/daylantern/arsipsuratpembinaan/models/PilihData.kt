package com.daylantern.arsipsuratpembinaan.models

data class PilihData(
    val id: Int,
    val title: String,
    var isChecked: Boolean = false
)
