package com.bitlove.fetlife.view.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bitlove.fetlife.R
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : Activity() {

    companion object {
        private const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"
        fun start(imageUrl: String, context: Context) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_URL,imageUrl)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        image_view.setPhotoUri(Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URL)!!))
    }

}