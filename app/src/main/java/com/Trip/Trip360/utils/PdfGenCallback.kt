package com.Trip.Trip360.utils

import java.lang.Error

interface PdfGenCallback {
        fun onPdfGenerated(filePath: String?)
        fun onFailure(errorMessage: String?)
}