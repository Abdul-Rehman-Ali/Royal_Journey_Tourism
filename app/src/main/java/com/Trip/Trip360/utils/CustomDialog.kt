package com.Trip.Trip360.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
