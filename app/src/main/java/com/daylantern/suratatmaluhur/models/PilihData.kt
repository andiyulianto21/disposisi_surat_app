package com.daylantern.suratatmaluhur.models

data class PilihData(
    val id: Int,
    val title: String,
    val jabatan: String,
    var isChecked: Boolean = false
)
