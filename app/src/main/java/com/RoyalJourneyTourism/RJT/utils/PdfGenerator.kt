package com.RoyalJourneyTourism.RJT.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.RoyalJourneyTourism.RJT.R
import com.RoyalJourneyTourism.RJT.data.Booking
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    fun generateInvoicePdf(booking: Booking, context: Context) {
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.invoice_layout, null)

            view.findViewById<TextView>(R.id.tvGuestName).text = "Customer Name: ${booking.name}"
            view.findViewById<TextView>(R.id.tvPickupTime).text = "Customer Name: ${booking.pickupTime}"
            view.findViewById<TextView>(R.id.tvBookingDate).text = "Customer Name: ${booking.pickupDate}"
            view.findViewById<TextView>(R.id.tvGrandTotal).text = "Total Price: ${calculateTotalPrice(booking)}"
            view.findViewById<TextView>(R.id.tvPackageName).text = "Total Price: ${calculateTotalPrice(booking)}"
            view.findViewById<TextView>(R.id.tvPickupLocation).text = "Total Price: ${calculateTotalPrice(booking)}"
            view.findViewById<TextView>(R.id.tvAdultCount).text = "Total Price: ${calculateTotalPrice(booking)}"
            view.findViewById<TextView>(R.id.tvKidsCount).text = "Total Price: ${calculateTotalPrice(booking)}"
            view.findViewById<TextView>(R.id.tvPricePerKid).text = "Total Price: ${calculateTotalPrice(booking)}"
            view.findViewById<TextView>(R.id.tvPricePerAdult).text = "Total Price: ${calculateTotalPrice(booking)}"
            view.findViewById<TextView>(R.id.tvTotalOnAdults).text = "Total Price: ${calculateTotalPrice(booking)}"
            view.findViewById<TextView>(R.id.tvTotalOnKids).text = "Total Price: ${calculateTotalPrice(booking)}"

            val width = 595
            val height = 842
            view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, width, height)

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)

            // Save the PDF file
            val filePath = File(context.filesDir, "Invoice.pdf")
            pdfDocument.writeTo(FileOutputStream(filePath))
            pdfDocument.close()

            Toast.makeText(context, "Invoice saved at ${filePath.absolutePath}", Toast.LENGTH_SHORT).show()

//            openPdf(context, filePath)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PdfDebugger", "Failed to generate invoice: ${e.message}")
        }
    }

    private fun calculateTotalPrice(booking: Booking): Double {
        val adultCost = (booking.noOfAdults ?: 0) * (booking.pkgPricePerAdult ?: 0.0)
        val kidCost = (booking.noOfKids ?: 0) * (booking.pkgPricePerKid ?: 0.0)
        return adultCost + kidCost
    }

    private fun openPdf(context: Context, pdfFile: File) {
        try {
            if (pdfFile.exists()) {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)
                } else {
                    Uri.fromFile(pdfFile)
                }

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                context.startActivity(Intent.createChooser(intent, "Open PDF"))
            } else {
                Toast.makeText(context, "PDF file not found!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PdfDebugger", "Failed to open PDF: ${e.message}")
            Toast.makeText(context, "Error opening PDF.", Toast.LENGTH_SHORT).show()
        }
    }
}
