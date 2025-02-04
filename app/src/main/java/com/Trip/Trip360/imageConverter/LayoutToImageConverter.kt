package com.Trip.Trip360.imageConverter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import java.io.File
import java.io.FileOutputStream

object LayoutToImageConverter {

    fun saveLayoutAsImage(context: Context, layoutResId: Int, outputFile: File) {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(layoutResId, null)

        // Measure and layout the view
        view.measure(
            View.MeasureSpec.makeMeasureSpec(982, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1347, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        // Create bitmap and canvas
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        // Save bitmap as PNG
        try {
            val outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
