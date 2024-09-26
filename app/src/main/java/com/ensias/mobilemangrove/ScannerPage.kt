package com.ensias.mobilemangrove


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScannerPage(navController: NavController) {
    val context = LocalContext.current




    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            // Permission granted
        } else {
            // Handle permission denied
        }
    }

    var permissionRequested by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var resultText by remember { mutableStateOf("Result will be shown here") }
    var confidenceText by remember { mutableStateOf("Confidence will be shown here") }

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
            imageBitmap?.let { bitmap ->
                classifyImage(bitmap, context) { result, confidence ->
                    resultText = result
                    confidenceText = confidence

                    val path = context.getExternalFilesDir(null)!!.absolutePath
                    val image = imageBitmap
                    val tempFile = File(path , "tempFileName.jpg")
                    val fOut = FileOutputStream(tempFile)
                    image?.compress(Bitmap.CompressFormat.JPEG , 100 , fOut)

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
                bitmap?.let { imageBitmap ->
                    classifyImage(imageBitmap, context) { result, confidence ->
                        resultText = result
                        confidenceText = confidence


                        val path = context.getExternalFilesDir(null)!!.absolutePath
                        val image = imageBitmap
                        val tempFile = File(path , "tempFileName.jpg")
                        val fOut = FileOutputStream(tempFile)
                        image.compress(Bitmap.CompressFormat.JPEG , 100 , fOut)

                        fOut.close()


                        navController.navigate("ResultPage/${resultText}")
                    }
                }
            }
        }
    }

    fun onGalleryClick() {
        checkAndRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
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
                    navController.navigate("homePage") // Update route accordingly
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
        modifier = modifier
            .size(80.dp),
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


fun classifyImage(image: Bitmap, context: Context, onResult: (String, String) -> Unit) {
    try {
        val model = MangroveModel.newInstance(context)

        val imageSize = 224
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(imageSize * imageSize)
        val scaledBitmap = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
        scaledBitmap.getPixels(intValues, 0, imageSize, 0, 0, imageSize, imageSize)

        var pixel = 0
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255f))
                byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255f))
                byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
            }
        }

        inputFeature0.loadBuffer(byteBuffer)
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidences = outputFeature0.floatArray
        val classes = arrayOf("Bakawan", "Miyapi", "Pagatpat", "Pototan")
        val maxPos = confidences.indices.maxByOrNull { confidences[it] } ?: 0
        val result = classes[maxPos]


        val confidenceText = confidences.mapIndexed { index, confidence ->
            "${classes[index]}: ${confidence * 100}%"
        }.joinToString("\n")

        onResult(result, confidenceText)

        model.close()
    } catch (e: IOException) {
        Log.e("ImageClassification", "Error classifying image", e)
        onResult("Error", "Unable to classify image")
    }
}
