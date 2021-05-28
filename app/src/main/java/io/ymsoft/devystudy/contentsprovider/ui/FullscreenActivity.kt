package io.ymsoft.devystudy.contentsprovider.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.ymsoft.devystudy.databinding.ActivityFullscreenBinding

class FullscreenActivity : AppCompatActivity() {
    companion object {
        fun start(uri: Uri, from: Context) {
            val intent = Intent(from, FullscreenActivity::class.java)
            intent.putExtra("uri", uri)
            from.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityFullscreenBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)



        intent.getParcelableExtra<Uri>("uri")?.let {
            Glide.with(this)
                .load(it)
                .fitCenter()
                .into(binding.fullscreenContent)
        }
    }
}
