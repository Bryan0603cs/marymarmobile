package com.marymar.mobile.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.marymar.mobile.presentation.viewmodel.CartViewModel
import com.marymar.mobile.presentation.viewmodel.ProductsViewModel

@Composable
fun ProductListScreen(
    productsVm: ProductsViewModel,
    cartVm: CartViewModel
) {
    val state by productsVm.ui.collectAsState()

    LaunchedEffect(Unit) { productsVm.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Menú", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.query,
            onValueChange = productsVm::setQuery,
            label = { Text("Buscar producto o categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        if (state.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (state.error != null) {
            Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
        }

        val products = productsVm.filtered()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(products, key = { it.id }) { p ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!p.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = p.imageUrl,
                                contentDescription = p.name,
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.name, style = MaterialTheme.typography.titleMedium)
                            if (!p.category.isNullOrBlank()) {
                                Text(p.category ?: "", style = MaterialTheme.typography.bodySmall)
                            }
                            Text("$" + String.format("%.0f", p.price), style = MaterialTheme.typography.bodyMedium)
                        }

                        Button(onClick = { cartVm.add(p) }) {
                            Text("Agregar")
                        }
                    }
                }
            }
        }
    }
}
