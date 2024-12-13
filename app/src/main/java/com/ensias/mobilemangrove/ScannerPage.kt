package com.ensias.mobilemangrove

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.ensias.mobilemangrove.ml.MangroveModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScannerPage(navController: NavController) {
    val context = LocalContext.current
    var permissionRequested by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var resultText by remember { mutableStateOf("Result will be shown here") }
    var confidenceText by remember { mutableStateOf("Confidence will be shown here") }

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            Log.e("Permission", "Permission denied")
        }
    }

    fun checkAndRequestPermission(permission: String, action: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                action()
            }
            else -> {
                if (permissionRequested) {
                    requestPermissionLauncher.launch(permission)
                } else {
                    permissionRequested = true
                    requestPermissionLauncher.launch(permission)
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = uri
            val inputStream = context.contentResolver.openInputStream(uri)
            imageBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            imageBitmap?.let { bitmap ->
                classifyImage(bitmap, context) { result, confidence ->
                    resultText = result
                    confidenceText = confidence

                    val path = context.getExternalFilesDir(null)!!.absolutePath
                    val tempFile = File(path, "tempFileName.jpg")
                    val fOut = FileOutputStream(tempFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                    fOut.close()

                    navController.navigate("ResultPage/${resultText}")
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            imageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                bitmap?.let { rawBitmap ->
                    val orientedBitmap = rotateImageIfRequired(context, rawBitmap, uri)

                    classifyImage(orientedBitmap, context) { result, confidence ->
                        resultText = result
                        confidenceText = confidence

                        val path = context.getExternalFilesDir(null)!!.absolutePath
                        val tempFile = File(path, "tempFileName.jpg")
                        val fOut = FileOutputStream(tempFile)
                        orientedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                        fOut.close()

                        navController.navigate("ResultPage/${resultText}")
                    }
                }
            }
        }
    }

    fun onGalleryClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestPermission(Manifest.permission.READ_MEDIA_IMAGES) {
                galleryLauncher.launch("image/*")
            }
        } else {
            galleryLauncher.launch("image/*")
        }
    }

    fun onCameraClick() {
        checkAndRequestPermission(Manifest.permission.CAMERA) {
            val tempUri = FileProvider.getUriForFile(context, context.packageName + ".provider", createImageFile(context))
            imageUri = tempUri
            cameraLauncher.launch(tempUri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = {
                    navController.navigate("homePage")
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back",
                    Modifier.size(25.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.mangrovescan),
                contentDescription = "Mangrove Scan",
                Modifier.size(220.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    onGalleryClick()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = "Gallery",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Gallery", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            CircularButton(
                onClick = {
                    onCameraClick()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CircularButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier.size(80.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.camera),
            contentDescription = "Scan",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = modifier.size(25.dp)
        )
    }
}

@Throws(IOException::class)
fun createImageFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
}

fun rotateImageIfRequired(context: Context, image: Bitmap, imageUri: Uri): Bitmap {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    val exif = inputStream?.let { ExifInterface(it) }
    val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    inputStream?.close()

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270f)
        else -> image
    }
}

private fun rotateImage(image: Bitmap, degree: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree)
    return Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
}

fun classifyImage(image: Bitmap, context: Context, onResult: (String, String) -> Unit) {
    try {
        // Load the TFLite model
        val classifyModel = MangroveModel.newInstance(context)

        // Define input image size and create input buffer
        val imageSize = 28  // The model expects 28x28 images in grayscale
        val inputFeatureClassify = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 1), DataType.FLOAT32)
        val byteBufferClassify = ByteBuffer.allocateDirect(4 * imageSize * imageSize).apply {
            order(ByteOrder.nativeOrder())
        }

        // Preprocess the image
        val scaledBitmap = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
        val intValues = IntArray(imageSize * imageSize)
        scaledBitmap.getPixels(intValues, 0, imageSize, 0, 0, imageSize, imageSize)

        // Normalize pixel values and convert to grayscale (manual grayscale calculation)
        intValues.forEach { pixel ->
            val grayValue = ((pixel shr 16 and 0xFF) * 0.3f + (pixel shr 8 and 0xFF) * 0.59f + (pixel and 0xFF) * 0.11f).toInt()
            byteBufferClassify.putFloat(grayValue / 255.0f)  // Normalize to [0, 1]
        }

        // Load the normalized image into the input buffer
        inputFeatureClassify.loadBuffer(byteBufferClassify)

        // Run inference
        val outputs = classifyModel.process(inputFeatureClassify)
        val outputBuffer = outputs.outputFeature0AsTensorBuffer

        // Get model predictions
        val confidenceScores = outputBuffer.floatArray
        val classes = arrayOf("Bakawan", "Miyapi", "Non-Mangrove", "Pagatpat", "Pototan")
        val maxIndex = confidenceScores.indices.maxByOrNull { confidenceScores[it] } ?: -1

        if (maxIndex >= 0) {
            val predictedClass = classes[maxIndex]
            val confidence = confidenceScores[maxIndex]
            onResult(predictedClass, "%.2f".format(confidence))
        } else {
            onResult("Error", "Unable to classify")
        }

        // Close the model
        classifyModel.close()

    } catch (e: Exception) {
        // Handle exceptions and log errors
        Log.e("ClassifyImage", "Error during classification: ${e.message}", e)
        onResult("Error", e.message ?: "Unknown error")
    }
}

