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
import com.ensias.mobilemangrove.ml.MangroveCheck
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
        if (granted) {
            // Permission granted
        } else {
            // Handle permission denied
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
        val checkModel = MangroveCheck.newInstance(context)
        val imageSize = 224
        val inputFeatureCheck = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        val byteBufferCheck = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBufferCheck.order(ByteOrder.nativeOrder())
        val intValuesCheck = IntArray(imageSize * imageSize)
        val scaledBitmapCheck = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
        scaledBitmapCheck.getPixels(intValuesCheck, 0, imageSize, 0, 0, imageSize, imageSize)
        intValuesCheck.forEach { value ->
            byteBufferCheck.putFloat(((value shr 16) and 0xFF) * (1f / 255f))
            byteBufferCheck.putFloat(((value shr 8) and 0xFF) * (1f / 255f))
            byteBufferCheck.putFloat((value and 0xFF) * (1f / 255f))
        }
        inputFeatureCheck.loadBuffer(byteBufferCheck)
        val checkOutputs = checkModel.process(inputFeatureCheck)
        val checkOutputFeature = checkOutputs.outputFeature0AsTensorBuffer
        val checkConfidences = checkOutputFeature.floatArray
        val isMangrove = checkConfidences[0] > checkConfidences[1]
        checkModel.close()

        if (isMangrove) {
            val classifyModel = MangroveModel.newInstance(context)
            val inputFeatureClassify = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            val byteBufferClassify = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBufferClassify.order(ByteOrder.nativeOrder())
            val intValuesClassify = IntArray(imageSize * imageSize)
            val scaledBitmapClassify = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
            scaledBitmapClassify.getPixels(intValuesClassify, 0, imageSize, 0, 0, imageSize, imageSize)
            intValuesClassify.forEach { value ->
                byteBufferClassify.putFloat(((value shr 16) and 0xFF) * (1f / 255f))
                byteBufferClassify.putFloat(((value shr 8) and 0xFF) * (1f / 255f))
                byteBufferClassify.putFloat((value and 0xFF) * (1f / 255f))
            }
            inputFeatureClassify.loadBuffer(byteBufferClassify)
            val classifyOutputs = classifyModel.process(inputFeatureClassify)
            val classifyOutputFeature = classifyOutputs.outputFeature0AsTensorBuffer
            val classifyConfidences = classifyOutputFeature.floatArray
            val classes = arrayOf("Bakawan", "Miyapi", "Pagatpat", "Pototan")
            val maxPos = classifyConfidences.indices.maxByOrNull { classifyConfidences[it] } ?: -1
            val result = classes[maxPos]
            val confidence = classifyConfidences[maxPos]
            onResult(result, confidence.toString())
            classifyModel.close()
        } else {
            onResult("Non-Mangrove", "This is not a mangrove plant")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
