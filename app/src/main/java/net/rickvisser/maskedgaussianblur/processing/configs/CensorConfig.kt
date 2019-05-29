package net.rickvisser.maskedgaussianblur.processing.configs

import android.graphics.Bitmap

/**
 * Class that represents a configuration for image processing based on censoring (part of) the image.
 *
 * @param image The image that is to be censored.
 * @param censor The image that is to be utilized for pixels that should be censored.
 * @param mask The parts of the image that should be censored. Any pixel that is not black (#00000000)
 * will be censored.
 */
class CensorConfig(
        val image: Bitmap,
        val censor: Bitmap,
        val mask: Bitmap
) : ProcessConfig(ProcessType.CENSOR)