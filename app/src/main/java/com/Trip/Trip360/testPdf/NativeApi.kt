package com.Trip.Trip360.testPdf

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.widget.ImageView
import android.widget.TextView
import com.Trip.Trip360.R
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.utils.PdfGenCallback
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_LOCAL_FILE_PATH
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_PHONE_NO
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_NAME
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_URL
import com.Trip.Trip360.utils.SharedPrefUtils.getValue
import java.io.File
import java.io.FileOutputStream

object NativeApi {
    fun generateInvoicePdf(context: Context, selectedTemplate: Int, booking: Invoice, callback: PdfGenCallback) {
        // Inflate the XML layout
        var paymentStatus = "N/A"
        val view = LayoutInflater.from(context).inflate(selectedTemplate, null)

        Log.d("TestingPdfCreation", "In Native Api")

        (view.findViewById<View>(R.id.tvGuestName) as TextView).text =
            booking.name
        (view.findViewById<View>(R.id.tvPickupTime) as TextView).text =
            booking.pickupTime
        (view.findViewById<View>(R.id.tvBookingDate) as TextView).text =
            booking.pickupDate
        (view.findViewById<View>(R.id.tvGrandTotal) as TextView).text =
            "AED " + calculateTotalPrice(booking)
        (view.findViewById<View>(R.id.tvPackageName) as TextView).text =
            booking.packageName ?: "N/A"
        (view.findViewById<View>(R.id.tvPickupLocation) as TextView).text =
            booking.pickupLocation ?: "N/A"
        (view.findViewById<View>(R.id.tvAdultCount) as TextView).text =
            if (booking.noOfAdults != null) booking.noOfAdults.toString() else "0"
        (view.findViewById<View>(R.id.tvKidsCount) as TextView).text =
            if (booking.noOfKids != null) booking.noOfKids.toString() else "0"
        (view.findViewById<View>(R.id.tvPricePerKid) as TextView).text =
            if (booking.pkgPricePerKid != null) booking.pkgPricePerKid.toString() else "0.00"
        (view.findViewById<View>(R.id.tvPricePerAdult) as TextView).text =
            if (booking.pkgPricePerAdult != null) booking.pkgPricePerAdult.toString() else "0.00"
        (view.findViewById<View>(R.id.tvTotalOnAdults) as TextView).text =
            calculateTotalPriceForAdults(booking).toString()
        (view.findViewById<View>(R.id.tvTotalOnKids) as TextView).text =
            calculateTotalPriceForKids(booking).toString()


        if (booking.paymentStatus) {
            paymentStatus = "Paid"
        } else {
            paymentStatus = "Payment on Arrival"
        }

        (view.findViewById<View>(R.id.tvPaymentStatus) as TextView).setText(paymentStatus)


        (view.findViewById<View>(R.id.tvPhoneFooter) as TextView).text =
            getValue<String>(context, KEY_PHONE_NO, "")
        (view.findViewById<View>(R.id.tvWebNameFooter) as TextView).text =
            getValue<String>(context, KEY_WEB_NAME, "")
        (view.findViewById<View>(R.id.tvWebUrlFooter) as TextView).text =
            getValue<String>(context, KEY_WEB_URL, "")


        val logoImageView: ImageView =
            view.findViewById<ImageView>(R.id.imageView)

        val drawable =
            Drawable.createFromPath(getValue<String>(context, KEY_LOGO_LOCAL_FILE_PATH, ""))
        logoImageView.setImageDrawable(drawable)

        val pageWidth = 1000
        val pageHeight = 1300

        view.measure(
            MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, pageWidth, pageHeight)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
//        canvas.scale(
//            pageWidth.toFloat() / invoiceView.width,  // Scale width
//            pageHeight.toFloat() / invoiceView.height // Scale height
//        )
        view.draw(canvas)

        // Finish the page
        pdfDocument.finishPage(page)

        var fileName = booking.name + "_" + booking.pickupDate
        fileName = fileName.replace("[^a-zA-Z0-9_\\-.]".toRegex(), "-")

        val pdfFile = File(context.getExternalFilesDir(null), fileName)

        // Save the PDF to storage
//        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//        val file = File(directory, "InvoiceNative33.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            println("PDF saved to: ${pdfFile.absolutePath}")
            Log.d("TestingPdfCreation", "File path: ${pdfFile.absolutePath}")
            callback.onPdfGenerated(pdfFile.absolutePath)
        } catch (e: Exception) {
            callback.onFailure(e.message.toString())
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }


    fun calculateTotalPrice(booking: Invoice): Double {
        return calculateTotalPriceForAdults(booking) + calculateTotalPriceForKids(booking)
    }

    private fun calculateTotalPriceForAdults(booking: Invoice): Double {
        if (booking.noOfAdults != null && booking.pkgPricePerAdult != null) {
            return booking.noOfAdults * booking.pkgPricePerAdult
        }
        return 0.00
    }

    private fun calculateTotalPriceForKids(booking: Invoice): Double {
        if (booking.noOfKids != null && booking.pkgPricePerKid != null) {
            return booking.noOfKids * booking.pkgPricePerKid
        }
        return 0.00
    }



//    fun generateInvoicePdf(layoutInflater: LayoutInflater) {
//        // Inflate the XML layout
//        val invoiceView = layoutInflater.inflate(R.layout.invoice_layout_1, null)
//
//        // Set dynamic data (if needed)
//        invoiceView.findViewById<TextView>(R.id.tvGuestName).text = "Customer: Janny"
//        invoiceView.findViewById<TextView>(R.id.tvBookingDate).text = "Date: 01/24/2025"
//        invoiceView.findViewById<TextView>(R.id.tvGrandTotal).text = "Total: $150.00"
//
//        // Define PDF page size (A4 size: 595x842 points)
//        val pageWidth = 1000
//        val pageHeight = 1300
//
//        // Measure and layout the inflated view to match the PDF dimensions
//        invoiceView.measure(
//            MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY),
//            MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.EXACTLY)
//        )
//        invoiceView.layout(0, 0, pageWidth, pageHeight)
//
//        // Create a PdfDocument
//        val pdfDocument = PdfDocument()
//        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
//        val page = pdfDocument.startPage(pageInfo)
//
//        // Render the view onto the PDF canvas
//        val canvas = page.canvas
////        canvas.scale(
////            pageWidth.toFloat() / invoiceView.width,  // Scale width
////            pageHeight.toFloat() / invoiceView.height // Scale height
////        )
//        invoiceView.draw(canvas)
//
//        // Finish the page
//        pdfDocument.finishPage(page)
//
//        // Save the PDF to storage
//        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//        val file = File(directory, "InvoiceNative33.pdf")
//        try {
//            pdfDocument.writeTo(FileOutputStream(file))
//            println("PDF saved to: ${file.absolutePath}")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            pdfDocument.close()
//        }
//    }
}
