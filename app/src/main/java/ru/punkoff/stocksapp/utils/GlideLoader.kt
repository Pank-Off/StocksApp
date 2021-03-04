package ru.punkoff.stocksapp.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

object GlideLoader {

    fun loadImage(url: String, imageView: ImageView) {
        Glide.with(imageView)
            .load(url)
            .into(imageView)
    }
}