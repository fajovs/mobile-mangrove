//package com.ensias.mobilemangrove
//
//import android.Manifest
//
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.provider.MediaStore
//import androidx.activity.compose.rememberLauncherForActivityResult
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.Alignment
//import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.res.painterResource
//
//import androidx.core.content.ContextCompat
//import androidx.navigation.NavController
//
//
//
//@Composable
//fun sScannerPage(navController: NavController) {
//    val context = LocalContext.current
//
//
//    val requestPermissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { _: Boolean -> }
//
//    var permissionRequested by remember { mutableStateOf(false) }
//
//    fun checkAndRequestPermission(permission: String, action: () -> Unit) {
//        when {
//            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
//                action()
//            }
//            else -> {
//                if (permissionRequested) {
//                    requestPermissionLauncher.launch(permission)
//                } else {
//                    permissionRequested = true
//                    requestPermissionLauncher.launch(permission)
//                }
//            }
//        }
//    }
//
//    fun onGalleryClick() {
//        checkAndRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
//            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            context.startActivity(galleryIntent)
//        }
//    }
//
//    fun onCameraClick() {
//        checkAndRequestPermission(Manifest.permission.CAMERA) {
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            context.startActivity(cameraIntent)
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.Start
//        ) {
//            IconButton(
//                onClick = {
//                    navController.navigate(Routes.homePage)
//                }
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.arrow),
//                    contentDescription = "Back",
//                    Modifier.size(25.dp)
//                )
//            }
//
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            contentAlignment = Alignment.Center
//        ) {
//
//            Image(
//                painter = painterResource(id = R.drawable.mangrovescan),
//                contentDescription = "Mangrove Scan",
//                Modifier.size(220.dp)
//            )
//        }
//
//
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//
//        ) {
//            Button(
//                onClick = {
//                    onGalleryClick()
//                },
//                shape = RoundedCornerShape(8.dp),
//                modifier = Modifier
//                    .width(120.dp)
//                    .height(80.dp)
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.gallery),
//                        contentDescription = "Gallery",
//                        colorFilter = ColorFilter.tint(Color.White),
//                        modifier = Modifier.size(25.dp)
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text("Gallery",fontSize = 12.sp)
//                }
//            }
//
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            CircularButton(
//                onClick = {
//                    onCameraClick()
//                }
//            )
//
//
//        }
//        Spacer(modifier = Modifier.height(132.dp))
//    }
//}
//
//@Composable
//fun CircularButton(
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Button(
//        onClick = onClick,
//        shape = CircleShape,
//        modifier = modifier
//            .size(80.dp),
//
//
//        ) {
//        Image(
//            painter = painterResource(id = R.drawable.camera),
//            contentDescription = "Scan",
//            colorFilter = ColorFilter.tint(Color.White),
//            modifier = modifier.size(25.dp)
//
//
//
//        )
//
//    }
//}
//
//
//
