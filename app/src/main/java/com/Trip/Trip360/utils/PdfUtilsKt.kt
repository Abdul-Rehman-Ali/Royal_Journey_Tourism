package com.Trip.Trip360.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
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

    fun viewPdf(filePath: String, context: Context) {

        inspectAppExternalDir(context)

        val file = File(filePath) // Convert filePath to a File object
        if (!file.exists()) {
            Toast.makeText(context, "File does not exist at: $filePath", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission to read the file
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No application to view PDF", Toast.LENGTH_SHORT).show()
        }
    }


    fun inspectAppExternalDir(context: Context) {
        // Get the app's external files directory
        val externalDir: File? = context.getExternalFilesDir(null) // null = root of external files directory

        if (externalDir != null && externalDir.exists()) {
            Log.d("ExternalDirInspector", "App's External Directory: ${externalDir.absolutePath}")
            listFilesRecursive(externalDir, "")
        } else {
            Log.e("ExternalDirInspector", "App's external directory does not exist.")
        }
    }

    private fun listFilesRecursive(directory: File, indent: String) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    Log.d("ExternalDirInspector", "$indent Dir: ${file.name}")
                    listFilesRecursive(file, "$indent  ")
                } else {
                    Log.d("ExternalDirInspector", "$indent File: ${file.name} (Size: ${file.length()} bytes)")
                }
            }
        } else {
            Log.w("ExternalDirInspector", "$indent No files found in: ${directory.name}")
        }
    }


    fun shareInvoice(filePath: String, context: Context) {
        val file = File(filePath) // Convert filePath to a File object
        if (!file.exists()) {
            Toast.makeText(context, "File does not exist at: $filePath", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the URI for the file using FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Create a share intent
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf" // Set MIME type for PDF files
            putExtra(Intent.EXTRA_STREAM, uri) // Attach the file URI
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission to access the file
        }

        // Try to start the sharing activity
        try {
            context.startActivity(Intent.createChooser(intent, "Share PDF via"))
        } catch (e: Exception) {
            Toast.makeText(context, "No application to share PDF", Toast.LENGTH_SHORT).show()
        }
    }

}