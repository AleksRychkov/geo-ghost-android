package dev.aleksrychkov.geoghost.feature.map.provider.libre.internal

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat

internal class LibreMapLoaderView(
    context: Context
) : View(context) {

    private companion object {
        const val START_DELAY_MILLIS = 500L
        const val DURATION_MILLIS = 500L
    }

    init {
        ContextCompat.getColor(
            context,
            dev.aleksrychkov.geoghost.core.designsystem.R.color.md_theme_background
        ).let(::setBackgroundColor)
    }

    fun removeWithAnimation(removeFromLayout: (View) -> Unit) {
        animate()
            .alpha(0f)
            .setStartDelay(START_DELAY_MILLIS)
            .withEndAction { removeFromLayout(this) }
            .setDuration(DURATION_MILLIS)
            .start()
    }
}
