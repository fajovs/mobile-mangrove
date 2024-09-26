package com.ensias.mobilemangrove


import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun HomePage(navController: NavController){
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Box( Modifier.height(50.dp).background(Color.Gray).fillMaxWidth()){
            Text(text = "HOME",Modifier.align(Alignment.Center), fontSize = 20.sp)
        }
        Spacer(Modifier.height(50.dp))
        Text(text = "WELCOME TO", fontSize = 30.sp)
        Text(text = "MOBILEMANGROVE", fontSize = 30.sp)
        Spacer(Modifier.height(200.dp))
        Button(onClick = {
            navController.navigate(Routes.scannerPage)
        }) {
            Text(text = "SCAN")
        }
        Spacer(Modifier.height(10.dp))
        Text(text = "SCANNER")

    }
}