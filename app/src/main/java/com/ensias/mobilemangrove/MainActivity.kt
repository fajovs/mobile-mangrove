package com.ensias.mobilemangrove

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.ensias.mobilemangrove.data.AppDatabase
import com.ensias.mobilemangrove.ui.theme.MobileMangroveTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this, CoroutineScope(Dispatchers.IO)) }

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileMangroveTheme {

                lifecycleScope.launch {
                    database.plantDao().getAllPlants()
                }


                AppNavigation()
            }
        }
    }
}
