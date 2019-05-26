package net.rickvisser.maskedgaussianblur.renderscript

import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.RenderScript
import android.util.Log
import net.rickvisser.maskedgaussianblur.benchmark.Timer
import net.rickvisser.maskedgaussianblur.ui.activity.BlurView

class RenderScriptTask(private val rs: RenderScript, private val view: BlurView) : AsyncTask<Bitmap, Float, Bitmap>() {
    companion object {
        private var script: ScriptC_MaskedBlur? = null
    }

    private var timer: Timer = Timer()

    override fun onPreExecute() {
        super.onPreExecute()

        timer.start()
    }

    override fun doInBackground(vararg images: Bitmap): Bitmap {
        timer.recordTime("Thread initialization")

        // Define the image to use as input.
        val inImage = images[0]
        val width = inImage.width
        val height = inImage.height

        // Get the image to use as a mask.
        val mask = images[1]

        // Create an allocation from the input image.
        var inAlloc = Allocation.createFromBitmap(rs, inImage)

        // Create an allocation from the mask.
        val maskAlloc = Allocation.createFromBitmap(rs, mask)

        // Create an allocation for the output image.
        var outAlloc = Allocation.createFromBitmap(rs, inImage)

        timer.recordTime("Allocation initialization")

        // Create a script.
        if (script == null) {
            script = ScriptC_MaskedBlur(rs)
        }

        timer.recordTime("Script initialization")

        script!!._gMask = maskAlloc
        script!!._gWidth = width.toLong()
        script!!._gHeight = height.toLong()

        timer.recordTime("Script assignation")

        // Run the script a few times to make the image blurry.
        for (i in 0..9) {
            val tmp = runScript(script!!, inAlloc, outAlloc)
            outAlloc = inAlloc
            inAlloc = tmp
        }

        timer.recordTime("Blur loop")

        // Copy it to an image.
        val outImage = Bitmap.createBitmap(inImage)
        outAlloc.copyTo(outImage)

        timer.recordTime("Blur output")

        return outImage
    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)

        // Show the image on the view.
        view.showImage(result)

        timer.stop()
    }

    private fun runScript(script: ScriptC_MaskedBlur, aIn: Allocation, aOut: Allocation): Allocation {
        script._gIn = aIn
        script.forEach_blur(aIn, aOut)

        return aOut
    }
}