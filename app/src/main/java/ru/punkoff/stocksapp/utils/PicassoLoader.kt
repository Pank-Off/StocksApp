package ru.punkoff.stocksapp.utils

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import ru.punkoff.stocksapp.R

object PicassoLoader {

    fun loadImage(url: String, imageView: ImageView) {
        try {
            Picasso.get().isLoggingEnabled = true
            Picasso.get().load(url).fit().error(R.drawable.no_image_icon).into(imageView)
        } catch (exc: IllegalArgumentException) {
            Log.e(javaClass.toString(), "IllegalExceptionURL $url")
            Picasso.get().load(R.drawable.no_image_icon).into(imageView)
        }
    }
}