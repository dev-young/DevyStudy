package io.ymsoft.devystudy.tourapi.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.ymsoft.devystudy.tourapi.models.Tour
import io.ymsoft.devystudy.tourapi.models.TourResponse
import io.ymsoft.devystudy.tourapi.remote.ServiceGenerator
import timber.log.Timber

class TourViewModel : ViewModel() {

    var disposable: Disposable? = null
    val resultText = MutableLiveData<String>()
    val searchResult = MutableLiveData<List<Tour>>()
    val isLoading = MutableLiveData(false)
    private val gson = Gson()

    fun search(word: String) {
        isLoading.value = true
        disposable = ServiceGenerator.createTour().searchWithKeyWord(word)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                convert(it)
            }
            .subscribe { pair, e ->
                if (e != null) {
                    Timber.e(e)
                } else {
                    val (tourResponse, tours) = pair
                    Timber.i(tourResponse.toString())
                    searchResult.postValue(tours)
                    tours.toString().let {
                        Timber.i(it)
                        resultText.postValue(it)
                    }
                }
                isLoading.postValue(false)
            }
    }

    private fun convert(jsonObject: JsonObject): Pair<TourResponse, List<Tour>> {
        val body = jsonObject.get("response").asJsonObject.get("body")
        val items = body.asJsonObject.get("items").asJsonObject.get("item")
        val tourResponse = gson.fromJson(body, TourResponse::class.java)
        val tours = gson.fromJson<List<Tour>>(items, object: TypeToken<List<Tour>>(){}.type)
        return Pair(tourResponse, tours)
    }


    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }
}