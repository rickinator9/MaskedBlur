package net.rickvisser.maskedgaussianblur.processing.creators

import android.graphics.Bitmap
import net.rickvisser.maskedgaussianblur.processing.configs.ProcessConfig

/**
 * Interface that allows custom ProcessCreator implementations to add new image
 * processing operations.
 */
abstract class ProcessCreator(protected val resultHandler: ProcessResultHandler) {

    /**
     * Creates a operation based on the specified config.
     *
     * @param config The config that defines the parameters of the operation.
     * @return A Runnable operation for image processing if this config can be handled by the object.
     * Null otherwise.
     */
    abstract fun create(config: ProcessConfig): Runnable?
}

/**
 * Interface that allows implementation to handle the result image of an operation.
 */
interface ProcessResultHandler {

    /**
     * Transfers an image from an image processing operation to the processing manager.
     *
     * @param image The image to be transferred.
     */
    fun transferImage(image: Bitmap)
}