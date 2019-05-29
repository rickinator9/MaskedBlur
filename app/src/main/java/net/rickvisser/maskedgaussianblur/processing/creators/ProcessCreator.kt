package net.rickvisser.maskedgaussianblur.processing.creators

import android.graphics.Bitmap
import net.rickvisser.maskedgaussianblur.processing.configs.ProcessConfig

interface ProcessCreator {
    fun create(config: ProcessConfig): Runnable?
}

interface ProcessResultHandler {
    fun transferImage(image: Bitmap)
}