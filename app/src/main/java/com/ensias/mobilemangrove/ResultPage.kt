package com.ensias.mobilemangrove

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ensias.mobilemangrove.data.AppDatabase
import com.ensias.mobilemangrove.data.Photo
import com.ensias.mobilemangrove.data.Plant
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ResultPage(navController: NavController, result: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context, coroutineScope) }
    val plantDao = database.plantDao()
    val photoDao = database.photoDao()

    var plant by remember { mutableStateOf<Plant?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val relatedImageNames = listOf(
        "$result/related_image1.jpg",
        "$result/related_image2.jpg",
        "$result/related_image3.jpg",
        "$result/related_image4.jpg",
        "$result/related_image5.jpg",
        "$result/related_image6.jpg"
    )

    LaunchedEffect(result) {
        coroutineScope.launch {
            plant = plantDao.getPlantByName(result)
            isLoading = false

            plant?.let {
                val path = context.getExternalFilesDir(null)!!.absolutePath
                val imagePath = "$path/tempFileName.jpg"
                val imageFile = File(imagePath)

                if (imageFile.exists()) {
                    val photoBytes = imageFile.readBytes()
                    val photo = Photo(plant_id = it.id, photo = photoBytes)
                    photoDao.insert(photo)
                }

                imageFile.deleteOnExit()
            }
        }
    }

    val path = context.getExternalFilesDir(null)!!.absolutePath
    val imagePath = "$path/tempFileName.jpg"
    val image = BitmapFactory.decodeFile(imagePath)?.asImageBitmap()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = {
                    navController.navigate(Routes.scannerPage) {
                        popUpTo(Routes.resultPage) { inclusive = true }
                    }
                }) {
                    Image(painter = painterResource(id = R.drawable.arrow), contentDescription = "Back", Modifier.size(25.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                Modifier
                    .border(4.dp, Color.Black, CircleShape)
                    .clip(CircleShape)
                    .size(250.dp), Alignment.Center
            ) {
                image?.let {
                    Image(bitmap = it, contentDescription = null, modifier = Modifier.size(250.dp))
                }
            }

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    if(result == "Non-Mangrove"){
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "This is not a Mangrove Plant",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                            )
                        }
                    }else{
                        Text(text = "Local Name", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                        Text(text = result, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), modifier = Modifier.padding(bottom = 12.dp))
                        Text(text = "Scientific Name", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                        Text(text = plant?.scientificName ?: if (isLoading) "Loading..." else "Not Found", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), modifier = Modifier.padding(bottom = 12.dp))
                        Text(text = "Location", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                        Text(text = plant?.location ?: if (isLoading) "Loading..." else "Not Found", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), modifier = Modifier.padding(bottom = 12.dp))
                        Text(text = "Description", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                        Text(text = plant?.description ?: if (isLoading) "Loading..." else "Not Found", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp), modifier = Modifier.padding(bottom = 12.dp))

                    }
                 }
            }
        }

        if(result != "Non-Mangrove"){
            // Related Images Section as a separate item
            item {
                RelatedImagesSection(relatedImageNames)
            }
        }

    }
}

@Composable
fun RelatedImagesSection(imageNames: List<String>) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Related Images:",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (imageName in imageNames) {
                    val assetManager = context.assets
                    val inputStream = assetManager.open(imageName)
                    val relatedImage = BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                    relatedImage?.let {
                        Card(
                            modifier = Modifier.size(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                    inputStream.close()
                }
            }
        }
    }
}
