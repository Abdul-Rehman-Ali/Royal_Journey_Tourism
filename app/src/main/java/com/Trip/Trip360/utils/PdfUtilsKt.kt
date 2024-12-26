package com.Trip.Trip360.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.File

object PdfUtilsKt {

    fun showInvoicePdf(filePath: String, context: Context) {
        Log.d("fjasklfjlds", "FilePath: $filePath")
        val file = File(filePath)
        if (file.exists()) {
            val uri = Uri.fromFile(file) // Directly use Uri.fromFile
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) // Optional
            }
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No application to view PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "File not found at: $filePath", Toast.LENGTH_SHORT).show()
        }
    }
}