package com.fyp.agrifarm.app.crops

import android.graphics.*
import android.media.Image
import com.fyp.agrifarm.app.log
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


object CameraUtils {

    @Throws(Exception::class)
    fun jpegImageToBitmap(image: Image) : Bitmap {
        try {
            val buffer: ByteBuffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
        } catch (e: Exception) {
            throw(e)
        }
    }

    fun yuvImageToBitmap(image: Image): Bitmap? {
        log("FORMAT IS ${image.format}")
        try {
            val yBuffer = image.planes[0].buffer // Y
            val uBuffer = image.planes[1].buffer // U
            val vBuffer = image.planes[2].buffer // V

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            //U and V are swapped
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
            val imageBytes = out.toByteArray()
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Crop Bitmap Center
    fun cropCenter(bitmap: Bitmap) : Bitmap {
        lateinit var cropped: Bitmap
        if (bitmap.width >= bitmap.height){
            cropped = Bitmap.createBitmap(
                    bitmap,
                    bitmap.width/2 - bitmap.height/2,
                    0,
                    bitmap.height,
                    bitmap.height
            );

        }else{
            cropped = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.height/2 - bitmap.width/2,
                    bitmap.width,
                    bitmap.width
            )
        }
        return cropped
    }
}