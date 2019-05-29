package net.rickvisser.maskedgaussianblur.processing.configs

import android.graphics.Bitmap

class BlurConfig(
        val image: Bitmap,
        val mask: Bitmap
) : ProcessConfig(ProcessType.BLUR)