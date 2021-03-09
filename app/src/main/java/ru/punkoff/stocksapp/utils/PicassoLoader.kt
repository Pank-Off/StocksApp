package ru.punkoff.stocksapp.utils

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ru.punkoff.stocksapp.R
import java.lang.Exception
import java.lang.IllegalArgumentException

object PicassoLoader {

    fun loadImage(url: String, imageView: ImageView) {
        try {
            Picasso.get().load(url).error(R.drawable.no_image_icon).into(imageView)
            Log.d(javaClass.toString(), "IllegalExceptionURL $url")
        } catch (exc: IllegalArgumentException) {
            Log.e(javaClass.toString(), "IllegalExceptionURL $url")
            Picasso.get().load(R.drawable.no_image_icon).into(imageView)
        }
    }
}