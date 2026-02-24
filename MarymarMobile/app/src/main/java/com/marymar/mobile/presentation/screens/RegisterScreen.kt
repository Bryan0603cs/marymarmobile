package com.marymar.mobile.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.marymar.mobile.domain.model.Role
import com.marymar.mobile.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    vm: AuthViewModel,
    onBack: () -> Unit
) {
    val state by vm.ui.collectAsState()

    var idNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") } // YYYY-MM-DD
    var role by remember { mutableStateOf(Role.CLIENTE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = idNumber,
            onValueChange = { idNumber = it },
            label = { Text("Número identificación") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Fecha nacimiento (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = role == Role.CLIENTE,
                onClick = { role = Role.CLIENTE },
                label = { Text("Cliente") }
            )
            FilterChip(
                selected = role == Role.MESERO,
                onClick = { role = Role.MESERO },
                label = { Text("Mesero") }
            )
        }

        if (state.error != null) {
            Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                vm.register(
                    idNumber = idNumber.trim(),
                    name = name.trim(),
                    email = email.trim(),
                    password = password,
                    phone = phone.trim(),
                    birthDateIso = birthDate.trim(),
                    role = role
                )
            },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text("Crear cuenta")
        }

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }

        Text(
            "En tu backend, el registro devuelve token directo (sin 2FA).",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
