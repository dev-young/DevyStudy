package io.ymsoft.devystudy.contentsprovider.models

import android.net.Uri

data class Photo(
    val uri: Uri,
    val date: Long,
    val displayName: String,
    val size: Int
)
