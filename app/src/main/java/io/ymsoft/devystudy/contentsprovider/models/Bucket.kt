package io.ymsoft.devystudy.contentsprovider.models

import android.net.Uri

/**이미지 폴더*/
class Bucket(val id: Long, val uri: Uri) {
    var name: String? = null
    var numberOfImg = 0    //폴더에 들어있는 사진 수

    var firstPic: Uri? = null   //대표 이미지

    fun increaseImgCount() {
        numberOfImg++
    }

    fun getImgCount() = numberOfImg.toString()
}
