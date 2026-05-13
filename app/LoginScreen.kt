package com.latha.ksheerasagara

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current // For showing Toast alerts

    // Validation Logic
    val isPhoneValid = username.length == 10 && username.all { it.isDigit() }
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()
    val isPasswordValid = password.length >= 6

    // Combined validation state
    val canLogin = (isPhoneValid || isEmailValid) && isPasswordValid

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.farm_bg),
                contentDescription = "Farm Background",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Ksheera-Sagara",
                            fontSize = 28.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Username Field with Error Text
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Phone (10 digits) or Email") },
                            isError = username.isNotEmpty() && !isPhoneValid && !isEmailValid,
                            supportingText = {
                                if (username.isNotEmpty() && !isPhoneValid && !isEmailValid) {
                                    Text("Enter 10 digits or a valid email", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Password Field with Error Text
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password (Min 6 chars)") },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = password.isNotEmpty() && !isPasswordValid,
                            supportingText = {
                                if (password.isNotEmpty() && !isPasswordValid) {
                                    Text("Password must be at least 6 characters", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (canLogin) {
                                    onLoginSuccess()
                                } else {
                                    // Block and Alert
                                    Toast.makeText(context, "Please fix the errors above", Toast.LENGTH_SHORT).show()
                                }
                            },
                            // Button stays disabled until format is correct
                            enabled = canLogin,
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        ) {
                            Text("LOGIN", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}