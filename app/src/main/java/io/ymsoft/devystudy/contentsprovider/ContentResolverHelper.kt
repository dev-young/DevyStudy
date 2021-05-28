package io.ymsoft.devystudy.contentsprovider

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import io.ymsoft.devystudy.contentsprovider.models.Bucket
import io.ymsoft.devystudy.contentsprovider.models.Photo
import timber.log.Timber

/**ContentResolver를 사용해 핸드폰의 이미지들을 불러온다. */
class ContentResolverHelper(private val contentResolver: ContentResolver) {
    var loadLimit = 0    // 가져올 이미지 혹은 동영상의 최대 갯수 (0 == 무한)

    /**핸드폰의 이미지들을 핸드폰에 추가된 날짜를 바탕으로 내림차순 정렬하여 불러온다*/
    fun loadPhotos(limitCount: Int = loadLimit): ArrayList<Photo> {
        val res = arrayListOf<Photo>()
        val allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,    //ID (uri 불러올때 필요)
            MediaStore.Images.Media.DISPLAY_NAME,   //파일명
            MediaStore.Images.Media.DATE_ADDED, //추가된 날짜
//            MediaStore.Images.Media.DATE_TAKEN, //촬영 날짜
//            MediaStore.Images.Media.DATE_EXPIRES, //만료일 (이게 뭘 의미하는지 모르겠다.)
            MediaStore.Images.Media.SIZE    //파일 크기
        )   // 불러올 컬럼들 목록 (필요한 컬럼들을 선언하고 뒤에서 getColumnIndexOrThrow()를 통해 커서에서 정보를 추출한다.)
        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //안드로이드 Q 이상에서는 쿼리에 LIMIT 를 사용할 수 없다. 때문에 아래와 같이 번들을 통해 조건을 추가해준다
            val bundle = Bundle()
            if (limitCount > 0) bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, limitCount)
            bundle.putString(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                MediaStore.Images.Media.DATE_ADDED
            )
            bundle.putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
            contentResolver.query(allImagesUri, projection, bundle, null)
        } else {
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC" +
                    if (limitCount > 0) " LIMIT $limitCount" else ""
            contentResolver.query(allImagesUri, projection, null, null, sortOrder)
        }

        cursor ?: return res.apply { Timber.e("cursor == null") }

        try {
            cursor.moveToFirst()
            val idIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            do {
                val id = cursor.getLong(idIdx)
                val name = cursor.getString(nameIdx)
                val date = cursor.getLong(dateIdx)
                val size = cursor.getInt(sizeIdx)
                //요청한 _id값을 통해 uri를 가져온다.
                val imgUri = Uri.withAppendedPath(allImagesUri, id.toString())
                val photo = Photo(imgUri, date, name, size)
                res.add(photo)
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return res
    }

    /**Bucket(이미지폴더)을 불러온다.
     * 실제로는 모든 이미지들을탐색하는데 가져오는 정보가  */
    @SuppressLint("InlinedApi") //BUCKET 관련 컬럼을 요청하는건 API 29 이상에서만 사용 가능하다는데 실제로는 더 밑에서도 사용 가능하다.
    fun loadBuckets(): ArrayList<Bucket> {
        val res = ArrayList<Bucket>()
        val bucketMap = hashMapOf<Long, Bucket>()
        val allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,    //ID (uri 불러올때 필요)
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        )
        val cursor = contentResolver.query(allImagesUri, projection, null, null, null)
        cursor ?: return res

        try {
            cursor.moveToFirst()
            val idIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val nameIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            do {
                val id = cursor.getLong(idIdx)
                val bucketId = cursor.getLong(bucketIdx)
                val name = cursor.getString(nameIdx)

                /**요청한 _id값을 통해 uri를 가져온다.*/
                val uri = Uri.withAppendedPath(allImagesUri, id.toString())
                val bucketUri = Uri.withAppendedPath(allImagesUri, bucketId.toString())
                bucketMap[bucketId]?.let {
                    it.increaseImgCount()
                    it.firstPic = uri
                } ?: kotlin.run {
                    bucketMap[bucketId] = Bucket(bucketId, bucketUri).also {
                        it.firstPic = uri
                        it.numberOfImg = 1
                        it.name = name
                        res.add(it)
                    }
                }

            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return res
    }

    /**버켓ID를 통해 해당 버켓에 들어있는 이미지들을 쿼리하여 가져온다.*/
    @SuppressLint("InlinedApi")
    fun loadPhotos(bucketId: Long): ArrayList<Photo> {
        var res = ArrayList<Photo>()
        val allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE
        )
        val cursor = contentResolver.query(
            allImagesUri,
            projection,
            MediaStore.Images.Media.BUCKET_ID + " = ? ", arrayOf("$bucketId"),
            null
        )
        cursor ?: return res

        try {
            cursor.moveToFirst()
            val idIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            do {
                val id = cursor.getLong(idIdx)
                val name = cursor.getString(nameIdx)
                val date = cursor.getLong(dateIdx)
                val size = cursor.getInt(sizeIdx)
                //요청한 _id값을 통해 uri를 가져온다.
                val imgUri = Uri.withAppendedPath(allImagesUri, id.toString())
                val photo = Photo(imgUri, date, name, size)
                res.add(photo)
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return res
    }
}