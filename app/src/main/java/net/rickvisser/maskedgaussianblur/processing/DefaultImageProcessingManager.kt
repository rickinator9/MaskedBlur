package net.rickvisser.maskedgaussianblur.processing

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import net.rickvisser.maskedgaussianblur.processing.configs.ProcessConfig
import net.rickvisser.maskedgaussianblur.processing.creators.BlurProcessCreator
import net.rickvisser.maskedgaussianblur.processing.creators.CensorProcessCreator
import net.rickvisser.maskedgaussianblur.processing.creators.ProcessCreator
import net.rickvisser.maskedgaussianblur.processing.creators.ProcessResultHandler
import net.rickvisser.maskedgaussianblur.ui.activity.ImageProcessingView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Default implementation of an image processing manager.
 *
 * This implementation uses a threadpool with a single thread to queue image processing operations and
 * synchronises the result of the operation with the Android UI thread upon completion. Configs are handled
 * using ProcessCreator implementations. These return a Runnable class based on the config that was supplied
 * in the process() function.
 *
 * @param context The context of the calling activity. Required for operations involving renderscripts.
 * @param view A handle to a view that will use the resulting image of an operation.
 */
class DefaultImageProcessingManager(context: Context, private val view: ImageProcessingView): ImageProcessingManager, ProcessResultHandler {
    /** List containing ProcessCreator objects. */
    private val processCreators: List<ProcessCreator>

    /** Threadpool to schedule operations. */
    private val threadPool: ExecutorService

    init {
        // Initialize the process creators.
        val blurProcessCreator = BlurProcessCreator(context, this)
        val censorProcessCreator = CensorProcessCreator(context, this)
        processCreators = listOf(blurProcessCreator, censorProcessCreator)

        // Initialize the Threadpool.
        threadPool = Executors.newSingleThreadExecutor()
    }

    /**
     * Schedules an image processing operation according to the configuration supplied.
     *
     * @param config Configuration object that contains parameters useful to image processing.
     */
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

    /**
     * Transfers an image from an image processing operation to the processing manager. This implementation
     * synchronises the image with the Android UI thread.
     *
     * @param image The image to be transferred.
     */
    override fun transferImage(image: Bitmap) {
        Handler(Looper.getMainLooper()).post {
            view.showImage(image)
        }
    }
}