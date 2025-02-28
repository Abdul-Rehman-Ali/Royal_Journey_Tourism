package com.Trip.Trip360.testPdf

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.widget.ImageView
import android.widget.TextView
import com.Trip.Trip360.PrivateInvoiceActivity
import com.Trip.Trip360.R
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.utils.PdfGenCallback
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_COLOR
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_EMAIL
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_LOCAL_FILE_PATH
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_PHONE_NO
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_URL
import com.Trip.Trip360.utils.SharedPrefUtils.getValue
import java.io.File
import java.io.FileOutputStream


object NativeApi {

//    fun generateInvoicePdf(
//        context: InvoiceActivity,
//        selectedTemplate: Int,
//        termsTemplate: Int,
//        booking: Invoice,
//        callback: PdfGenCallback
//    ) {
//        val pdfDocument = PdfDocument()
//
//        // Convert desired dp values to pixels using the device's density.
//        // Adjust these dp values as needed.
//        val density = context.resources.displayMetrics.density
//        val desiredPageWidthDp = 500    // Example: 300dp width
//        val desiredPageHeightDp = 720   // Example: 450dp height
//        val pageWidth = (desiredPageWidthDp * density).toInt()
//        val pageHeight = (desiredPageHeightDp * density).toInt()
//
//        // ----- Page 1: Invoice -----
//        val invoiceView = LayoutInflater.from(context).inflate(selectedTemplate, null)
//        setupInvoiceData(invoiceView, booking, context) // Populate invoice data
//
//        // Force the invoice view to measure itself with an EXACT width so it matches the PDF page.
//        invoiceView.measure(
//            MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY),
//            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//        )
//        invoiceView.layout(0, 0, invoiceView.measuredWidth, invoiceView.measuredHeight)
//
//        // Calculate scale factor in case the measured width is different.
//        val scaleInvoice = pageWidth.toFloat() / invoiceView.measuredWidth
//
//        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
//        val page1 = pdfDocument.startPage(pageInfo1)
//        val canvas1 = page1.canvas
//        canvas1.save()
//        // Scale the canvas so that the view fits the PDF page width.
//        canvas1.scale(scaleInvoice, scaleInvoice)
//        invoiceView.draw(canvas1)
//        canvas1.restore()
//        pdfDocument.finishPage(page1)
//
//        // ----- Page 2: Terms & Conditions -----
//        val termsView = LayoutInflater.from(context).inflate(termsTemplate, null)
//        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF") // Default to white
//        val colorInt = android.graphics.Color.parseColor(dynamicColor)
//
//        // Set dynamic colors and properties for the terms view.
//        val materialDividerTerm = termsView.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDividerterm)
//        materialDividerTerm?.dividerColor = colorInt
//
//        val linearLayoutTerm = termsView.findViewById<View>(R.id.linearLayoutterm)
//        linearLayoutTerm?.setBackgroundColor(colorInt)
//
//        // Load dynamic logo.
//        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
//        val imgHeaderTerm = termsView.findViewById<ImageView>(R.id.imageViewterm)
//        if (logoLocalPath.isNotEmpty()) {
//            val file = File(logoLocalPath)
//            if (file.exists()) {
//                imgHeaderTerm.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//            } else {
//                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
//            }
//        } else {
//            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
//        }
//
//        // Set footer text values.
//        val phoneNo1 = getValue(context, KEY_PHONE_NO, "")
//        val phone1 = termsView.findViewById<TextView>(R.id.tvPhoneFooter1)
//        phone1.text = phoneNo1
//
//        val webName1 = getValue(context, KEY_WEB_URL, "")
//        val web1 = termsView.findViewById<TextView>(R.id.tvWebNameFooter1)
//        web1.text = webName1
//
//        val email1 = getValue(context, KEY_EMAIL, "")
//        val emailTextView = termsView.findViewById<TextView>(R.id.tvWebUrlFooter1)
//        emailTextView.text = email1
//
//        // Force the terms view to measure itself with an EXACT width.
//        val termsWidthSpec = MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY)
//        val termsHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//        termsView.measure(termsWidthSpec, termsHeightSpec)
//        termsView.layout(0, 0, pageWidth, termsView.measuredHeight)
//        Log.d("PDF", "TermsView measured width: ${termsView.measuredWidth}, height: ${termsView.measuredHeight}")
//
//        val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
//        val page2 = pdfDocument.startPage(pageInfo2)
//        val canvas2 = page2.canvas
//        canvas2.save()
//        // No scaling is needed if the measured width equals pageWidth.
//        termsView.draw(canvas2)
//        canvas2.restore()
//        pdfDocument.finishPage(page2)
//
//        // Save the PDF file.
//        val trip360Dir = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
//            "Trip360"
//        )
//        if (!trip360Dir.exists()) {
//            trip360Dir.mkdirs()
//        }
//        val timestamp = System.currentTimeMillis()
//        val sanitizedFileName = booking.name.replace("[^a-zA-Z0-9]".toRegex(), "_")
//        val fileName = "${sanitizedFileName}_$timestamp.pdf"
//        val pdfFile = File(trip360Dir, fileName)
//
//        try {
//            pdfDocument.writeTo(FileOutputStream(pdfFile))
//            Log.d("TestingPdfCreation", "File saved: ${pdfFile.absolutePath}")
//            callback.onPdfGenerated(pdfFile.absolutePath)
//        } catch (e: Exception) {
//            callback.onFailure(e.message.toString())
//            e.printStackTrace()
//        } finally {
//            pdfDocument.close()
//        }
//    }

    fun generateInvoicePdf(
        context: Activity,
        selectedTemplate: Int,
        termsTemplate: Int,
        booking: Invoice,
        callback: PdfGenCallback
    ) {
        val pdfDocument = PdfDocument()

        // **1. Generate Page 1 - Invoice**
        val invoiceView = LayoutInflater.from(context).inflate(selectedTemplate, null)
        setupInvoiceData(invoiceView, booking, context) // Populate invoice data

        val invoiceBitmap = renderViewToBitmap(context, invoiceView)
        addBitmapToPdf(invoiceBitmap, pdfDocument, 1)

        // **2. Generate Page 2 - Terms & Conditions**
        val termsView = LayoutInflater.from(context).inflate(termsTemplate, null)
        setupTermsView(termsView, context) // Populate terms & conditions

        val termsBitmap = renderViewToBitmap(context, termsView)
        addBitmapToPdf(termsBitmap, pdfDocument, 2)

        // **3. Save the PDF**
        val pdfDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Trip360")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val fileName = "${booking.name.replace("[^a-zA-Z0-9]".toRegex(), "_")}_${System.currentTimeMillis()}.pdf"
        val pdfFile = File(pdfDir, fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            callback.onPdfGenerated(pdfFile.absolutePath)
        } catch (e: Exception) {
            callback.onFailure(e.message.toString())
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }

    // **Helper function to render any View into a Bitmap**
    private fun renderViewToBitmap(context: Context, view: View): Bitmap {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        view.measure(
            View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, screenWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    // **Helper function to add a Bitmap as a page in PDF**
    private fun addBitmapToPdf(bitmap: Bitmap, pdfDocument: PdfDocument, pageNumber: Int) {
        val pageWidth = 500  // A4 width in pixels (8.27 inches at 72 DPI)
        val pageHeight = 842 // A4 height in pixels (11.69 inches at 72 DPI)
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        val pdfCanvas = page.canvas

        val scaleX = pageWidth.toFloat() / bitmap.width
        val scaleY = pageHeight.toFloat() / bitmap.height
        val scale = minOf(scaleX, scaleY)

        val offsetX = (pageWidth - bitmap.width * scale) / 2
        val offsetY = (pageHeight - bitmap.height * scale) / 2

        val matrix = Matrix()
        matrix.postScale(scale, scale)
        matrix.postTranslate(offsetX, offsetY)

        pdfCanvas.drawBitmap(bitmap, matrix, null)
        pdfDocument.finishPage(page)
    }

    // **Helper function to setup Terms & Conditions**
    private fun setupTermsView(termsView: View, context: Context) {
        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF")
        val colorInt = android.graphics.Color.parseColor(dynamicColor)

        termsView.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDividerterm)?.dividerColor = colorInt
        termsView.findViewById<View>(R.id.linearLayoutterm)?.setBackgroundColor(colorInt)

        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
        val imgHeaderTerm = termsView.findViewById<ImageView>(R.id.imageViewterm)
        if (logoLocalPath.isNotEmpty()) {
            val file = File(logoLocalPath)
            if (file.exists()) {
                imgHeaderTerm.setImageDrawable(Drawable.createFromPath(logoLocalPath))
            }
        }

        termsView.findViewById<TextView>(R.id.tvPhoneFooter)?.text = getValue(context, KEY_PHONE_NO, "")
        termsView.findViewById<TextView>(R.id.tvWebNameFooter)?.text = getValue(context, KEY_WEB_URL, "")
        termsView.findViewById<TextView>(R.id.tvWebUrlFooter)?.text = getValue(context, KEY_EMAIL, "")
    }




//    fun generateInvoicePdf(
//        context: InvoiceActivity,
//        selectedTemplate: Int,
//        termsTemplate: Int,
//        booking: Invoice,
//        callback: PdfGenCallback
//    ) {
//        val pdfDocument = PdfDocument()
//        val pageWidth = 1000
//        val pageHeight = 1500
//
//        // ----- Page 1: Invoice -----
//        val invoiceView = LayoutInflater.from(context).inflate(selectedTemplate, null)
//        setupInvoiceData(invoiceView, booking, context) // Populate invoice data
//
//
//        // Let the view measure itself naturally
//        invoiceView.measure(
//            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//        )
//        invoiceView.layout(0, 0, invoiceView.measuredWidth, invoiceView.measuredHeight)
//
//        // Calculate scale factor so the view fits the PDF page width
//        val scaleInvoice = pageWidth.toFloat() / invoiceView.measuredWidth
//
//        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
//        val page1 = pdfDocument.startPage(pageInfo1)
//        val canvas1 = page1.canvas
//        canvas1.save()
//        // Scale the canvas before drawing the invoice view
//        canvas1.scale(scaleInvoice, scaleInvoice)
//        invoiceView.draw(canvas1)
//        canvas1.restore()
//        pdfDocument.finishPage(page1)
//
//        // ----- Page 2: Terms & Conditions -----
//// Inflate your terms view as before
//        val termsView = LayoutInflater.from(context).inflate(termsTemplate, null)
//        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF") // Default to white
//        val colorInt = android.graphics.Color.parseColor(dynamicColor) // Convert hex to color int
//// ... (set dynamic colors, logo, text, etc.)
//        //// Set color for Material Divider on Page 2
//        val materialDividerTerm = termsView.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDividerterm)
//        materialDividerTerm?.dividerColor = colorInt
//
//// Set color for the Background Layout on Page 2
//        val linearLayoutTerm = termsView.findViewById<View>(R.id.linearLayoutterm)
//        linearLayoutTerm?.setBackgroundColor(colorInt)
//
//// Load dynamic logo on Page 2
//        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
//        val imgHeaderTerm = termsView.findViewById<ImageView>(R.id.imageViewterm)
//
//        if (logoLocalPath.isNotEmpty()) {
//            val file = File(logoLocalPath)
//            if (file.exists()) {
//                imgHeaderTerm.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//            } else {
//                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
//            }
//        } else {
//            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
//        }
//
//
//        val phoneNo1 = getValue(context, KEY_PHONE_NO, "")
//        val phone1 = termsView.findViewById<TextView>(R.id.tvPhoneFooter1)
//        phone1.text = phoneNo1
//
//        val webName1 = getValue(context, KEY_WEB_URL, "")
//        val web1 = termsView.findViewById<TextView>(R.id.tvWebNameFooter1)
//        web1.text = webName1
//
//        val email1 = getValue(context, KEY_EMAIL, "")
//        val Email1 = termsView.findViewById<TextView>(R.id.tvWebUrlFooter1)
//        Email1.text = email1
//
//
//// Measure the view: force width to match PDF page width and let height be measured naturally.
//        val termsWidthSpec = MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY)
//        val termsHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//        termsView.measure(termsWidthSpec, termsHeightSpec)
//        termsView.layout(0, 0, pageWidth, termsView.measuredHeight)
//
//// (Optional) Log the measured size for debugging
//        Log.d("PDF", "TermsView measured width: ${termsView.measuredWidth}, height: ${termsView.measuredHeight}")
//
//// If the view's measured width equals pageWidth, no scaling is needed.
//// Otherwise, calculate a scale factor:
//// val scaleTerms = pageWidth.toFloat() / termsView.measuredWidth
//
//        val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
//        val page2 = pdfDocument.startPage(pageInfo2)
//        val canvas2 = page2.canvas
//
//    // If scaling is needed (if measured width != pageWidth), then apply scaling.
//    // In this case, we expect measuredWidth to equal pageWidth because of the EXACT spec.
//        canvas2.save()
//    // canvas2.scale(scaleTerms, scaleTerms)  // Only if needed
//        termsView.draw(canvas2)
//        canvas2.restore()
//        pdfDocument.finishPage(page2)
//
//
//        // Save the PDF file
//        val trip360Dir = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
//            "Trip360"
//        )
//        if (!trip360Dir.exists()) {
//            trip360Dir.mkdirs()
//        }
//
//        val timestamp = System.currentTimeMillis()
//        val sanitizedFileName = booking.name.replace("[^a-zA-Z0-9]".toRegex(), "_")
//        val fileName = "${sanitizedFileName}_$timestamp.pdf"
//        val pdfFile = File(trip360Dir, fileName)
//
//        try {
//            pdfDocument.writeTo(FileOutputStream(pdfFile))
//            Log.d("TestingPdfCreation", "File saved: ${pdfFile.absolutePath}")
//            callback.onPdfGenerated(pdfFile.absolutePath)
//        } catch (e: Exception) {
//            callback.onFailure(e.message.toString())
//            e.printStackTrace()
//        } finally {
//            pdfDocument.close()
//        }
//    }


    // Function to populate invoice data
    private fun setupInvoiceData(view: View, booking: Invoice, context: Activity) {
        (view.findViewById<TextView>(R.id.tvGuestName)).text = booking.name
        (view.findViewById<TextView>(R.id.tvDate)).text = booking.currentDate
        (view.findViewById<TextView>(R.id.tvBookingCode)).text = booking.bookingCode
        (view.findViewById<TextView>(R.id.tvPickupTime)).text = booking.pickupTime
        (view.findViewById<TextView>(R.id.tvEmail)).text = booking.email
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

        // Fetch the background drawable and update its color
        fun updateBackground(view: View, colorInt: Int) {
            val drawable = view.background as? GradientDrawable
            drawable?.setColor(colorInt) // Update the solid color while keeping other properties
        }

        val heading = view.findViewById<View>(R.id.heading1)
        val heading2 = view.findViewById<View>(R.id.heading2)
        val heading3 = view.findViewById<View>(R.id.heading3)

// Apply the dynamic color while retaining padding & corner radius
        heading?.let { updateBackground(it, colorInt) }
        heading2?.let { updateBackground(it, colorInt) }
        heading3?.let { updateBackground(it, colorInt) }



        //  Set color for the Table Header
        val tableHeader = view.findViewById<View>(R.id.tableHeader)
        tableHeader?.let { updateBackground(it, colorInt) }
        //  Set color for the Material Dividers
        val materialDivider1 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider1)
        val materialDivider2 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider2)
        val materialDivider3 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider3)
//        val materialDividerterm = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDividerterm)
        materialDivider1?.dividerColor = colorInt
        materialDivider2?.dividerColor = colorInt
        materialDivider3?.dividerColor = colorInt
//        materialDividerterm?.dividerColor = colorInt

        //  Set color for LinearLayout
        val linearLayout000 = view.findViewById<View>(R.id.linearLayout000)
        linearLayout000?.setBackgroundColor(colorInt)

        val linearLayoutterm = view.findViewById<View>(R.id.linearLayoutterm)
        linearLayoutterm?.setBackgroundColor(colorInt)


//        val phoneNo1 = getValue(context, KEY_PHONE_NO, "")
//        val phone1 = termsView.findViewById<TextView>(R.id.tvPhoneFooter1)
//        phone1.text = phoneNo1
//
//        val webName1 = getValue(context, KEY_WEB_URL, "")
//        val web1 = termsView.findViewById<TextView>(R.id.tvWebNameFooter1)
//        web1.text = webName1
//
//        val email1 = getValue(context, KEY_EMAIL, "")
//        val Email1 = termsView.findViewById<TextView>(R.id.tvWebUrlFooter1)
//        Email1.text = email1

        //  Set color for TextView (Foreground Color)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView?.setTextColor(colorInt) // Changes text color instead of background

        //  Load the logo from SharedPreferences
        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
        Log.d("InvoiceActivity", "Retrieved logo path: $KEY_LOGO_LOCAL_FILE_PATH")

        val imgHeader = view.findViewById<ImageView>(R.id.imageView)
        val imgHeader2 = view.findViewById<ImageView>(R.id.imageViewterm)

        if (logoLocalPath.isNotEmpty()) {
            val file = File(logoLocalPath)
            if (file.exists()) {
                imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//                imgHeader2.setImageDrawable(Drawable.createFromPath(logoLocalPath))
            } else {
                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
            }
        } else {
            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
        }
    }


    fun generatePrivateInvoicePdf(
        context: PrivateInvoiceActivity,
        selectedTemplate: Int,
        termsTemplate: Int,
        booking: Invoice,
        callback: PdfGenCallback
    ) {
        val pdfDocument = PdfDocument()

        // **1. Generate Page 1 - Invoice**
        val invoiceView = LayoutInflater.from(context).inflate(selectedTemplate, null)
        generatePrivateInvoicePdf(invoiceView, booking, context) // Populate invoice data

        val invoiceBitmap = renderViewToBitmap(context, invoiceView)
        addBitmapToPdf(invoiceBitmap, pdfDocument, 1)

        // **2. Generate Page 2 - Terms & Conditions**
        val termsView = LayoutInflater.from(context).inflate(termsTemplate, null)
        setupTermsView(termsView, context) // Populate terms & conditions

        val termsBitmap = renderViewToBitmap(context, termsView)
        addBitmapToPdf(termsBitmap, pdfDocument, 2)

        // **3. Save the PDF**
        val pdfDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Trip360")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val fileName = "${booking.name.replace("[^a-zA-Z0-9]".toRegex(), "_")}_${System.currentTimeMillis()}.pdf"
        val pdfFile = File(pdfDir, fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            callback.onPdfGenerated(pdfFile.absolutePath)
        } catch (e: Exception) {
            callback.onFailure(e.message.toString())
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }

    private fun generatePrivateInvoicePdf(view: View, booking: Invoice, context: PrivateInvoiceActivity) {
        (view.findViewById<TextView>(R.id.tvGuestName)).text = booking.name
        (view.findViewById<TextView>(R.id.tvBookingCode)).text = booking.bookingCode
        (view.findViewById<TextView>(R.id.tvPickupTime)).text = booking.pickupTime
        (view.findViewById<TextView>(R.id.tvBookingDate)).text = booking.pickupDate
        (view.findViewById<TextView>(R.id.tvPhoneNo)).text = booking.phone
        (view.findViewById<TextView>(R.id.tvEmail)).text = booking.email
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

//        val phoneNo = getValue(context, KEY_PHONE_NO, "")
//        view.findViewById<TextView>(R.id.tvPhoneFooter).text = phoneNo
//
//        val webName = getValue(context, KEY_WEB_URL, "")
//        view.findViewById<TextView>(R.id.tvWebNameFooter).text = webName
//
//        val email = getValue(context, KEY_EMAIL, "")
//        view.findViewById<TextView>(R.id.tvWebUrlFooter).text = email

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

        // Fetch the background drawable and update its color
        fun updateBackground(view: View, colorInt: Int) {
            val drawable = view.background as? GradientDrawable
            drawable?.setColor(colorInt) // Update the solid color while keeping other properties
        }

        val heading = view.findViewById<View>(R.id.heading1)
        val heading2 = view.findViewById<View>(R.id.heading2)
        val heading3 = view.findViewById<View>(R.id.heading3)

// Apply the dynamic color while retaining padding & corner radius
        heading?.let { updateBackground(it, colorInt) }
        heading2?.let { updateBackground(it, colorInt) }
        heading3?.let { updateBackground(it, colorInt) }



        //  Set color for the Table Header
        val tableHeader = view.findViewById<View>(R.id.tableHeader)
        tableHeader?.let { updateBackground(it, colorInt) }
        //  Set color for the Material Dividers
        val materialDivider1 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider1)
        val materialDivider2 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider2)
        val materialDivider3 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider3)
//        val materialDividerterm = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDividerterm)
        materialDivider1?.dividerColor = colorInt
        materialDivider2?.dividerColor = colorInt
        materialDivider3?.dividerColor = colorInt
//        materialDividerterm?.dividerColor = colorInt

        //  Set color for LinearLayout
        val linearLayout000 = view.findViewById<View>(R.id.linearLayout000)
        linearLayout000?.setBackgroundColor(colorInt)

        val linearLayoutterm = view.findViewById<View>(R.id.linearLayoutterm)
        linearLayoutterm?.setBackgroundColor(colorInt)


        //  Set color for TextView (Foreground Color)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView?.setTextColor(colorInt) // Changes text color instead of background

        //  Load the logo from SharedPreferences
        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
        Log.d("InvoiceActivity", "Retrieved logo path: $KEY_LOGO_LOCAL_FILE_PATH")

        val imgHeader = view.findViewById<ImageView>(R.id.imageView)
        val imgHeader2 = view.findViewById<ImageView>(R.id.imageViewterm)

        if (logoLocalPath.isNotEmpty()) {
            val file = File(logoLocalPath)
            if (file.exists()) {
                imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//                imgHeader2.setImageDrawable(Drawable.createFromPath(logoLocalPath))
            } else {
                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
            }
        } else {
            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
        }
    }

    // Helper function to render any View into a Bitmap
//    private fun renderViewToBitmap(context: Context, view: View): Bitmap {
//        val displayMetrics = context.resources.displayMetrics
//        val screenWidth = displayMetrics.widthPixels
//
//        view.measure(
//            View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY),
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        )
//        view.layout(0, 0, screenWidth, view.measuredHeight)
//
//        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        view.draw(canvas)
//
//        return bitmap
//    }

    // Helper function to add a Bitmap as a page in PDF
//    private fun addBitmapToPdf(bitmap: Bitmap, pdfDocument: PdfDocument, pageNumber: Int) {
//        val pageWidth = 500  // A4 width in pixels (8.27 inches at 72 DPI)
//        val pageHeight = 842 // A4 height in pixels (11.69 inches at 72 DPI)
//        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
//        val page = pdfDocument.startPage(pageInfo)
//        val pdfCanvas = page.canvas
//
//        val scaleX = pageWidth.toFloat() / bitmap.width
//        val scaleY = pageHeight.toFloat() / bitmap.height
//        val scale = minOf(scaleX, scaleY)
//
//        val offsetX = (pageWidth - bitmap.width * scale) / 2
//        val offsetY = (pageHeight - bitmap.height * scale) / 2
//
//        val matrix = Matrix()
//        matrix.postScale(scale, scale)
//        matrix.postTranslate(offsetX, offsetY)
//
//        pdfCanvas.drawBitmap(bitmap, matrix, null)
//        pdfDocument.finishPage(page)
//    }

    // Helper function to setup Terms & Conditions
//    private fun setupTermsView(termsView: View, context: Context) {
//        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF")
//        val colorInt = android.graphics.Color.parseColor(dynamicColor)
//
//        termsView.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDividerterm)?.dividerColor = colorInt
//        termsView.findViewById<View>(R.id.linearLayoutterm)?.setBackgroundColor(colorInt)
//
//        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
//        val imgHeaderTerm = termsView.findViewById<ImageView>(R.id.imageViewterm)
//        if (logoLocalPath.isNotEmpty()) {
//            val file = File(logoLocalPath)
//            if (file.exists()) {
//                imgHeaderTerm.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//            }
//        }
//
//        termsView.findViewById<TextView>(R.id.tvPhoneFooter1)?.text = getValue(context, KEY_PHONE_NO, "")
//        termsView.findViewById<TextView>(R.id.tvWebNameFooter1)?.text = getValue(context, KEY_WEB_URL, "")
//        termsView.findViewById<TextView>(R.id.tvWebUrlFooter1)?.text = getValue(context, KEY_EMAIL, "")
//    }

//


//    fun generatePrivateInvoicePdf(
//        context: PrivateInvoiceActivity,
//        selectedTemplate: Int,
//        termsTemplate: Int,
//        booking: Invoice,
//        callback: PdfGenCallback
//    ) {
//        val pdfDocument = PdfDocument()
//        val pageWidth = 1000
//        val pageHeight = 1500
//
//        // ----- Page 1: Invoice -----
//        val invoiceView = LayoutInflater.from(context).inflate(selectedTemplate, null)
//        generatePrivateInvoicePdf(invoiceView, booking, context) // Populate invoice data
//
//        // Measure the invoice view with forced width but let height be measured naturally
//        invoiceView.measure(
//            MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY),
//            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//        )
//        invoiceView.layout(0, 0, pageWidth, invoiceView.measuredHeight)
//
//        // Calculate scale factor to fit the invoice view within the PDF page
//        val scaleInvoice = minOf(
//            pageWidth.toFloat() / invoiceView.measuredWidth,
//            pageHeight.toFloat() / invoiceView.measuredHeight
//        )
//
//        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
//        val page1 = pdfDocument.startPage(pageInfo1)
//        val canvas1 = page1.canvas
//        canvas1.save()
//        canvas1.scale(scaleInvoice, scaleInvoice)
//        invoiceView.draw(canvas1)
//        canvas1.restore()
//        pdfDocument.finishPage(page1)
//
//        // ----- Page 2: Terms & Conditions -----
//        val termsView = LayoutInflater.from(context).inflate(termsTemplate, null)
//        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF") // Default to white
//        val colorInt = android.graphics.Color.parseColor(dynamicColor)
//
//        // Set dynamic colors and logo
//        termsView.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDividerterm)?.dividerColor = colorInt
//        termsView.findViewById<View>(R.id.linearLayoutterm)?.setBackgroundColor(colorInt)
//
//        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
//        val imgHeaderTerm = termsView.findViewById<ImageView>(R.id.imageViewterm)
//        if (logoLocalPath.isNotEmpty()) {
//            val file = File(logoLocalPath)
//            if (file.exists()) {
//                imgHeaderTerm.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//            } else {
//                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
//            }
//        } else {
//            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
//        }
//
//        termsView.findViewById<TextView>(R.id.tvPhoneFooter1)?.text = getValue(context, KEY_PHONE_NO, "")
//        termsView.findViewById<TextView>(R.id.tvWebNameFooter1)?.text = getValue(context, KEY_WEB_URL, "")
//        termsView.findViewById<TextView>(R.id.tvWebUrlFooter1)?.text = getValue(context, KEY_EMAIL, "")
//
//        // Measure the terms view with forced width but letting height adjust naturally
//        termsView.measure(
//            MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY),
//            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//        )
//        termsView.layout(0, 0, pageWidth, termsView.measuredHeight)
//
//        // Calculate scale factor for the terms view
//        val scaleTerms = minOf(
//            pageWidth.toFloat() / termsView.measuredWidth,
//            pageHeight.toFloat() / termsView.measuredHeight
//        )
//
//        val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
//        val page2 = pdfDocument.startPage(pageInfo2)
//        val canvas2 = page2.canvas
//        canvas2.save()
//        canvas2.scale(scaleTerms, scaleTerms)
//        termsView.draw(canvas2)
//        canvas2.restore()
//        pdfDocument.finishPage(page2)
//
//        // Define the Trip360 folder in Documents and ensure it exists
//        val trip360Dir = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
//            "Trip360"
//        )
//        if (!trip360Dir.exists()) {
//            trip360Dir.mkdirs()
//        }
//
//        // Generate filename with Name + Timestamp
//        val timestamp = System.currentTimeMillis()
//        val sanitizedFileName = booking.name.replace("[^a-zA-Z0-9]".toRegex(), "_")
//        val fileName = "${sanitizedFileName}_$timestamp.pdf"
//
//        val pdfFile = File(trip360Dir, fileName)
//
//        try {
//            pdfDocument.writeTo(FileOutputStream(pdfFile))
//            Log.d("TestingPdfCreation", "File saved: ${pdfFile.absolutePath}")
//            callback.onPdfGenerated(pdfFile.absolutePath)
//        } catch (e: Exception) {
//            callback.onFailure(e.message.toString())
//            e.printStackTrace()
//        } finally {
//            pdfDocument.close()
//        }
//    }



    // Function to populate invoice data
//    private fun generatePrivateInvoicePdf(view: View, booking: Invoice, context: PrivateInvoiceActivity) {
//        (view.findViewById<TextView>(R.id.tvGuestName)).text = booking.name
//        (view.findViewById<TextView>(R.id.tvBookingCode)).text = booking.bookingCode
//        (view.findViewById<TextView>(R.id.tvPickupTime)).text = booking.pickupTime
//        (view.findViewById<TextView>(R.id.tvBookingDate)).text = booking.pickupDate
//        (view.findViewById<TextView>(R.id.tvPhoneNo)).text = booking.phone
//        (view.findViewById<TextView>(R.id.tvEmail)).text = booking.email
//        (view.findViewById<TextView>(R.id.tvAddon)).text = booking.additionalAddon
//        (view.findViewById<TextView>(R.id.tvGrandTotal)).text = "AED " + calculateTotalPrice(booking)
//        (view.findViewById<TextView>(R.id.tvPackageName)).text = booking.packageName ?: "N/A"
//        (view.findViewById<TextView>(R.id.tvPickupLocation)).text = booking.pickupLocation ?: "N/A"
//        (view.findViewById<TextView>(R.id.tvDropOffLocation)).text = booking.dropLocation ?: "N/A"
//        (view.findViewById<TextView>(R.id.tvAdultCount)).text = booking.noOfAdults?.toString() ?: "0"
//        (view.findViewById<TextView>(R.id.tvKidsCount)).text = booking.noOfKids?.toString() ?: "0"
//        (view.findViewById<TextView>(R.id.tvPricePerKid)).text = booking.pkgPricePerKid?.toString() ?: "0.00"
//        (view.findViewById<TextView>(R.id.tvPricePerAdult)).text = booking.pkgPricePerAdult?.toString() ?: "0.00"
//        (view.findViewById<TextView>(R.id.tvTotalOnAdults)).text = calculateTotalPriceForAdults(booking).toString()
//        (view.findViewById<TextView>(R.id.tvTotalOnKids)).text = calculateTotalPriceForKids(booking).toString()
//        (view.findViewById<TextView>(R.id.tvNoOfPeople)).text = booking.noOfPeople.toString()
//        (view.findViewById<TextView>(R.id.tvPaymentStatus)).text = if (booking.paymentStatus) "Paid" else "Payment on Arrival"
//
////        val phoneNo = getValue(context, KEY_PHONE_NO, "")
////        view.findViewById<TextView>(R.id.tvPhoneFooter).text = phoneNo
////
////        val webName = getValue(context, KEY_WEB_URL, "")
////        view.findViewById<TextView>(R.id.tvWebNameFooter).text = webName
////
////        val email = getValue(context, KEY_EMAIL, "")
////        view.findViewById<TextView>(R.id.tvWebUrlFooter).text = email
//
//        val phoneNo = getValue(context, KEY_PHONE_NO, "")
//        val phone = view.findViewById<TextView>(R.id.tvPhoneFooter)
//        phone.text = phoneNo
//
//        val webName = getValue(context, KEY_WEB_URL, "")
//        val web = view.findViewById<TextView>(R.id.tvWebNameFooter)
//        web.text = webName
//
//        val email = getValue(context, KEY_EMAIL, "")
//        val Email = view.findViewById<TextView>(R.id.tvWebUrlFooter)
//        Email.text = email
//
//
//
//        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF") // Default to white
//        val colorInt = android.graphics.Color.parseColor(dynamicColor) // Convert hex to color int
//
//        // Fetch the background drawable and update its color
//        fun updateBackground(view: View, colorInt: Int) {
//            val drawable = view.background as? GradientDrawable
//            drawable?.setColor(colorInt) // Update the solid color while keeping other properties
//        }
//
//        val heading = view.findViewById<View>(R.id.heading1)
//        val heading2 = view.findViewById<View>(R.id.heading2)
//        val heading3 = view.findViewById<View>(R.id.heading3)
//
//// Apply the dynamic color while retaining padding & corner radius
//        heading?.let { updateBackground(it, colorInt) }
//        heading2?.let { updateBackground(it, colorInt) }
//        heading3?.let { updateBackground(it, colorInt) }
//
//
//
//        //  Set color for the Table Header
//        val tableHeader = view.findViewById<View>(R.id.tableHeader)
//        tableHeader?.let { updateBackground(it, colorInt) }
//        //  Set color for the Material Dividers
//        val materialDivider1 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider1)
//        val materialDivider2 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider2)
//        val materialDivider3 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider3)
////        val materialDividerterm = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDividerterm)
//        materialDivider1?.dividerColor = colorInt
//        materialDivider2?.dividerColor = colorInt
//        materialDivider3?.dividerColor = colorInt
////        materialDividerterm?.dividerColor = colorInt
//
//        //  Set color for LinearLayout
//        val linearLayout000 = view.findViewById<View>(R.id.linearLayout000)
//        linearLayout000?.setBackgroundColor(colorInt)
//
//        val linearLayoutterm = view.findViewById<View>(R.id.linearLayoutterm)
//        linearLayoutterm?.setBackgroundColor(colorInt)
//
//
//        //  Set color for TextView (Foreground Color)
//        val textView = view.findViewById<TextView>(R.id.textView)
//        textView?.setTextColor(colorInt) // Changes text color instead of background
//
//        //  Load the logo from SharedPreferences
//        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
//        Log.d("InvoiceActivity", "Retrieved logo path: $KEY_LOGO_LOCAL_FILE_PATH")
//
//        val imgHeader = view.findViewById<ImageView>(R.id.imageView)
//        val imgHeader2 = view.findViewById<ImageView>(R.id.imageViewterm)
//
//        if (logoLocalPath.isNotEmpty()) {
//            val file = File(logoLocalPath)
//            if (file.exists()) {
//                imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))
////                imgHeader2.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//            } else {
//                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
//            }
//        } else {
//            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
//        }
//    }

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



//        fun generatePrivateInvoicePdf(
//            context: PrivateInvoiceActivity,
//            selectedTemplate: Int,
//            booking: Invoice,
//            callback: PdfGenCallback
//    ) {
//        // Inflate the XML layout
//        val view = LayoutInflater.from(context).inflate(selectedTemplate, null)
//
//        Log.d("TestingPdfCreation", "In Native API")
//
//        (view.findViewById<TextView>(R.id.tvGuestName)).text = booking.name
//        (view.findViewById<TextView>(R.id.tvBookingCode)).text = booking.bookingCode
//        (view.findViewById<TextView>(R.id.tvPickupTime)).text = booking.pickupTime
//        (view.findViewById<TextView>(R.id.tvBookingDate)).text = booking.pickupDate
//        (view.findViewById<TextView>(R.id.tvPhoneNo)).text = booking.phone
//        (view.findViewById<TextView>(R.id.tvAddon)).text = booking.additionalAddon
//        (view.findViewById<TextView>(R.id.tvGrandTotal)).text = "AED " + calculateTotalPrice(booking)
//        (view.findViewById<TextView>(R.id.tvPackageName)).text = booking.packageName ?: "N/A"
//        (view.findViewById<TextView>(R.id.tvPickupLocation)).text = booking.pickupLocation ?: "N/A"
//        (view.findViewById<TextView>(R.id.tvDropOffLocation)).text = booking.dropLocation ?: "N/A"
//        (view.findViewById<TextView>(R.id.tvAdultCount)).text = booking.noOfAdults?.toString() ?: "0"
//        (view.findViewById<TextView>(R.id.tvKidsCount)).text = booking.noOfKids?.toString() ?: "0"
//        (view.findViewById<TextView>(R.id.tvPricePerKid)).text = booking.pkgPricePerKid?.toString() ?: "0.00"
//        (view.findViewById<TextView>(R.id.tvPricePerAdult)).text = booking.pkgPricePerAdult?.toString() ?: "0.00"
//        (view.findViewById<TextView>(R.id.tvTotalOnAdults)).text = calculateTotalPriceForAdults(booking).toString()
//        (view.findViewById<TextView>(R.id.tvTotalOnKids)).text = calculateTotalPriceForKids(booking).toString()
//        (view.findViewById<TextView>(R.id.tvNoOfPeople)).text = booking.noOfPeople.toString()
//        (view.findViewById<TextView>(R.id.tvPaymentStatus)).text = if (booking.paymentStatus) "Paid" else "Payment on Arrival"
//
//        val phoneNo = getValue(context, KEY_PHONE_NO, "")
//        val phone = view.findViewById<TextView>(R.id.tvPhoneFooter)
//        phone.text = phoneNo
//
//        val webName = getValue(context, KEY_WEB_URL, "")
//        val web = view.findViewById<TextView>(R.id.tvWebNameFooter)
//        web.text = webName
//
//        val email = getValue(context, KEY_EMAIL, "")
//        val Email = view.findViewById<TextView>(R.id.tvWebUrlFooter)
//        Email.text = email
//
//        val dynamicColor = getValue(context, KEY_COLOR, "#FFFFFF") // Default to white
//        val colorInt = android.graphics.Color.parseColor(dynamicColor) // Convert hex to color int
//
//        //  Set color for the Table Header
//        val tableHeader = view.findViewById<View>(R.id.tableHeader)
//        tableHeader?.setBackgroundColor(colorInt)
//        //  Set color for the Material Dividers
//        val materialDivider1 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider1)
//        val materialDivider2 = view.findViewById<com.google.android.material.divider.MaterialDivider>(R.id.materialDivider2)
//        materialDivider1?.dividerColor = colorInt
//        materialDivider2?.dividerColor = colorInt
//
//        //  Set color for LinearLayout
//        val linearLayout000 = view.findViewById<View>(R.id.linearLayout000)
//        linearLayout000?.setBackgroundColor(colorInt)
//        //  Set color for TextView (Foreground Color)
//        val textView = view.findViewById<TextView>(R.id.textView)
//        textView?.setTextColor(colorInt) // Changes text color instead of background
//
//        //  Load the logo from SharedPreferences
//        val logoLocalPath = getValue(context, KEY_LOGO_LOCAL_FILE_PATH, "")
//        Log.d("InvoiceActivity", "Retrieved logo path: $KEY_LOGO_LOCAL_FILE_PATH")
//
//        val imgHeader = view.findViewById<ImageView>(R.id.imageView)
//
//        if (logoLocalPath.isNotEmpty()) {
//            val file = File(logoLocalPath)
//            if (file.exists()) {
//                imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//            } else {
//                Log.e("InvoiceActivity", "Logo file does not exist at path: $logoLocalPath")
//            }
//        } else {
//            Log.e("InvoiceActivity", "Logo path is empty or invalid.")
//        }
//
//        //  Define page size
//        val pageWidth = 1000
//        val pageHeight = 1300
//
//        //  Measure & layout the view
//        view.measure(
//            MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY),
//            MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.EXACTLY)
//        )
//        view.layout(0, 0, pageWidth, pageHeight)
//
//        val pdfDocument = PdfDocument()
//        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
//        val page = pdfDocument.startPage(pageInfo)
//        val canvas = page.canvas
//
//        //  Draw the full layout
//        view.draw(canvas)
//
//        pdfDocument.finishPage(page)
//
//        //  Define the Trip360 folder in Documents and ensure it exists
//        val trip360Dir = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
//            "Trip360"
//        )
//        if (!trip360Dir.exists()) {
//            trip360Dir.mkdirs()
//        }
//
//        //  Generate filename with Name + Timestamp
//        val timestamp = System.currentTimeMillis()
//        val sanitizedFileName = booking.name.replace("[^a-zA-Z0-9]".toRegex(), "_") // Replace invalid characters
//        val fileName = "${sanitizedFileName}_$timestamp.pdf"
//
//        val pdfFile = File(trip360Dir, fileName)
//
//        try {
//            pdfDocument.writeTo(FileOutputStream(pdfFile))
//            Log.d("TestingPdfCreation", "File saved: ${pdfFile.absolutePath}")
//            callback.onPdfGenerated(pdfFile.absolutePath)
//        } catch (e: Exception) {
//            callback.onFailure(e.message.toString())
//            e.printStackTrace()
//        } finally {
//            pdfDocument.close()
//        }
//    }




