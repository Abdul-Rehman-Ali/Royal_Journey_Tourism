package com.Trip.Trip360.imageConverter

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.Trip.Trip360.R
import java.io.File

class ImageExportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val outputDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "invoice_screenshots")
        if (!outputDir.exists()) outputDir.mkdirs()

        val layouts = listOf(
            Pair(R.layout.general_invoice_template, "general_invoice_template.png"),
//            Pair(R.layout.invoice_layout_2, "invoice_layout_2.png"),
//            Pair(R.layout.invoice_layout_3, "invoice_layout_3.png")
        )

        for ((layoutResId, fileName) in layouts) {
            val outputFile = File(outputDir, fileName)
            LayoutToImageConverter.saveLayoutAsImage(this, layoutResId, outputFile)
            Log.d("ImageExport", "Saved: ${outputFile.absolutePath}")
        }

        finish() // Close the activity once done
    }
}
