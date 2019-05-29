package net.rickvisser.maskedgaussianblur.processing

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import net.rickvisser.maskedgaussianblur.processing.configs.ProcessConfig
import net.rickvisser.maskedgaussianblur.processing.creators.BlurProcessCreator
import net.rickvisser.maskedgaussianblur.processing.creators.ProcessCreator
import net.rickvisser.maskedgaussianblur.processing.creators.ProcessResultHandler
import net.rickvisser.maskedgaussianblur.ui.activity.BlurView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DefaultImageProcessingManager(context: Context, private val view: BlurView): ImageProcessingManager, ProcessResultHandler {
    private val processCreators: List<ProcessCreator>
    private val threadPool: ExecutorService

    init {
        val blurProcessCreator = BlurProcessCreator(context, this)
        processCreators = listOf(blurProcessCreator)
        threadPool = Executors.newSingleThreadExecutor()
    }

    override fun process(config: ProcessConfig) {
        // Give the creators the responsibility of creating the runnable.
        for (creator in processCreators) {
            val runnable = creator.create(config) ?: continue

            threadPool.execute(runnable)
            return
        }

        // If no runnable could be created, throw an exception.
        throw IllegalArgumentException("No creator present for config.")
    }

    override fun transferImage(image: Bitmap) {
        Handler(Looper.getMainLooper()).post {
            view.showImage(image)
        }
    }
}