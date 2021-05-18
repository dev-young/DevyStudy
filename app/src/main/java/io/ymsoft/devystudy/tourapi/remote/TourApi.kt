package io.ymsoft.devystudy.tourapi.remote

import com.google.gson.JsonObject
import io.reactivex.Single
import io.ymsoft.devystudy.tourapi.models.TourResponse
import retrofit2.http.*

interface TourApi {
    companion object {
        const val BASE_URL = "http://api.visitkorea.or.kr/"
        const val BASE_ENDPOINT = "openapi/service/rest/KorService/"
        const val KEY =
            "JtaAnoVKjZcwOIfcC0aP6KmkPE7wV2/waeJIbpIYKMhBsPSGBKZiq0VaZEix2tVgaqwMAjGCtn85eHjZbnhrDA=="
        //Decoding 된 인증키 사용
    }

    @GET(BASE_ENDPOINT+"searchKeyword")
    fun searchWithKeyWord(
        @Query("keyword") word: String,
        @Query("ServiceKey") key: String = KEY,
        @Query("MobileApp") appName: String = "DevyStudy",
        @Query("MobileOS") os: String = "AND",
        @Query("listYN") listYN: String = "Y",
        @Query("_type") type: String = "json"
    ): Single<JsonObject>

    @GET(BASE_ENDPOINT+"locationBasedList")
    fun searchWithLatLng(
        @Query("mapY") lat: String,
        @Query("mapX") lng: String,
        @Query("radius") radius: String = "1000", //단위(m)
        @Query("ServiceKey") key: String = KEY,
        @Query("MobileApp") appName: String = "DevyStudy",
        @Query("MobileOS") os: String = "AND",
        @Query("_type") type: String = "json"
    ): Single<JsonObject>


    @FormUrlEncoded
    @POST("api url")
    fun test(
        @Field("access_token") token: String?,
        @Field("code") authorization_code: String?
    ): Single<HashMap<String, Any>>

}