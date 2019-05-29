package net.rickvisser.maskedgaussianblur.processing.configs

import android.graphics.Bitmap

/**
 * Class that represents a configuration for image processing based on blurring (part of) the image.
 *
 * @param image The image that is to be blurred.
 * @param mask The parts of the image that should be blurred. Any pixel that is not black (#00000000)
 * will be blurred.
 */
class BlurConfig(
        val image: Bitmap,
        val mask: Bitmap
) : ProcessConfig(ProcessType.BLUR)