package com.ensias.mobilemangrove




import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.homePage, builder = {
        composable(Routes.homePage) {
            HomePage(navController)
        }

        composable(Routes.scannerPage) {
            ScannerPage(navController)
        }

        composable(Routes.resultPage) {
                backStackEntry ->
            val result = backStackEntry.arguments?.getString("result") ?: ""



            ResultPage(navController, result)
        }

    })
        

}