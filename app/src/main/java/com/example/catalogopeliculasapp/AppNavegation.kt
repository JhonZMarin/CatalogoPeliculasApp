package com.example.catalogopeliculasapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(navController)
        }

        composable("lists") {
            ListsScreen(navController)
        }

        composable(
            "details/{title}/{overview}/{posterPath}"
        ) { backStackEntry ->
            DetailsScreen(
                navController,
                title = backStackEntry.arguments?.getString("title") ?: "",
                overview = backStackEntry.arguments?.getString("overview") ?: "",
                posterPath = backStackEntry.arguments?.getString("posterPath") ?: ""
            )
        }

    }
    }

