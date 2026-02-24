package com.marymar.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.marymar.mobile.core.network.TokenProvider
import com.marymar.mobile.core.storage.SessionSnapshot
import com.marymar.mobile.core.storage.SessionStore
import com.marymar.mobile.domain.model.Role
import com.marymar.mobile.presentation.navigation.BottomNavBar
import com.marymar.mobile.presentation.navigation.Routes
import com.marymar.mobile.presentation.screens.*
import com.marymar.mobile.presentation.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionStore: SessionStore
    @Inject lateinit var tokenProvider: TokenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val session by sessionStore.sessionFlow.collectAsStateWithLifecycle(
                initialValue = SessionSnapshot(
                    token = null,
                    email = null,
                    name = null,
                    role = null,
                    userId = null,
                    loggedIn = false
                )
            )

            // Mantener token en memoria para el interceptor
            LaunchedEffect(session.token) {
                tokenProvider.setToken(session.token)
            }

            val nav = rememberNavController()

            // Compartido en toda la app (no se reinicia al cambiar de pantalla)
            val cartVm: CartViewModel = hiltViewModel()

            val isLoggedIn = session.loggedIn && !session.token.isNullOrBlank() && session.userId != null

            val sessionRole = runCatching { Role.valueOf(session.role ?: "CLIENTE") }.getOrDefault(Role.CLIENTE)
            val sessionUserId = session.userId ?: -1L

            val sessionVm: SessionViewModel = hiltViewModel()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(if (isLoggedIn) "Marymar" else "Marymar - Auth") },
                        actions = {
                            if (isLoggedIn) {
                                TextButton(onClick = {
                                    sessionVm.logout()
                                    nav.navigate(Routes.Login) {
                                        popUpTo(nav.graph.id) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }) {
                                    Text("Salir")
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    if (isLoggedIn) {
                        BottomNavBar(nav)
                    }
                }
            ) { innerPadding ->

                NavHost(
                    navController = nav,
                    startDestination = if (isLoggedIn) Routes.Products else Routes.Login,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    // AUTH
                    composable(Routes.Login) {
                        val vm: AuthViewModel = hiltViewModel()
                        val authState by vm.ui.collectAsState()

                        LaunchedEffect(authState.next) {
                            when (val next = authState.next) {
                                is AuthNext.GoToCode -> {
                                    vm.consumeNext()
                                    nav.navigate("${Routes.Code}?email=${next.email}")
                                }
                                AuthNext.LoggedIn -> {
                                    vm.consumeNext()
                                    nav.navigate(Routes.Products) {
                                        popUpTo(Routes.Login) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                null -> Unit
                            }
                        }

                        LoginScreen(
                            vm = vm,
                            onRegister = { nav.navigate(Routes.Register) }
                        )
                    }

                    composable(Routes.Register) {
                        val vm: AuthViewModel = hiltViewModel()
                        val authState by vm.ui.collectAsState()

                        LaunchedEffect(authState.next) {
                            if (authState.next == AuthNext.LoggedIn) {
                                vm.consumeNext()
                                nav.navigate(Routes.Products) {
                                    popUpTo(Routes.Register) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }

                        RegisterScreen(
                            vm = vm,
                            onBack = { nav.popBackStack() }
                        )
                    }

                    composable(
                        route = "${Routes.Code}?email={email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStack ->
                        val email = backStack.arguments?.getString("email") ?: ""
                        val vm: AuthViewModel = hiltViewModel()
                        val authState by vm.ui.collectAsState()

                        LaunchedEffect(authState.next) {
                            if (authState.next == AuthNext.LoggedIn) {
                                vm.consumeNext()
                                nav.navigate(Routes.Products) {
                                    popUpTo(Routes.Login) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }

                        CodeScreen(
                            vm = vm,
                            email = email,
                            onBack = { nav.popBackStack() }
                        )
                    }

                    // HOME
                    composable(Routes.Products) {
                        val productsVm: ProductsViewModel = hiltViewModel()
                        ProductListScreen(productsVm, cartVm)
                    }

                    composable(Routes.Cart) {
                        CartScreen(
                            cartVm = cartVm,
                            sessionUserId = sessionUserId,
                            sessionRole = sessionRole
                        )
                    }

                    composable(Routes.Orders) {
                        val ordersVm: OrdersViewModel = hiltViewModel()
                        OrdersScreen(
                            vm = ordersVm,
                            sessionUserId = sessionUserId,
                            sessionRole = sessionRole
                        )
                    }
                }
            }
        }
    }
}
