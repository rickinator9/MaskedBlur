package net.rickvisser.maskedgaussianblur.processing

import net.rickvisser.maskedgaussianblur.processing.configs.ProcessConfig

/**
 * Interface for scheduling image processing operations using a configuration.
 */
interface ImageProcessingManager {

    /**
     * Schedules an image processing operation according to the configuration supplied.
     *
     * @param config Configuration object that contains parameters useful to image processing.
     */
    fun process(config: ProcessConfig)
}