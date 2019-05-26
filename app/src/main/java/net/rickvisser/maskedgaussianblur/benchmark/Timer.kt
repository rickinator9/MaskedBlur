package net.rickvisser.maskedgaussianblur.benchmark

import android.util.Log

class Timer {

    private var startTime: Long = 0
    private var previousTime: Long = 0

    fun start() {
        startTime = System.nanoTime()
        previousTime = startTime
    }

    fun recordTime(log: String) {
        val currentTime = System.nanoTime()
        val difference = currentTime - previousTime

        val millis = difference.toFloat() / 1000000
        Log.d("TIMER", log + ": ${millis}ms")

        previousTime = currentTime
    }

    fun stop() {
        val currentTime = System.nanoTime()
        val difference = currentTime - startTime

        val millis = difference.toFloat() / 1000000
        Log.d("TIMER", "Total runtime: ${millis}ms")

        startTime = 0
        previousTime = 0
    }
}