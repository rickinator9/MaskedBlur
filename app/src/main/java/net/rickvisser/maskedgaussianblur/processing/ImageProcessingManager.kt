package net.rickvisser.maskedgaussianblur.processing

import net.rickvisser.maskedgaussianblur.processing.configs.ProcessConfig

interface ImageProcessingManager {
    fun process(config: ProcessConfig)
}