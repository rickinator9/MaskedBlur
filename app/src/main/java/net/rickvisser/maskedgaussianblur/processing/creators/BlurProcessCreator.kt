package net.rickvisser.maskedgaussianblur.processing.creators

import android.content.Context
import android.graphics.Bitmap
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.RenderScript
import net.rickvisser.maskedgaussianblur.benchmark.Timer
import net.rickvisser.maskedgaussianblur.processing.configs.BlurConfig
import net.rickvisser.maskedgaussianblur.processing.configs.ProcessConfig
import net.rickvisser.maskedgaussianblur.processing.configs.ProcessType
import net.rickvisser.maskedgaussianblur.renderscript.ScriptC_MaskedBlur

class BlurProcessCreator(context: Context, private val resultHandler: ProcessResultHandler): ProcessCreator {

    private val renderscript: RenderScript
    private val script: ScriptC_MaskedBlur

    init {
        renderscript = RenderScript.create(context)
        script = ScriptC_MaskedBlur(renderscript)
    }

    override fun create(config: ProcessConfig): Runnable? {
        // Make sure that the config is of the correct type.
        if (config.type != ProcessType.BLUR) return null

        // Get the config variables.
        val blurProcessConfig = config as BlurConfig
        val image = blurProcessConfig.image
        val mask = blurProcessConfig.mask

        return BlurRunnable(renderscript, script, image, mask, resultHandler)
    }
}

private class BlurRunnable(
        private val rs: RenderScript,
        private val script: ScriptC_MaskedBlur,
        private val image: Bitmap,
        private val mask: Bitmap,
        private val resultHandler: ProcessResultHandler
) : Runnable {
    private val timer: Timer = Timer()

    override fun run() {
        timer.start()

        // Define the image to use as input.
        val inImage = image
        val width = inImage.width
        val height = inImage.height

        // Create an allocation from the input image.
        var inAlloc = Allocation.createFromBitmap(rs, inImage)

        // Create an allocation from the mask.
        val maskAlloc = Allocation.createFromBitmap(rs, mask)

        // Create an allocation for the output image.
        var outAlloc = Allocation.createFromBitmap(rs, inImage)

        timer.recordTime("Allocation initialization")

        script._gMask = maskAlloc
        script._gWidth = width.toLong()
        script._gHeight = height.toLong()

        timer.recordTime("Script assignation")

        // Run the script a few times to make the image blurry.
        for (i in 0..9) {
            val tmp = runScript(script, inAlloc, outAlloc)
            outAlloc = inAlloc
            inAlloc = tmp
        }

        timer.recordTime("Blur loop")

        // Copy it to an image.
        val outImage = Bitmap.createBitmap(inImage.width, inImage.height, inImage.config)
        outAlloc.copyTo(outImage)

        timer.recordTime("Blur output")

        resultHandler.transferImage(outImage)
    }

    private fun runScript(script: ScriptC_MaskedBlur, aIn: Allocation, aOut: Allocation): Allocation {
        script._gIn = aIn
        script.forEach_blur(aIn, aOut)

        return aOut
    }
}