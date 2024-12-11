package com.RoyalJourneyTourism.RJT.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import com.RoyalJourneyTourism.RJT.R;
import com.RoyalJourneyTourism.RJT.data.Booking;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;

public class PdfUtils {

    public static void generateInvoicePdf(Booking booking, Context context) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.invoice_layout_1, null);

//            View footerView = inflater.inflate(R.layout.invoice_layout_footer_1, null);
//
//            LinearLayout parentLayout = new LinearLayout(context);
//            parentLayout.setOrientation(LinearLayout.VERTICAL);
//
//
//            parentLayout.addView(view);
//            parentLayout.addView(footerView);

            // Populate the view with data
            ((TextView) view.findViewById(R.id.tvGuestName)).setText(booking.getName());
            ((TextView) view.findViewById(R.id.tvPickupTime)).setText(booking.getPickupTime());
            ((TextView) view.findViewById(R.id.tvBookingDate)).setText(booking.getPickupDate());
            ((TextView) view.findViewById(R.id.tvGrandTotal)).setText(calculateTotalPrice(booking));
            ((TextView) view.findViewById(R.id.tvPackageName)).setText(booking.getPackageName() != null ? booking.getPackageName() : "N/A");
            ((TextView) view.findViewById(R.id.tvPickupLocation)).setText(booking.getPickupLocation() != null ? booking.getPickupLocation() : "N/A");
            ((TextView) view.findViewById(R.id.tvAdultCount)).setText(booking.getNoOfAdults() != null ? String.valueOf(booking.getNoOfAdults()) : "0");
            ((TextView) view.findViewById(R.id.tvKidsCount)).setText(booking.getNoOfKids() != null ? String.valueOf(booking.getNoOfKids()) : "0");
            ((TextView) view.findViewById(R.id.tvPricePerKid)).setText(booking.getPkgPricePerKid() != null ? String.valueOf(booking.getPkgPricePerKid()) : "0.00");
            ((TextView) view.findViewById(R.id.tvPricePerAdult)).setText(booking.getPkgPricePerAdult() != null ? String.valueOf(booking.getPkgPricePerAdult()) : "0.00");
            ((TextView) view.findViewById(R.id.tvTotalOnAdults)).setText(calculateTotalPriceForAdults(booking));
            ((TextView) view.findViewById(R.id.tvTotalOnKids)).setText(calculateTotalPriceForKids(booking));


            // Generate the PDF
            PdfGenerator.getBuilder()
                    .setContext((ComponentActivity) context)
                    .fromViewSource()
                    .fromView(view)
                    .setFileName("Invoice")
                    .setFolderNameOrPath(context.getFilesDir().getAbsolutePath())
                    .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                    .build(new PdfGeneratorListener() {
                        @Override
                        public void onSuccess(SuccessResponse response) {
                            Toast.makeText(context, "Invoice saved at: " + response.getPath(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(FailureResponse failureResponse) {
                            Toast.makeText(context, "Failed to generate PDF: " + failureResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
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
            Log.e("PdfDebugger", "Failed to generate invoice: "+ e.getMessage());
        }
    }

    private static String calculateTotalPrice(Booking booking) {
        // Your logic to calculate the total price for the booking
        return "1234"; // Replace with actual calculation
    }

    private static String calculateTotalPriceForAdults(Booking booking) {
        // Calculate the total price for adults
        if (booking.getNoOfAdults() != null && booking.getPkgPricePerAdult() != null) {
            double total = booking.getNoOfAdults() * booking.getPkgPricePerAdult();
            return String.valueOf(total);
        }
        return "0.00";
    }

    private static String calculateTotalPriceForKids(Booking booking) {
        // Calculate the total price for kids
        if (booking.getNoOfKids() != null && booking.getPkgPricePerKid() != null) {
            double total = booking.getNoOfKids() * booking.getPkgPricePerKid();
            return String.valueOf(total);
        }
        return "0.00";
    }

}
