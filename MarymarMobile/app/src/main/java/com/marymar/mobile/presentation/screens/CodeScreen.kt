package com.marymar.mobile.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marymar.mobile.presentation.viewmodel.AuthViewModel

@Composable
fun CodeScreen(
    vm: AuthViewModel,
    email: String,
    onBack: () -> Unit
) {
    val state by vm.ui.collectAsState()
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Verificación", style = MaterialTheme.typography.headlineSmall)
        Text("Te enviamos un código a: $email")

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Código") },
            modifier = Modifier.fillMaxWidth()
        )

        if (state.error != null) {
            Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { vm.validateCode(email, code.trim()) },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text("Validar")
        }

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
