package ru.punkoff.stocksapp.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso
import ru.punkoff.stocksapp.R

object PicassoLoader {

    fun loadImage(url: String, imageView: ImageView) {
        try {
            Picasso.get().load(url).into(imageView)
        } catch (exc: IllegalArgumentException) {
            Picasso.get().load(R.drawable.no_image_icon).into(imageView)
        }
    }
}