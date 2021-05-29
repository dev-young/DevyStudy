package io.ymsoft.devystudy

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class RunWithPermission3 {
    private lateinit var activity: AppCompatActivity
    private lateinit var fragment: Fragment
    private val componentActivity: ComponentActivity
    private val context: Context
    private val permissions = linkedSetOf<String>()
//    val permission: List<String> = _permissions

    private val permissionLauncher: ActivityResultLauncher<Array<String>>
    private val callback = { granted: Map<String, Boolean> ->

        if (granted.values.contains(false)) {
            //거절을 눌렀거나 응답하지 않은 경우
            doWhenDenied?.invoke()
        } else {
            doGrantedAction()
        }
        Unit
    }

    constructor(activity: AppCompatActivity, vararg permissions: String) {
        this.activity = activity
        context = activity
        componentActivity = activity
        permissionLauncher =
            componentActivity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
                callback
            )
        this.permissions.addAll(permissions)
    }

    constructor(fragment: Fragment, vararg permissions: String) {
        this.fragment = fragment
        context = fragment.requireContext()
        componentActivity = fragment.requireActivity()
        permissionLauncher =
            componentActivity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
                callback
            )
        this.permissions.addAll(permissions)
    }

    var doWhenGranted: (() -> Unit)? = null
    var doWhenDenied: (() -> Unit)? = null

    //거절을 두번 당했을 때 (Android 11에서는 두번 거절당하면 더이상 팝업창이 안뜬다. 이때 처리할 작업)
    var doWhenDeniedTwice: (() -> Unit)? = null

    fun setGrantedAction(action: (() -> Unit)): RunWithPermission3 {
        doWhenGranted = action
        return this
    }

    fun setDeniedAction(action: (() -> Unit)): RunWithPermission3 {
        doWhenDenied = action
        return this
    }

    fun setDeniedTwiceAction(action: (() -> Unit)): RunWithPermission3 {
        doWhenDeniedTwice = action
        return this
    }

//    fun setPermission(vararg permission: String): RunWithPermission {
//        permissions.addAll(permission.toList())
//        return this
//    }

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

    fun run() {
        if (isGranted()) {
            doGrantedAction()
        } else {
            var isUserClickDenied = false
            for (p in permissions) {
                if (componentActivity.shouldShowRequestPermissionRationale(p)) {
                    isUserClickDenied = true
                    break
                }
            }

            if(isUserClickDenied) {
                //거절을 직접 누른경우
                doWhenDenied?.invoke()

            } else {
                //권한 승인 여부를 선택 안했거나
                permissionLauncher.launch(permissions.toTypedArray())
            }

        }

    }

    private fun doGrantedAction(){
        doWhenGranted?.invoke()
        clear()
    }

    private val PERMISSION = "permission"
    private val DENIED_COUNT = "denied"
    private var deniedCount = 0

    fun save() {
        val sp = context.getSharedPreferences(PERMISSION, Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(DENIED_COUNT, deniedCount)
        editor.apply()
    }

    fun load() {
        val sp = context.getSharedPreferences(PERMISSION, Activity.MODE_PRIVATE)
        deniedCount = sp.getInt(DENIED_COUNT, 0)
    }

    fun clear() {
        val sp = context.getSharedPreferences(PERMISSION, Activity.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.apply()
    }

}