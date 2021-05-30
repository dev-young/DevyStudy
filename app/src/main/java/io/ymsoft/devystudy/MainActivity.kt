package io.ymsoft.devystudy

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import io.ymsoft.devystudy.contentsprovider.ui.BucketListFragment
import io.ymsoft.devystudy.contentsprovider.ui.PhotoListFragment
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.container, BucketListFragment.newInstance())
            }
        }

    }

    fun <T : Fragment> addFragment(f: T) {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.container, f)
        }
        Timber.i("${f.javaClass.simpleName} 추가됨")
    }


    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        val curId = item.itemId
        when (curId) {
            R.id.menu_photo -> {
                addFragment(PhotoListFragment.newInstance(true))
            }
            R.id.menu_album -> {
                supportFragmentManager.popBackStack()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

}