package com.example.plesapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

// Función para convertir ImageProxy a Bitmap
fun ImageProxy.toBitmap(): Bitmap {
    val nv21Bytes = yuv420888ToNv21(this)
    val yuvImage = YuvImage(nv21Bytes, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, this.width, this.height), 100, out)
    val jpegBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
}


// Conversión manual de YUV_420_888 a NV21
fun yuv420888ToNv21(image: ImageProxy): ByteArray {
    val yPlane = image.planes[0]
    val uPlane = image.planes[1]
    val vPlane = image.planes[2]

    val ySize = yPlane.buffer.remaining()
    val uSize = uPlane.buffer.remaining()
    val vSize = vPlane.buffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yPlane.buffer.get(nv21, 0, ySize)

    val uvStride = uPlane.rowStride
    val pixelStride = uPlane.pixelStride
    val uvHeight = uPlane.buffer.remaining() / uvStride

    var index = ySize
    for (i in 0 until uvHeight) {
        for (j in 0 until image.width / 2) {
            nv21[index++] = vPlane.buffer.get(i * vPlane.rowStride + j * pixelStride)
            nv21[index++] = uPlane.buffer.get(i * uPlane.rowStride + j * pixelStride)
        }
    }

    return nv21
}