package com.Trip.Trip360.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.Trip.Trip360.utils.PdfUtilsKt.inspectAppExternalDir
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

object CustomDialog {
    private var currentDialog: AlertDialog? = null

    fun showMessageDialog(message: String, title: String, context: Context) {
        currentDialog?.apply {
            if (isShowing) {
                dismiss()
            }
        }

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        currentDialog = dialog
        dialog.show()
    }

    fun MessageDialog(
        message: String?,
        title: String?,
        context: Context,
        pdfFilePath: String?
    ) {
        if (currentDialog != null && currentDialog!!.isShowing) {
            currentDialog!!.dismiss()
        }

        val dialogBuilder = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        if (!pdfFilePath.isNullOrEmpty()) {
            dialogBuilder.setNegativeButton("Open PDF") { dialog, _ ->
                openPdfWithChooser(context, pdfFilePath)
                dialog.dismiss()
            }
        }

        currentDialog = dialogBuilder.create()
        currentDialog!!.show()
    }

    private fun openPdfWithChooser(context: Context, pdfFilePath: String) {
        inspectAppExternalDir(context)

        val file = File(pdfFilePath) // Convert filePath to a File object
        if (!file.exists()) {
            Toast.makeText(context, "File does not exist at: $pdfFilePath", Toast.LENGTH_SHORT).show()
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


    fun showConfirmationDialog(
        message: String,
        title: String,
        context: Context,
        onProceed: () -> Unit // Lambda function that will be called when the user proceeds
    ) {
        // Dismiss the existing dialog if it's showing
        currentDialog?.apply {
            if (isShowing) {
                dismiss()
            }
        }

        // Create a new dialog
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Proceed") { _, _ ->
                // When Proceed is clicked, invoke the onProceed lambda
                onProceed.invoke()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // Dismiss the dialog when Cancel is clicked
                dialog.dismiss()
            }
            .create()

        // Show the dialog
        currentDialog = dialog
        dialog.show()
    }
}
