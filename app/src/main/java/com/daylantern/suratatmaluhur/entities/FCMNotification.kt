package com.daylantern.suratatmaluhur.entities

import com.google.gson.annotations.SerializedName

data class FCMNotification(
    @SerializedName("token") val token: String,
    @SerializedName("notification") val sendNotificationModel: FCMRequest
)