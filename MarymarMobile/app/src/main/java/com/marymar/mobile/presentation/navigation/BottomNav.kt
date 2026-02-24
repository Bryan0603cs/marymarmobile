package com.marymar.mobile.presentation.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    val backStack = navController.currentBackStackEntryAsState().value
    val route = backStack?.destination?.route

    val items = listOf(
        Routes.Products to "Productos",
        Routes.Cart to "Carrito",
        Routes.Orders to "Pedidos"
    )

    NavigationBar(modifier = modifier) {
        items.forEach { (r, label) ->
            NavigationBarItem(
                selected = route == r,
                onClick = { navController.navigate(r) { launchSingleTop = true } },
                label = { Text(label) },
                icon = {}
            )
        }
    }
}
