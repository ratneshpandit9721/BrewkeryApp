package com.brewkery.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brewkery.app.ui.screen.cart.CartScreen
import com.brewkery.app.ui.screen.details.DetailsScreen
import com.brewkery.app.ui.screen.home.HomeScreen
import com.brewkery.app.ui.screen.splash.SplashScreen
import com.brewkery.app.ui.screen.success.SuccessScreen

@Composable
fun BrewkeryNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Destinations.SPLASH,
    ) {
        composable(Destinations.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.SPLASH) { inclusive = true }
                    }
                },
            )
        }
        composable(Destinations.HOME) {
            HomeScreen(
                onItemClick = { itemId ->
                    navController.navigate(Destinations.details(itemId))
                },
                onCartClick = { navController.navigate(Destinations.CART) },
            )
        }
        composable(
            route = Destinations.DETAILS,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType }),
        ) {
            DetailsScreen(
                onBack = { navController.popBackStack() },
                onViewCart = { navController.navigate(Destinations.CART) },
            )
        }
        composable(Destinations.CART) {
            CartScreen(
                onBack = { navController.popBackStack() },
                onBrowseMenu = {
                    navController.popBackStack(Destinations.HOME, inclusive = false)
                },
                onOrderPlaced = { orderId ->
                    navController.navigate(Destinations.success(orderId)) {
                        popUpTo(Destinations.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            route = Destinations.SUCCESS,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
        ) {
            SuccessScreen(
                onBackToMenu = {
                    val popped = navController.popBackStack(Destinations.HOME, inclusive = false)
                    if (!popped) {
                        navController.navigate(Destinations.HOME) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                },
            )
        }
    }
}
