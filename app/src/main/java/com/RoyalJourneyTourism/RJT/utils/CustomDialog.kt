package com.RoyalJourneyTourism.RJT.utils

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
}
