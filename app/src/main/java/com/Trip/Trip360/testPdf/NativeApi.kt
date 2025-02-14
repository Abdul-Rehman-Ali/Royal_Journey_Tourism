package com.Trip.Trip360.testPdf

import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.widget.ImageView
import android.widget.TextView
import com.Trip.Trip360.InvoiceActivity
import com.Trip.Trip360.PrivateInvoiceActivity
import com.Trip.Trip360.R
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.utils.PdfGenCallback
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_COLOR
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_LOCAL_FILE_PATH
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_EMAIL
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_PHONE_NO
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_URL
import com.Trip.Trip360.utils.SharedPrefUtils.getValue
import java.io.File
import java.io.FileOutputStream

object NativeApi {
    fun generateInvoicePdf(
        context: InvoiceActivity,
        selectedTemplate: Int,
        booking: Invoice,
        callback: PdfGenCallback
    ) {
        // Inflate the XML layout
        val view = LayoutInflater.from(context).inflate(selectedTemplate, null)

        Log.d("TestingPdfCreation", "In Native API")

        (view.findViewById<TextView>(R.id.tvGuestName)).text = booking.name
        (view.findViewById<TextView>(R.id.tvPickupTime)).text = booking.pickupTime
        (view.findViewById<TextView>(R.id.tvPhoneNo)).text = booking.phone
        (view.findViewById<TextView>(R.id.tvAddon)).text = booking.additionalAddon
        (view.findViewById<TextView>(R.id.tvBookingDate)).text = booking.pickupDate
        (view.findViewById<TextView>(R.id.tvGrandTotal)).text = "AED " + calculateTotalPrice(booking)
        (view.findViewById<TextView>(R.id.tvPackageName)).text = booking.packageName ?: "N/A"
        (view.findViewById<TextView>(R.id.tvPickupLocation)).text = booking.pickupLocation ?: "N/A"
        (view.findViewById<TextView>(R.id.tvAdultCount)).text = booking.noOfAdults?.toString() ?: "0"
        (view.findViewById<TextView>(R.id.tvKidsCount)).text = booking.noOfKids?.toString() ?: "0"
        (view.findViewById<TextView>(R.id.tvPricePerKid)).text = booking.pkgPricePerKid?.toString() ?: "0.00"
        (view.findViewById<TextView>(R.id.tvPricePerAdult)).text = booking.pkgPricePerAdult?.toString() ?: "0.00"
        (view.findViewById<TextView>(R.id.tvTotalOnAdults)).text = calculateTotalPriceForAdults(booking).toString()
        (view.findViewById<TextView>(R.id.tvTotalOnKids)).text = calculateTotalPriceForKids(booking).toString()
        (view.findViewById<TextView>(R.id.tvPaymentStatus)).text = if (booking.paymentStatus) "Paid" else "Payment on Arrival"

        val phoneNo = getValue(context, KEY_PHONE_NO, "")
        val phone = view.findViewById<TextView>(R.id.tvPhoneFooter)
        phone.text = phoneNo

        val webName = getValue(context, KEY_WEB_URL, "")
        val web = view.findViewById<TextView>(R.id.tvWebNameFooter)
        web.text = webName

        val email = getValue(context, KEY_EMAIL, "")
        val Email = view.findViewById<TextView>(R.id.tvWebUrlFooter)
        Email.text = email

        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF") // Default to white
        val colorInt = android.graphics.Color.parseColor(dynamicColor) // Convert hex to color int

        //  Set color for the Table Header
        val tableHeader = view.findViewById<View>(R.id.tableHeader)
        tableHeader?.setBackgroundColor(colorInt)
        //  Set color for the Material Dividers
        val materialDivider1 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider1)
        val materialDivider2 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider2)
        materialDivider1?.dividerColor = colorInt
        materialDivider2?.dividerColor = colorInt

        //  Set color for LinearLayout
        val linearLayout000 = view.findViewById<View>(R.id.linearLayout000)
        linearLayout000?.setBackgroundColor(colorInt)
        //  Set color for TextView (Foreground Color)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView?.setTextColor(colorInt) // Changes text color instead of background

        //  Load the logo from SharedPreferences
        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
        Log.d("InvoiceActivity", "Retrieved logo path: $KEY_LOGO_LOCAL_FILE_PATH")

        val imgHeader = view.findViewById<ImageView>(R.id.imageView)

        if (logoLocalPath.isNotEmpty()) {
            val file = File(logoLocalPath)
            if (file.exists()) {
                imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))
            } else {
                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
            }
        } else {
            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
        }

        //  Define page size
        val pageWidth = 1000
        val pageHeight = 1300

        //  Measure & layout the view
        view.measure(
            MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, pageWidth, pageHeight)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        //  Draw the full layout
        view.draw(canvas)

        pdfDocument.finishPage(page)

        //  Define the Trip360 folder in Documents and ensure it exists
        val trip360Dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "Trip360"
        )
        if (!trip360Dir.exists()) {
            trip360Dir.mkdirs()
        }

        //  Generate filename with Name + Timestamp
        val timestamp = System.currentTimeMillis()
        val sanitizedFileName = booking.name.replace("[^a-zA-Z0-9]".toRegex(), "_") // Replace invalid characters
        val fileName = "${sanitizedFileName}_$timestamp.pdf"

        val pdfFile = File(trip360Dir, fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            Log.d("TestingPdfCreation", "File saved: ${pdfFile.absolutePath}")
            callback.onPdfGenerated(pdfFile.absolutePath)
        } catch (e: Exception) {
            callback.onFailure(e.message.toString())
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }


    fun generatePrivateInvoicePdf(
        context: PrivateInvoiceActivity,
        selectedTemplate: Int,
        booking: Invoice,
        callback: PdfGenCallback
    ) {
        // Inflate the XML layout
        val view = LayoutInflater.from(context).inflate(selectedTemplate, null)

        Log.d("TestingPdfCreation", "In Native API")

        (view.findViewById<TextView>(R.id.tvGuestName)).text = booking.name
        (view.findViewById<TextView>(R.id.tvPickupTime)).text = booking.pickupTime
        (view.findViewById<TextView>(R.id.tvBookingDate)).text = booking.pickupDate
        (view.findViewById<TextView>(R.id.tvPhoneNo)).text = booking.phone
        (view.findViewById<TextView>(R.id.tvAddon)).text = booking.additionalAddon
        (view.findViewById<TextView>(R.id.tvGrandTotal)).text = "AED " + calculateTotalPrice(booking)
        (view.findViewById<TextView>(R.id.tvPackageName)).text = booking.packageName ?: "N/A"
        (view.findViewById<TextView>(R.id.tvPickupLocation)).text = booking.pickupLocation ?: "N/A"
        (view.findViewById<TextView>(R.id.tvDropOffLocation)).text = booking.dropLocation ?: "N/A"
        (view.findViewById<TextView>(R.id.tvAdultCount)).text = booking.noOfAdults?.toString() ?: "0"
        (view.findViewById<TextView>(R.id.tvKidsCount)).text = booking.noOfKids?.toString() ?: "0"
        (view.findViewById<TextView>(R.id.tvPricePerKid)).text = booking.pkgPricePerKid?.toString() ?: "0.00"
        (view.findViewById<TextView>(R.id.tvPricePerAdult)).text = booking.pkgPricePerAdult?.toString() ?: "0.00"
        (view.findViewById<TextView>(R.id.tvTotalOnAdults)).text = calculateTotalPriceForAdults(booking).toString()
        (view.findViewById<TextView>(R.id.tvTotalOnKids)).text = calculateTotalPriceForKids(booking).toString()
        (view.findViewById<TextView>(R.id.tvNoOfPeople)).text = booking.noOfPeople.toString()
        (view.findViewById<TextView>(R.id.tvPaymentStatus)).text = if (booking.paymentStatus) "Paid" else "Payment on Arrival"

        val phoneNo = getValue(context, KEY_PHONE_NO, "")
        val phone = view.findViewById<TextView>(R.id.tvPhoneFooter)
        phone.text = phoneNo

        val webName = getValue(context, KEY_WEB_URL, "")
        val web = view.findViewById<TextView>(R.id.tvWebNameFooter)
        web.text = webName

        val email = getValue(context, KEY_EMAIL, "")
        val Email = view.findViewById<TextView>(R.id.tvWebUrlFooter)
        Email.text = email

        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF") // Default to white
        val colorInt = android.graphics.Color.parseColor(dynamicColor) // Convert hex to color int

        //  Set color for the Table Header
        val tableHeader = view.findViewById<View>(R.id.tableHeader)
        tableHeader?.setBackgroundColor(colorInt)
        //  Set color for the Material Dividers
        val materialDivider1 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider1)
        val materialDivider2 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider2)
        materialDivider1?.dividerColor = colorInt
        materialDivider2?.dividerColor = colorInt

        //  Set color for LinearLayout
        val linearLayout000 = view.findViewById<View>(R.id.linearLayout000)
        linearLayout000?.setBackgroundColor(colorInt)
        //  Set color for TextView (Foreground Color)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView?.setTextColor(colorInt) // Changes text color instead of background

        //  Load the logo from SharedPreferences
        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
        Log.d("InvoiceActivity", "Retrieved logo path: $KEY_LOGO_LOCAL_FILE_PATH")

        val imgHeader = view.findViewById<ImageView>(R.id.imageView)

        if (logoLocalPath.isNotEmpty()) {
            val file = File(logoLocalPath)
            if (file.exists()) {
                imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))
            } else {
                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
            }
        } else {
            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
        }

        //  Define page size
        val pageWidth = 1000
        val pageHeight = 1300

        //  Measure & layout the view
        view.measure(
            MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, pageWidth, pageHeight)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        //  Draw the full layout
        view.draw(canvas)

        pdfDocument.finishPage(page)

        //  Define the Trip360 folder in Documents and ensure it exists
        val trip360Dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "Trip360"
        )
        if (!trip360Dir.exists()) {
            trip360Dir.mkdirs()
        }

        //  Generate filename with Name + Timestamp
        val timestamp = System.currentTimeMillis()
        val sanitizedFileName = booking.name.replace("[^a-zA-Z0-9]".toRegex(), "_") // Replace invalid characters
        val fileName = "${sanitizedFileName}_$timestamp.pdf"

        val pdfFile = File(trip360Dir, fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            Log.d("TestingPdfCreation", "File saved: ${pdfFile.absolutePath}")
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
}