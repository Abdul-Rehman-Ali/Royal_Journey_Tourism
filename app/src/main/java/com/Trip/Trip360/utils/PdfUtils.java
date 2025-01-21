//package com.Trip.Trip360.utils;
//
//import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_LOCAL_FILE_PATH;
//import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_PHONE_NO;
//import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_NAME;
//import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_URL;
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.activity.ComponentActivity;
//import com.Trip.Trip360.R;
//import com.Trip.Trip360.data.Invoice;
//import com.gkemon.XMLtoPDF.PdfGenerator;
//import com.gkemon.XMLtoPDF.PdfGeneratorListener;
//import com.gkemon.XMLtoPDF.model.FailureResponse;
//import com.gkemon.XMLtoPDF.model.SuccessResponse;
//import java.io.File;
//
//public class PdfUtils {
//
//    public static void generateInvoicePdf(int selectedTemplate, Invoice booking, Context context, PdfGenerationCallback callback) {
//        try {
//            String paymentStatus = "N/A";
//
//            LayoutInflater inflater = LayoutInflater.from(context);
//            View view = inflater.inflate(selectedTemplate, null);
//
//            // Populate the view with data
//            ((TextView) view.findViewById(R.id.tvGuestName)).setText(booking.getName());
//            ((TextView) view.findViewById(R.id.tvPickupTime)).setText(booking.getPickupTime());
//            ((TextView) view.findViewById(R.id.tvBookingDate)).setText(booking.getPickupDate());
//            ((TextView) view.findViewById(R.id.tvGrandTotal)).setText("AED "+ calculateTotalPrice(booking));
//            ((TextView) view.findViewById(R.id.tvPackageName)).setText(booking.getPackageName() != null ? booking.getPackageName() : "N/A");
//            ((TextView) view.findViewById(R.id.tvPickupLocation)).setText(booking.getPickupLocation() != null ? booking.getPickupLocation() : "N/A");
//            ((TextView) view.findViewById(R.id.tvAdultCount)).setText(booking.getNoOfAdults() != null ? String.valueOf(booking.getNoOfAdults()) : "0");
//            ((TextView) view.findViewById(R.id.tvKidsCount)).setText(booking.getNoOfKids() != null ? String.valueOf(booking.getNoOfKids()) : "0");
//            ((TextView) view.findViewById(R.id.tvPricePerKid)).setText(booking.getPkgPricePerKid() != null ? String.valueOf(booking.getPkgPricePerKid()) : "0.00");
//            ((TextView) view.findViewById(R.id.tvPricePerAdult)).setText(booking.getPkgPricePerAdult() != null ? String.valueOf(booking.getPkgPricePerAdult()) : "0.00");
//            ((TextView) view.findViewById(R.id.tvTotalOnAdults)).setText(String.valueOf(calculateTotalPriceForAdults(booking)));
//            ((TextView) view.findViewById(R.id.tvTotalOnKids)).setText(String.valueOf(calculateTotalPriceForKids(booking)));
//
//            // payment status
//            if (booking.getPaymentStatus()) {
//                paymentStatus = "Paid";
//            } else {
//                paymentStatus = "Payment on Arrival";
//            }
//
//            ((TextView) view.findViewById(R.id.tvPaymentStatus)).setText(paymentStatus);
//
//            // set dynamic values
//            ((TextView) view.findViewById(R.id.tvPhoneFooter)).setText(SharedPrefUtils.INSTANCE.getValue(context, KEY_PHONE_NO, ""));
//            ((TextView) view.findViewById(R.id.tvWebNameFooter)).setText(SharedPrefUtils.INSTANCE.getValue(context, KEY_WEB_NAME, ""));
//            ((TextView) view.findViewById(R.id.tvWebUrlFooter)).setText(SharedPrefUtils.INSTANCE.getValue(context, KEY_WEB_URL, ""));
//
//
////            String logoUrl = SharedPrefUtils.INSTANCE.getValue(context, KEY_LOGO_URL, "emptyUrl");
//
//            ImageView logoImageView = view.findViewById(R.id.imageView); // Assuming you have an ImageView with id 'ivLogo'
//
//            Drawable drawable = Drawable.createFromPath(SharedPrefUtils.INSTANCE.getValue(context, KEY_LOGO_LOCAL_FILE_PATH,""));
//            logoImageView.setImageDrawable(drawable);
//
//
////            String colorHex = SharedPrefUtils.INSTANCE.getValue(context, KEY_COLOR, "#FF5733");
////            int color = Color.parseColor(colorHex);
//
////            view.findViewById(R.id.tableHeader).setBackgroundColor(color);
////            view.findViewById(R.id.linearLayout000).setBackgroundColor(color);
////            ((TextView) view.findViewById(R.id.textView)).setTextColor(color);
////            view.findViewById(R.id.materialDivider1).setBackgroundColor(color);
////            view.findViewById(R.id.materialDivider2).setBackgroundColor(color);
//
//
////            File documentsDir = new File(context.getExternalFilesDir(null), "RoyalInvoices");
////            if (!documentsDir.exists()) {
////                if (!documentsDir.mkdirs()) {
////                    Log.e("PdfUtils", "Failed to create directory: " + documentsDir.getAbsolutePath());
////                }
////            }
//            String fileName = booking.getName() + "_" + booking.getPickupDate();
//            fileName = fileName.replaceAll("[^a-zA-Z0-9_\\-.]", "-");
//
//            File pdfFile = new File(context.getExternalFilesDir(null), fileName);
//
//            // Generate the PDF
//            PdfGenerator.getBuilder()
//                    .setContext((ComponentActivity) context)
//                    .fromViewSource()
//                    .fromView(view)
//                    .setFileName(pdfFile.getName())
//                    .setFolderNameOrPath(pdfFile.getParent())
//                    .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
//                    .build(new PdfGeneratorListener() {
//                        @Override
//                        public void onSuccess(SuccessResponse response) {
//                            callback.onPdfGenerated(pdfFile.getAbsolutePath());
//                        }
//
//                        @Override
//                        public void onFailure(FailureResponse failureResponse) {
//                            callback.onFailure(failureResponse.getErrorMessage());
//                        }
//
//                        @Override
//                        public void showLog(String log) {
//                            // Optionally log details
//                        }
//
//                        @Override
//                        public void onStartPDFGeneration() {
//                        }
//
//                        @Override
//                        public void onFinishPDFGeneration() {
//                        }
//                    });
//
//        } catch (Exception e) {
//            Log.e("PdfDebugger", "Failed to generate invoice: " + e.getMessage());
//        }
//    }
//
//    public static double calculateTotalPrice(Invoice booking) {
//        return calculateTotalPriceForAdults(booking) + calculateTotalPriceForKids(booking);
//    }
//
//    private static double calculateTotalPriceForAdults(Invoice booking) {
//
//        if (booking.getNoOfAdults() != null && booking.getPkgPricePerAdult() != null) {
//            return booking.getNoOfAdults() * booking.getPkgPricePerAdult();
//        }
//        return 0.00;
//    }
//
//    private static double calculateTotalPriceForKids(Invoice booking) {
//        if (booking.getNoOfKids() != null && booking.getPkgPricePerKid() != null) {
//            return booking.getNoOfKids() * booking.getPkgPricePerKid();
//        }
//        return 0.00;
//    }
//}
//

//package com.Trip.Trip360.utils;
//
//import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_LOCAL_FILE_PATH;
//import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_PHONE_NO;
//import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_NAME;
//import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_URL;
//
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.LifecycleObserver;
//import androidx.lifecycle.OnLifecycleEvent;
//
//import com.Trip.Trip360.R;
//import com.Trip.Trip360.data.Invoice;
//import com.gkemon.XMLtoPDF.PdfGenerator;
//import com.gkemon.XMLtoPDF.PdfGeneratorListener;
//import com.gkemon.XMLtoPDF.model.FailureResponse;
//import com.gkemon.XMLtoPDF.model.SuccessResponse;
//
//import java.io.File;
//
//public class PdfUtils {
//
//    public static void generateInvoicePdf(int selectedTemplate, Invoice booking, AppCompatActivity activity, PdfGenerationCallback callback) {
//        if (activity == null) {
//            throw new IllegalArgumentException("Provided activity is null or does not have a valid lifecycle.");
//        } else {
//            activity.getLifecycle();
//        }
//
//        View view = null; // Declare view for cleanup after use
//        try {
//            String paymentStatus = "N/A";
//
//            LayoutInflater inflater = LayoutInflater.from(activity);
//            view = inflater.inflate(selectedTemplate, null);
//
//            // Populate the view with data
//            ((TextView) view.findViewById(R.id.tvGuestName)).setText(booking.getName());
//            ((TextView) view.findViewById(R.id.tvPickupTime)).setText(booking.getPickupTime());
//            ((TextView) view.findViewById(R.id.tvBookingDate)).setText(booking.getPickupDate());
//            ((TextView) view.findViewById(R.id.tvGrandTotal)).setText("AED " + calculateTotalPrice(booking));
//            ((TextView) view.findViewById(R.id.tvPackageName)).setText(booking.getPackageName() != null ? booking.getPackageName() : "N/A");
//            ((TextView) view.findViewById(R.id.tvPickupLocation)).setText(booking.getPickupLocation() != null ? booking.getPickupLocation() : "N/A");
//            ((TextView) view.findViewById(R.id.tvAdultCount)).setText(booking.getNoOfAdults() != null ? String.valueOf(booking.getNoOfAdults()) : "0");
//            ((TextView) view.findViewById(R.id.tvKidsCount)).setText(booking.getNoOfKids() != null ? String.valueOf(booking.getNoOfKids()) : "0");
//            ((TextView) view.findViewById(R.id.tvPricePerKid)).setText(booking.getPkgPricePerKid() != null ? String.valueOf(booking.getPkgPricePerKid()) : "0.00");
//            ((TextView) view.findViewById(R.id.tvPricePerAdult)).setText(booking.getPkgPricePerAdult() != null ? String.valueOf(booking.getPkgPricePerAdult()) : "0.00");
//            ((TextView) view.findViewById(R.id.tvTotalOnAdults)).setText(String.valueOf(calculateTotalPriceForAdults(booking)));
//            ((TextView) view.findViewById(R.id.tvTotalOnKids)).setText(String.valueOf(calculateTotalPriceForKids(booking)));
//
//            if (booking.getPaymentStatus()) {
//                paymentStatus = "Paid";
//            } else {
//                paymentStatus = "Payment on Arrival";
//            }
//
//            ((TextView) view.findViewById(R.id.tvPaymentStatus)).setText(paymentStatus);
//
//            ((TextView) view.findViewById(R.id.tvPhoneFooter)).setText(SharedPrefUtils.INSTANCE.getValue(activity, KEY_PHONE_NO, ""));
//            ((TextView) view.findViewById(R.id.tvWebNameFooter)).setText(SharedPrefUtils.INSTANCE.getValue(activity, KEY_WEB_NAME, ""));
//            ((TextView) view.findViewById(R.id.tvWebUrlFooter)).setText(SharedPrefUtils.INSTANCE.getValue(activity, KEY_WEB_URL, ""));
//
//            ImageView logoImageView = view.findViewById(R.id.imageView);
//            Drawable drawable = Drawable.createFromPath(SharedPrefUtils.INSTANCE.getValue(activity, KEY_LOGO_LOCAL_FILE_PATH, ""));
//            logoImageView.setImageDrawable(drawable);
//
//            String fileName = booking.getName() + "_" + booking.getPickupDate();
//            fileName = fileName.replaceAll("[^a-zA-Z0-9_\\-.]", "-");
//
//            File pdfFile = new File(activity.getExternalFilesDir(null), fileName);
//
//            // Attach custom lifecycle observer
//            PdfLifecycleObserver lifecycleObserver = new PdfLifecycleObserver(view);
//            activity.getLifecycle().addObserver(lifecycleObserver);
//
//            // Generate the PDF with lifecycle-aware context
//            PdfGenerator.getBuilder()
//                    .setContext(activity) // Use lifecycle-aware context
//                    .fromViewSource()
//                    .fromView(view)
//                    .setFileName(pdfFile.getName())
//                    .setFolderNameOrPath(pdfFile.getParent())
//                    .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
//                    .build(new PdfGeneratorListener() {
//                        @Override
//                        public void onSuccess(SuccessResponse response) {
//                            callback.onPdfGenerated(pdfFile.getAbsolutePath());
//                        }
//
//                        @Override
//                        public void onFailure(FailureResponse failureResponse) {
//                            callback.onFailure(failureResponse.getErrorMessage());
//                        }
//
//                        @Override
//                        public void showLog(String log) {
//                            Log.d("PdfGenerator", log);
//                        }
//
//                        @Override
//                        public void onStartPDFGeneration() {
//                            Log.d("PdfGenerator", "PDF generation started");
//                        }
//
//                        @Override
//                        public void onFinishPDFGeneration() {
//                            Log.d("PdfGenerator", "PDF generation finished");
//                        }
//                    });
//
//        } catch (Exception e) {
//            Log.e("PdfDebugger", "Failed to generate invoice: " + e.getMessage());
//            cleanupView(view);
//        }
//    }
//
//    private static void cleanupView(View view) {
//        if (view != null) {
//            view.setOnClickListener(null); // Remove click listeners
//            view = null; // Dereference for garbage collection
//        }
//    }
//
//    public static double calculateTotalPrice(Invoice booking) {
//        return calculateTotalPriceForAdults(booking) + calculateTotalPriceForKids(booking);
//    }
//
//    private static double calculateTotalPriceForAdults(Invoice booking) {
//        if (booking.getNoOfAdults() != null && booking.getPkgPricePerAdult() != null) {
//            return booking.getNoOfAdults() * booking.getPkgPricePerAdult();
//        }
//        return 0.00;
//    }
//
//    private static double calculateTotalPriceForKids(Invoice booking) {
//        if (booking.getNoOfKids() != null && booking.getPkgPricePerKid() != null) {
//            return booking.getNoOfKids() * booking.getPkgPricePerKid();
//        }
//        return 0.00;
//    }
//
//    public static class PdfLifecycleObserver implements LifecycleObserver {
//
//        private final View view;
//
//        public PdfLifecycleObserver(View view) {
//            this.view = view;
//        }
//
//        @OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_DESTROY)
//        public void onDestroy() {
//            cleanupView(view);
//        }
//    }
//}


package com.Trip.Trip360.utils;

import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_LOCAL_FILE_PATH;
import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_PHONE_NO;
import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_NAME;
import static com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_URL;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.Trip.Trip360.R;
import com.Trip.Trip360.data.Invoice;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;

import java.io.File;
import java.lang.ref.WeakReference;

public class PdfUtils {

    public static void generateInvoicePdf(int selectedTemplate, Invoice booking, AppCompatActivity activity, PdfGenerationCallback callback) {
        if (activity == null || activity.getLifecycle() == null) {
            throw new IllegalArgumentException("Provided activity is null or does not have a valid lifecycle.");
        }

        View view = null;
        try {
            String paymentStatus;

            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(selectedTemplate, null);

            // Populate the view with booking data
            populateInvoiceView(view, booking, activity);

            // Generate a sanitized file name
            String fileName = booking.getName() + "_" + booking.getPickupDate();
            fileName = fileName.replaceAll("[^a-zA-Z0-9_\\-.]", "-");

            // Save the PDF in the app's external files directory
            File pdfFile = new File(activity.getExternalFilesDir(null), fileName);

            // Attach a lifecycle observer for cleanup
            PdfLifecycleObserver lifecycleObserver = new PdfLifecycleObserver(view);
            activity.getLifecycle().addObserver(lifecycleObserver);

            // Generate the PDF
            PdfGenerator.getBuilder()
                    .setContext(activity)
                    .fromViewSource()
                    .fromView(view)
                    .setFileName(pdfFile.getName())
                    .setFolderNameOrPath(pdfFile.getParent())
                    .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                    .build(new PdfGeneratorListener() {
                        @Override
                        public void onSuccess(SuccessResponse response) {
                            callback.onPdfGenerated(pdfFile.getAbsolutePath());
                        }

                        @Override
                        public void onFailure(FailureResponse failureResponse) {
                            callback.onFailure(failureResponse.getErrorMessage());
                        }

                        @Override
                        public void showLog(String log) {
                            Log.d("PdfGenerator", log);
                        }

                        @Override
                        public void onStartPDFGeneration() {
                            Log.d("PdfGenerator", "PDF generation started");
                        }

                        @Override
                        public void onFinishPDFGeneration() {
                            Log.d("PdfGenerator", "PDF generation finished");
                        }
                    });

        } catch (Exception e) {
            Log.e("PdfUtils", "Error generating invoice PDF: " + e.getMessage(), e);
            cleanupView(view);
            callback.onFailure("An error occurred while generating the PDF. Please try again.");
        }
    }

    private static void populateInvoiceView(View view, Invoice booking, AppCompatActivity activity) {
        ((TextView) view.findViewById(R.id.tvGuestName)).setText(booking.getName());
        ((TextView) view.findViewById(R.id.tvPickupTime)).setText(booking.getPickupTime());
        ((TextView) view.findViewById(R.id.tvBookingDate)).setText(booking.getPickupDate());
        ((TextView) view.findViewById(R.id.tvGrandTotal))
                .setText("AED " + calculateTotalPrice(booking));
        ((TextView) view.findViewById(R.id.tvPackageName))
                .setText(booking.getPackageName() != null ? booking.getPackageName() : "N/A");
        ((TextView) view.findViewById(R.id.tvPickupLocation))
                .setText(booking.getPickupLocation() != null ? booking.getPickupLocation() : "N/A");
        ((TextView) view.findViewById(R.id.tvAdultCount))
                .setText(booking.getNoOfAdults() != null ? String.valueOf(booking.getNoOfAdults()) : "0");
        ((TextView) view.findViewById(R.id.tvKidsCount))
                .setText(booking.getNoOfKids() != null ? String.valueOf(booking.getNoOfKids()) : "0");
        ((TextView) view.findViewById(R.id.tvPricePerKid))
                .setText(booking.getPkgPricePerKid() != null ? String.valueOf(booking.getPkgPricePerKid()) : "0.00");
        ((TextView) view.findViewById(R.id.tvPricePerAdult))
                .setText(booking.getPkgPricePerAdult() != null ? String.valueOf(booking.getPkgPricePerAdult()) : "0.00");
        ((TextView) view.findViewById(R.id.tvTotalOnAdults))
                .setText(String.valueOf(calculateTotalPriceForAdults(booking)));
        ((TextView) view.findViewById(R.id.tvTotalOnKids))
                .setText(String.valueOf(calculateTotalPriceForKids(booking)));

        String paymentStatus = booking.getPaymentStatus() ? "Paid" : "Payment on Arrival";
        ((TextView) view.findViewById(R.id.tvPaymentStatus)).setText(paymentStatus);

        ((TextView) view.findViewById(R.id.tvPhoneFooter))
                .setText(SharedPrefUtils.INSTANCE.getValue(activity, KEY_PHONE_NO, ""));
        ((TextView) view.findViewById(R.id.tvWebNameFooter))
                .setText(SharedPrefUtils.INSTANCE.getValue(activity, KEY_WEB_NAME, ""));
        ((TextView) view.findViewById(R.id.tvWebUrlFooter))
                .setText(SharedPrefUtils.INSTANCE.getValue(activity, KEY_WEB_URL, ""));

        ImageView logoImageView = view.findViewById(R.id.imageView);
        Drawable drawable = Drawable.createFromPath(SharedPrefUtils.INSTANCE.getValue(activity, KEY_LOGO_LOCAL_FILE_PATH, ""));
        logoImageView.setImageDrawable(drawable);
    }

    private static void cleanupView(View view) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    cleanupView(viewGroup.getChildAt(i));
                }
                viewGroup.removeAllViews();
            }
            view.setOnClickListener(null);
        }
    }

    public static double calculateTotalPrice(Invoice booking) {
        return calculateTotalPriceForAdults(booking) + calculateTotalPriceForKids(booking);
    }

    private static double calculateTotalPriceForAdults(Invoice booking) {
        if (booking.getNoOfAdults() != null && booking.getPkgPricePerAdult() != null) {
            return booking.getNoOfAdults() * booking.getPkgPricePerAdult();
        }
        return 0.00;
    }

    private static double calculateTotalPriceForKids(Invoice booking) {
        if (booking.getNoOfKids() != null && booking.getPkgPricePerKid() != null) {
            return booking.getNoOfKids() * booking.getPkgPricePerKid();
        }
        return 0.00;
    }

    public static class PdfLifecycleObserver implements LifecycleObserver {
        private final WeakReference<View> viewReference;

        public PdfLifecycleObserver(View view) {
            this.viewReference = new WeakReference<>(view);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            View view = viewReference.get();
            if (view != null) {
                cleanupView(view);
            }
        }
    }
}
