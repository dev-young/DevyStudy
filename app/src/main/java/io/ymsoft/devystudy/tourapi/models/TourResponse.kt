package io.ymsoft.devystudy.tourapi.models

import com.google.gson.annotations.SerializedName

data class TourResponse(
    @SerializedName("numOfRows") val numOfRows:Int,
    @SerializedName("pageNo") val pageNo:Int,
    @SerializedName("totalCount") val totalCount:Int,
)
