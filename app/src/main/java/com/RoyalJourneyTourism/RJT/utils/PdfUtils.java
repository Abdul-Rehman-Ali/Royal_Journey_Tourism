package com.RoyalJourneyTourism.RJT.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import com.RoyalJourneyTourism.RJT.R;
import com.RoyalJourneyTourism.RJT.data.Booking;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import java.io.File;

public class PdfUtils {

    public static void generateInvoicePdf(int selectedTemplate, Booking booking, Context context) {
        try {

            String paymentStatus = "N/A";

            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(selectedTemplate, null);

            // Populate the view with data
            ((TextView) view.findViewById(R.id.tvGuestName)).setText(booking.getName());
            ((TextView) view.findViewById(R.id.tvPickupTime)).setText(booking.getPickupTime());
            ((TextView) view.findViewById(R.id.tvBookingDate)).setText(booking.getPickupDate());
            ((TextView) view.findViewById(R.id.tvGrandTotal)).setText("AED "+calculateTotalPrice(booking));
            ((TextView) view.findViewById(R.id.tvPackageName)).setText(booking.getPackageName() != null ? booking.getPackageName() : "N/A");
            ((TextView) view.findViewById(R.id.tvPickupLocation)).setText(booking.getPickupLocation() != null ? booking.getPickupLocation() : "N/A");
            ((TextView) view.findViewById(R.id.tvAdultCount)).setText(booking.getNoOfAdults() != null ? String.valueOf(booking.getNoOfAdults()) : "0");
            ((TextView) view.findViewById(R.id.tvKidsCount)).setText(booking.getNoOfKids() != null ? String.valueOf(booking.getNoOfKids()) : "0");
            ((TextView) view.findViewById(R.id.tvPricePerKid)).setText(booking.getPkgPricePerKid() != null ? String.valueOf(booking.getPkgPricePerKid()) : "0.00");
            ((TextView) view.findViewById(R.id.tvPricePerAdult)).setText(booking.getPkgPricePerAdult() != null ? String.valueOf(booking.getPkgPricePerAdult()) : "0.00");
            ((TextView) view.findViewById(R.id.tvTotalOnAdults)).setText(String.valueOf(calculateTotalPriceForAdults(booking)));
            ((TextView) view.findViewById(R.id.tvTotalOnKids)).setText(String.valueOf(calculateTotalPriceForKids(booking)));

            // payment status
            if (booking.getPaymentStatus()) {
                paymentStatus = "Paid";
            } else {
                paymentStatus = "Payment on Arrival";
            }

            ((TextView) view.findViewById(R.id.tvPaymentStatus)).setText(paymentStatus);

            File documentsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "RoyalInvoices");
            if (!documentsDir.exists()) {
                if (!documentsDir.mkdirs()) {
                    Log.e("PdfUtils", "Failed to create directory: " + documentsDir.getAbsolutePath());
                }
            }
            String fileName = booking.getName() + "_" + booking.getPickupDate();
            fileName = fileName.replaceAll("[^a-zA-Z0-9_\\-.]", "_");

            File pdfFile = new File(documentsDir, fileName + ".pdf");


            // Generate the PDF
            PdfGenerator.getBuilder()
                    .setContext((ComponentActivity) context)
                    .fromViewSource()
                    .fromView(view)
                    .setFileName(pdfFile.getName())
                    .setFolderNameOrPath(pdfFile.getParent())
                    .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                    .build(new PdfGeneratorListener() {
                        @Override
                        public void onSuccess(SuccessResponse response) {
                            CustomDialog.INSTANCE.showMessageDialog("Invoice successfully saved at InternalStorage/Documents/RoyalInvoices " , "Success",context);
                        }

                        @Override
                        public void onFailure(FailureResponse failureResponse) {
                            CustomDialog.INSTANCE.showMessageDialog("Failed to generate PDF: " + failureResponse.getErrorMessage(), "Failure",context);
                        }

                        @Override
                        public void showLog(String log) {
                            // Optionally log details
                        }

                        @Override
                        public void onStartPDFGeneration() {
                        }

                        @Override
                        public void onFinishPDFGeneration() {
                        }
                    });

        } catch (Exception e) {
            Log.e("PdfDebugger", "Failed to generate invoice: " + e.getMessage());
        }
    }

    private static double calculateTotalPrice(Booking booking) {
        return calculateTotalPriceForAdults(booking) + calculateTotalPriceForKids(booking);
    }

    private static double calculateTotalPriceForAdults(Booking booking) {

        if (booking.getNoOfAdults() != null && booking.getPkgPricePerAdult() != null) {
            return booking.getNoOfAdults() * booking.getPkgPricePerAdult();
        }
        return 0.00;
    }

    private static double calculateTotalPriceForKids(Booking booking) {
        if (booking.getNoOfKids() != null && booking.getPkgPricePerKid() != null) {
            return booking.getNoOfKids() * booking.getPkgPricePerKid();
        }
        return 0.00;
    }
}
