package net.rickvisser.maskedgaussianblur.processing.creators

import android.content.Context
import android.graphics.Bitmap
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.RenderScript
import net.rickvisser.maskedgaussianblur.benchmark.Timer
import net.rickvisser.maskedgaussianblur.processing.configs.CensorConfig
import net.rickvisser.maskedgaussianblur.processing.configs.ProcessConfig
import net.rickvisser.maskedgaussianblur.processing.configs.ProcessType
import net.rickvisser.maskedgaussianblur.renderscript.ScriptC_MaskedCensor

/**
 * Class that creates operations for image processing based on censoring.
 */
class CensorProcessCreator(context: Context, resultHandler: ProcessResultHandler): ProcessCreator(resultHandler) {

    /** Renderscript object required to do many interactions with the Renderscript API. */
    private val renderscript: RenderScript

    /** Censor script object to communicate with C99 code. */
    private val script: ScriptC_MaskedCensor

    init {
        renderscript = RenderScript.create(context)
        script = ScriptC_MaskedCensor(renderscript)
    }

    /**
     * Creates a operation based on the specified config.
     *
     * @param config The config that defines the parameters of the operation.
     * @return A Runnable operation for image processing if this config can be handled by the object.
     * Null otherwise.
     */
    override fun create(config: ProcessConfig): Runnable? {
        // Make sure that the config is of the correct type.
        if (config.type != ProcessType.CENSOR) return null

        // Get the config variables.
        val censorProcessConfig = config as CensorConfig
        val image = censorProcessConfig.image
        val censor = censorProcessConfig.censor
        val mask = censorProcessConfig.mask

        return CensorRunnable(renderscript, script, image, censor, mask, resultHandler)
    }
}

/**
 * Class to perform a censor operation.
 */
private class CensorRunnable(
        private val rs: RenderScript,
        private val script: ScriptC_MaskedCensor,
        private val image: Bitmap,
        private val censor: Bitmap,
        private val mask: Bitmap,
        private val resultHandler: ProcessResultHandler
) : Runnable {
    private val timer: Timer = Timer()

    override fun run() {
        timer.start()

        // Define the image to use as input.
        val inImage = image

        // Create an allocation from the input image.
        var inAlloc = Allocation.createFromBitmap(rs, inImage)

        // Create an allocation from the censor image.
        val censorAlloc = Allocation.createFromBitmap(rs, censor)

        // Create an allocation from the mask.
        val maskAlloc = Allocation.createFromBitmap(rs, mask)

        // Create an allocation for the output image.
        var outAlloc = Allocation.createFromBitmap(rs, inImage)

        timer.recordTime("Allocation initialization")

        script._gSampled = censorAlloc
        script._gMask = maskAlloc

        timer.recordTime("Script assignation")

        // Run the script once to apply the censor.
        outAlloc = runScript(script, inAlloc, outAlloc)

        timer.recordTime("Blur loop")

        // Copy it to an image.
        val outImage = Bitmap.createBitmap(inImage.width, inImage.height, inImage.config)
        outAlloc.copyTo(outImage)

        timer.recordTime("Blur output")

        resultHandler.transferImage(outImage)
    }

    private fun runScript(script: ScriptC_MaskedCensor, aIn: Allocation, aOut: Allocation): Allocation {
        script._gIn = aIn
        script.forEach_blur(aIn, aOut)

        return aOut
    }
}