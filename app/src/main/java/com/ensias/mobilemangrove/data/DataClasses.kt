package com.ensias.mobilemangrove.data

import android.graphics.Bitmap

data class ClassificationResult(
    val imageBitmap: Bitmap?,
    val result: String,
    val confidence: String
)
