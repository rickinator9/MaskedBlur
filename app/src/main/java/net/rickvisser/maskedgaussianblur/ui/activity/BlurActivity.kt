package net.rickvisser.maskedgaussianblur.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_blur.*
import net.rickvisser.maskedgaussianblur.R
import net.rickvisser.maskedgaussianblur.processing.DefaultImageProcessingManager
import net.rickvisser.maskedgaussianblur.processing.configs.BlurConfig


class BlurActivity : FragmentActivity(), BlurView {
    companion object {
        val PICK_IMAGE = 1234
    }

    private var image: Bitmap? = null
    private var imageProcessingManager: DefaultImageProcessingManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_blur)

        imageProcessingManager = DefaultImageProcessingManager(this,this)

        selectImageButton.setOnClickListener { initiateImageSelectAction() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If the request code is not ours as expected, ignore this result.
        if (requestCode != PICK_IMAGE) return

        // If the result code is not successful, show an error.
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "ResultCode is not OK!", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the bitmap from the result data and set it to the imageview.
        val uri = data?.data ?: return
        image = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        // Apply blur and set it to the image view.
        image?.let { applyBlurToImage(it) }
    }

    private fun initiateImageSelectAction() {
        showImageSelectView()
    }

    private fun showImageSelectView() {
        val intent = Intent().also {
            it.type = "image/*"
            it.action = Intent.ACTION_PICK
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    override fun showImage(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
    }

    private fun applyBlurToImage(image: Bitmap) {
        val width = Math.round(image.width / 4f)
        val height = Math.round(image.height / 4f)
        var scaledBitmap = Bitmap.createScaledBitmap(image, width, height, false)

        var mask = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val black = Color.parseColor("#000000")
        for (x in 0 until width/2) {
            for (y in 0 until height / 2) {
                mask.setPixel(x, y, black)
            }
        }

        imageProcessingManager?.process(BlurConfig(scaledBitmap, mask))
    }
}

interface BlurView {
    fun showImage(image: Bitmap)
}