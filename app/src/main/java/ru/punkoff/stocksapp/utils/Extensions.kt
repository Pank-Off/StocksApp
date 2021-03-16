package ru.punkoff.stocksapp.utils

import android.graphics.drawable.Drawable
import android.view.MotionEvent
import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.onLeftDrawableClicked(onClicked: (view: TextInputEditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is TextInputEditText) {
            if (event.x <= v.totalPaddingLeft) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}