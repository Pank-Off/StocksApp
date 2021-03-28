package ru.punkoff.stocksapp.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ChartFragmentBinding

@SuppressLint("ClickableViewAccessibility")
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

fun MaterialButton.activateButton(binding: ChartFragmentBinding) {
    with(binding) {
        when (this@activateButton.id) {
            R.id.day_btn -> {
                dayBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.black)
                dayBtn.setTextColor(Color.WHITE)
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.card_background)
                weekBtn.setTextColor(Color.BLACK)
                monthBtn.backgroundTintList =
                    ContextCompat.getColorStateList(monthBtn.context, R.color.card_background)
                monthBtn.setTextColor(Color.BLACK)
                yearBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.card_background)
                yearBtn.setTextColor(Color.BLACK)
            }
            R.id.week_btn -> {
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.black)
                weekBtn.setTextColor(Color.WHITE)
                dayBtn.backgroundTintList =
                    ContextCompat.getColorStateList(dayBtn.context, R.color.card_background)
                dayBtn.setTextColor(Color.BLACK)
                monthBtn.backgroundTintList =
                    ContextCompat.getColorStateList(monthBtn.context, R.color.card_background)
                monthBtn.setTextColor(Color.BLACK)
                yearBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.card_background)
                yearBtn.setTextColor(Color.BLACK)
            }
            R.id.month_btn -> {
                monthBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.black)
                monthBtn.setTextColor(Color.WHITE)
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.card_background)
                weekBtn.setTextColor(Color.BLACK)
                dayBtn.backgroundTintList =
                    ContextCompat.getColorStateList(dayBtn.context, R.color.card_background)
                dayBtn.setTextColor(Color.BLACK)
                yearBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.card_background)
                yearBtn.setTextColor(Color.BLACK)
            }
            R.id.year_btn -> {
                yearBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.black)
                yearBtn.setTextColor(Color.WHITE)
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.card_background)
                weekBtn.setTextColor(Color.BLACK)

                monthBtn.backgroundTintList =
                    ContextCompat.getColorStateList(monthBtn.context, R.color.card_background)
                monthBtn.setTextColor(Color.BLACK)
                dayBtn.backgroundTintList =
                    ContextCompat.getColorStateList(yearBtn.context, R.color.card_background)
                dayBtn.setTextColor(Color.BLACK)
            }
        }
    }
}