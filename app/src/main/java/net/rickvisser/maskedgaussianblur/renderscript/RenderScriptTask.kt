package net.rickvisser.maskedgaussianblur.renderscript

import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.RenderScript
import android.util.Log
import net.rickvisser.maskedgaussianblur.ui.activity.BlurView

class RenderScriptTask(private val rs: RenderScript, private val view: BlurView) : AsyncTask<Bitmap, Float, Bitmap>() {

    private var startTime: Long = 0

    override fun onPreExecute() {
        super.onPreExecute()

        startTime = System.nanoTime()
        Log.d("RENDER", "Render start: $startTime")
    }

    override fun doInBackground(vararg images: Bitmap): Bitmap {

        startTime = System.nanoTime()

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

        // Create a script.
        val script = ScriptC_MaskedBlur(rs)
        script._gMask = maskAlloc
        script._gWidth = width.toLong()
        script._gHeight = height.toLong()

        // Run the script a few times to make the image blurry.
        for (i in 0..9) {
            val tmp = runScript(script, inAlloc, outAlloc)
            outAlloc = inAlloc
            inAlloc = tmp
        }

        // Copy it to an image.
        val outImage = Bitmap.createBitmap(inImage)
        outAlloc.copyTo(outImage)

        return outImage
    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)

        // Calculate some benchmark data.
        val endTime = System.nanoTime()
        val diff = endTime - startTime
        val millis = diff.toFloat() / 10000000f

        // Log the benchmark data.
        Log.d("RENDER", "Render end: $endTime")
        Log.d("RENDER", "Time taken: $millis ms")

        // Show the image on the view.
        view.showImage(result)
    }

    private fun runScript(script: ScriptC_MaskedBlur, aIn: Allocation, aOut: Allocation): Allocation {
        script._gIn = aIn
        script.forEach_blur(aIn, aOut)

        return aOut
    }
}