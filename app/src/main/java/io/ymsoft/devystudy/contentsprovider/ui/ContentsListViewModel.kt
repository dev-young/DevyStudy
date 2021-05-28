package io.ymsoft.devystudy.contentsprovider.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.ymsoft.devystudy.contentsprovider.ContentResolverHelper
import io.ymsoft.devystudy.contentsprovider.models.Bucket
import io.ymsoft.devystudy.contentsprovider.models.Photo

class ContentsListViewModel(application: Application) : AndroidViewModel(application) {
    private val helper = ContentResolverHelper(application.contentResolver)

    val isLoading = MutableLiveData(false)
    val bucketList = MutableLiveData<List<Bucket>>()
    val bucketSelected = MutableLiveData<Bucket>()
    val photoList = MutableLiveData<List<Photo>>()

    fun selectBucket(bucket: Bucket) {
        bucketSelected.value = bucket
        photoList.value = emptyList()
        loadPhoto(bucket)
    }

    fun loadBucket() {
        isLoading.value = true
        Single.just(Unit).subscribeOn(Schedulers.io())
            .subscribe { r, t ->
                isLoading.postValue(false)
                if (t != null) return@subscribe
                bucketList.postValue(helper.loadBuckets())
            }
    }

    private fun loadPhoto(bucket: Bucket) {
        isLoading.value = true
        Single.just(Unit).subscribeOn(Schedulers.io())
            .subscribe { r, t ->
                isLoading.postValue(false)
                if (t != null) return@subscribe
                photoList.postValue(helper.loadPhotos(bucket.id))
            }
    }

    fun loadAllPhoto(limit : Int = 10000) {
        isLoading.value = true
        Single.just(Unit).subscribeOn(Schedulers.io())
            .subscribe { r, t ->
                isLoading.postValue(false)
                if (t != null) return@subscribe
                photoList.postValue(helper.loadPhotos(limit))
            }
    }

}