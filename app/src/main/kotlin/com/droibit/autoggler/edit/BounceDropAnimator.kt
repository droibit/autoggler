package com.droibit.autoggler.edit

import android.os.Handler
import android.view.animation.BounceInterpolator
import com.droibit.autoggler.data.config.ApplicationConfig
import com.droibit.autoggler.data.provider.time.TimeProvider
import com.google.android.gms.maps.model.Marker
import timber.log.Timber


class BounceDropAnimator(
        private val config: ApplicationConfig,
        private val timeProvider: TimeProvider) {

    inner class DropRunnable(private val marker: Marker, private val callback: (Marker)->Unit) : Runnable {

        private val startMillis = timeProvider.elapsedRealTimeMillis

        private val interpolator = BounceInterpolator()

        override fun run() {
            val elapsedMillis = (timeProvider.elapsedRealTimeMillis - startMillis).toFloat()
            val t = Math.max(1f - interpolator.getInterpolation((elapsedMillis / config.bounceDropAnimateDurationMillis)), 0f)
            marker.setAnchor(.5f, 1f + 10f * t)

            if (t > 0f) {
                handler.postDelayed(this, 20L)
                return
            }
            callback(marker)
            isAnimating = false
            Timber.d("Stop bounce animation")
        }
    }

    var isAnimating: Boolean = false
        private set(value) {
            field = value
            if (!value) {
                stop()
            }
        }

    private val handler = Handler()

    private var dropRunnable: Runnable? = null

    fun start(target: Marker, dropCallback: (Marker) -> Unit) {
        isAnimating = true
        dropRunnable = DropRunnable(marker = target, callback = dropCallback)
        handler.post(dropRunnable)

        Timber.d("Start bounce animation")
    }

    fun stop() {
        dropRunnable?.let { handler.removeCallbacks(it) }
        dropRunnable = null
    }
}