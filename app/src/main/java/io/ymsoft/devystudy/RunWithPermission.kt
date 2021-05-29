package io.ymsoft.devystudy

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import timber.log.Timber

class RunWithPermission(
    private val componentActivity: ComponentActivity,
    vararg permissions: String
) {
    private val permissions = LinkedHashSet<String>(permissions.toList())
    private val context = componentActivity.applicationContext

    private var lastRequestTime = 0L

    private val callback = { granted: Map<String, Boolean> ->
        if (granted.values.contains(false)) {
            val diff = SystemClock.uptimeMillis() - lastRequestTime
            Timber.i("거절까지 걸린 시간: $diff")
            if (diff < 600) {   //팝업창이 뜨지 않아서 거절을 하기까지 걸리는 시간이 현저히 짧을 경우
                doWhenDeniedTwice?.invoke(this)
            } else doWhenDenied?.invoke(this)
        } else {
            doGrantedAction()
        }
        Unit
    }

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        componentActivity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            callback
        )

    var doWhenGranted: (() -> Unit)? = null
    var doWhenDenied: ((RunWithPermission) -> Unit)? = null

    //거절을 두번 당했을 때 (Android 11에서는 두번 거절당하면 더이상 팝업창이 안뜬다. 이때 처리할 작업)
    var doWhenDeniedTwice: ((RunWithPermission) -> Unit)? = null

    fun setGrantedAction(action: (() -> Unit)): RunWithPermission {
        doWhenGranted = action
        return this
    }

    fun setDeniedAction(action: ((RunWithPermission) -> Unit)): RunWithPermission {
        doWhenDenied = action
        return this
    }

    fun setDeniedTwiceAction(action: ((RunWithPermission) -> Unit)): RunWithPermission {
        doWhenDeniedTwice = action
        return this
    }

    fun isGranted(): Boolean {
        for (p in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    p
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestPermission() {
        lastRequestTime = SystemClock.uptimeMillis()
        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun isUserClickDenied(): Boolean {
        var isUserClickDenied = false
        for (p in permissions) {
            if (componentActivity.shouldShowRequestPermissionRationale(p)) {
                isUserClickDenied = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    deniedCount[p] = deniedCount[p]!! + 1
                } else break
            }
        }
        if(isUserClickDenied)
            save()
        return isUserClickDenied
    }

    fun startPermissionIntent() {
        val i = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + context.packageName)
        )
        componentActivity.startActivity(i)
    }

    fun run() {
        if (isGranted()) {
            doGrantedAction()
        } else {

            val isUserClickDenied = isUserClickDenied()

            if (!deniedCount.values.none { it > 1 }) {
                //거절을 두번 이상 누른 권한이 있을경우
                doWhenDeniedTwice?.invoke(this)

            } else if (isUserClickDenied) {
                //거절을 직접 누른경우
                doWhenDenied?.invoke(this)

            } else {
                //권한 승인 여부를 선택 안했거나
                requestPermission()
            }
        }
    }


    private fun doGrantedAction() {
        doWhenGranted?.invoke()
        clear()
    }


    private val PERMISSION = "permission"
    private val deniedCount = hashMapOf<String, Int>().apply {
        permissions.forEach {
            this[it] = 0
        }
    }

    fun save() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val sp = context.getSharedPreferences(PERMISSION, Activity.MODE_PRIVATE)
            val editor = sp.edit()
            deniedCount.forEach { (key, v) ->
                editor.putInt(key, v)
            }
            editor.apply()
        }
    }

    fun load() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val sp = context.getSharedPreferences(PERMISSION, Activity.MODE_PRIVATE)
            deniedCount.keys.forEach { k ->
                deniedCount[k] = sp.getInt(k, 0)
            }
        }
    }

    fun clear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val sp = context.getSharedPreferences(PERMISSION, Activity.MODE_PRIVATE)
            val editor = sp.edit()
            editor.clear()
            editor.apply()
        }
    }

    init {
        load()
    }

}