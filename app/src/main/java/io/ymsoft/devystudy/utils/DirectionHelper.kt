package io.ymsoft.devystudy.utils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

/**각각의 API들을 사용해서 도보 경로를 가져온다.*/
class DirectionHelper {
    val TAG = "DirectionHelper"

    val GOOGLE_KEY = "AIzaSyB1o5uxVJYsgol2dYCIVI-IuMSwB_hwuZU"

    val NAVER_ID = "vjlw7hgaty"
    val NAVER_KEY = "OoDy38JGKnIZG3SZL52q9dSpYQEmrOejxrJtYIkR"

    val TMAP_KEY = "l7xx803c6d00dff241e99940568c45f4e5b6"


    /**국내에서는 대중교통 이용하는 경로만 제공됩니다.*/
    fun getPathFromGoogle(sta: LatLng, des: LatLng): List<LatLng> {
        val url =
            "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${sta.latitude},${sta.longitude}" +
                    "&destination=${des.latitude},${des.longitude}" +
                    "&mode=transit" +
//                    "&alternatives=true" +
                    "&departure_time=now" +
                    "&key=$GOOGLE_KEY"
        val connection = URL(url).openConnection()
        Log.i(TAG, "query: $url")
        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
        val routes = JSONObject(data).getJSONArray("routes")

        val res = arrayListOf<LatLng>()
        for (i in 0 until routes.length()) {
            val r = routes.getJSONObject(i)
            r.getJSONObject("overview_polyline").getString("points").let {
                Log.i(TAG, "getPathFromGoogle: poly: $it")
                PolyUtil.decode(it)?.let {
                    res.addAll(it)
                }
            }
        }

        return res
    }


    /**차량 경로만 제공합니다.*/
    fun getPathFromNaver(sta: LatLng, des: LatLng): List<LatLng> {
        val url =
            "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=${sta.longitude},${sta.latitude},&goal=${des.longitude},${des.latitude}"
        val connection = URL(url).openConnection()
        connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", NAVER_ID)
        connection.setRequestProperty("X-NCP-APIGW-API-KEY", NAVER_KEY)
        Log.i(TAG, "query: $url")
        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))

        val root = JSONObject(data)
        val route: JSONObject = root.getJSONObject("route")
        val traoptimal = route.getJSONArray("traoptimal")[0] as JSONObject
//        val summary = traoptimal.getJSONObject("summary")
        val path = traoptimal.getJSONArray("path")

        val res = arrayListOf<LatLng>()
        for (i in 0 until path.length()) {
            val path = path.getJSONArray(i)
            res.add(LatLng(path.getDouble(1), path.getDouble(0)))
        }

        return res
    }


    fun getPathFromTmap(sta: LatLng, des: LatLng): List<LatLng> {
        var url: URL? = null
        var conn: HttpURLConnection?
        val res = arrayListOf<LatLng>()
        try {
            val startName = URLEncoder.encode("출발지", "UTF-8")
            val endName = URLEncoder.encode("도착지", "UTF-8")
            val urlStr = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&" +
                    "callback=result&appKey=$TMAP_KEY&" +
                    "startX=${sta.longitude}&" +
                    "startY=${sta.latitude}&" +
                    "endX=${des.longitude}&" +
                    "endY=${des.latitude}&" +
                    "startName=$startName&" +
                    "endName=$endName"
            url = URL(urlStr)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        try {
            //uri를 담아서 데이터를 보내면
            conn = url!!.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Accept-Charset", "utf-8")
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val data = conn.inputStream.readBytes().toString(charset("UTF-8"))

            //전체 데이터를 제이슨 객체로 변환
            val root = JSONObject(data)

            //전체 데이터중에 features리스트의 첫번째 객체를 가지고 오기
            val features = root.getJSONArray("features")

            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val geometry = feature.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                if(coordinates.toString().startsWith("[[")) {
                    for (j in 0 until coordinates.length()) {
                        val coo = coordinates.getJSONArray(j)
                        res.add(LatLng(coo.getDouble(1), coo.getDouble(0)))
                    }
                } else {
                    res.add(LatLng(coordinates.getDouble(1), coordinates.getDouble(0)))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }

    fun getPathOnBackGround(start: LatLng, destination: LatLng, completeListener: (List<LatLng>?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var res = getPathFromTmap(start, destination)
            if (res.isEmpty()) res = getPathFromNaver(start, destination)
            if (res.isEmpty()) res = getPathFromGoogle(start, destination)

            launch(Dispatchers.Main) {
                completeListener.invoke(res)
            }
        }
    }

}




