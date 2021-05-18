package io.ymsoft.devystudy.tourapi.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ymsoft.devystudy.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {
    companion object {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        fun createTour(): TourApi {
            val logging = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                logging.level = HttpLoggingInterceptor.Level.BODY
            } else {
                logging.level = HttpLoggingInterceptor.Level.NONE
            }

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            val retorfit: Retrofit = Retrofit.Builder()
                .baseUrl(TourApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build()

            return retorfit.create(TourApi::class.java)
        }
    }
}