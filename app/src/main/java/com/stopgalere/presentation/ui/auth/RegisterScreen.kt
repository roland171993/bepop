package com.stopgalere.presentation.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.stopgalere.navigation.Route
import com.stopgalere.presentation.viewmodel.AuthViewModel
import java.io.File

/**
 * Registration screen: firstName, lastName, age, email, phone, password + profile photo.
 * After successful registration the user is navigated to Main.
 */
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel:     AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context  = LocalContext.current

    LaunchedEffect(uiState.successEvent) {
        if (uiState.successEvent == AuthSuccessEvent.LoggedIn) {
            viewModel.consumeSuccessEvent()
            navController.navigate(Route.Main.path) {
                popUpTo(Route.Register.path) { inclusive = true }
            }
        }
        if (uiState.successEvent == AuthSuccessEvent.ProfileUpdated) {
            // photo was uploaded just after register
            viewModel.consumeSuccessEvent()
        }
    }

    RegisterScreenContent(
        uiState     = uiState,
        onRegister  = { firstName, lastName, age, email, phone, password, photoFile ->
            viewModel.register(firstName, lastName, age, email, phone, password)
            // Photo upload is triggered after login in ProfileUpdated event, or eagerly:
            photoFile?.let { viewModel.uploadPhoto(it) }
        },
        onNavigateToLogin = { navController.popBackStack() },
        onClearError      = viewModel::clearError
    )
}

@Composable
internal fun RegisterScreenContent(
    uiState:          AuthUiState,
    onRegister:       (String, String, Int?, String, String, String, File?) -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearError:     () -> Unit
) {
    var firstName       by remember { mutableStateOf("") }
    var lastName        by remember { mutableStateOf("") }
    var ageText         by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var photoUri        by remember { mutableStateOf<Uri?>(null) }
    var photoFile       by remember { mutableStateOf<File?>(null) }

    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        photoUri = uri
        // Convert URI to a temp file that Retrofit can read
        val tmpFile = File(context.cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            tmpFile.outputStream().use { output -> input.copyTo(output) }
        }
        photoFile = tmpFile
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(4.dp))

        // ── Profile photo picker ──────────────────────────────────────
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { photoPickerLauncher.launch("image/*") }
                .testTag("register_photo_picker"),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model             = photoUri,
                    contentDescription = "Profile photo",
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.AddAPhoto,
                    contentDescription = "Add photo",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Text("Tap to add photo", style = MaterialTheme.typography.labelSmall)

        // ── Text fields ───────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value         = firstName,
                onValueChange = { firstName = it },
                label         = { Text("First name") },
                singleLine    = true,
                modifier      = Modifier.weight(1f).testTag("register_first_name")
            )
            OutlinedTextField(
                value         = lastName,
                onValueChange = { lastName = it },
                label         = { Text("Last name") },
                singleLine    = true,
                modifier      = Modifier.weight(1f).testTag("register_last_name")
            )
        }

        OutlinedTextField(
            value         = ageText,
            onValueChange = { ageText = it.filter { c -> c.isDigit() } },
            label         = { Text("Age") },
            leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().testTag("register_age")
        )

        OutlinedTextField(
            value         = email,
            onValueChange = { email = it },
            label         = { Text("Email") },
            leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().testTag("register_email")
        )

        OutlinedTextField(
            value         = phone,
            onValueChange = { phone = it },
            label         = { Text("Phone") },
            leadingIcon   = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().testTag("register_phone")
        )

        OutlinedTextField(
            value         = password,
            onValueChange = { password = it },
            label         = { Text("Password (min 8 chars, 1 uppercase, 1 number)") },
            leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon  = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().testTag("register_password")
        )

        // Error
        if (!uiState.errorMessage.isNullOrBlank()) {
            Text(
                text  = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("register_error")
            )
        }

        // Register button
        val canSubmit = !uiState.loading &&
                        email.isNotBlank() &&
                        password.length >= 8

        Button(
            onClick  = {
                onRegister(
                    firstName.trim(),
                    lastName.trim(),
                    ageText.toIntOrNull(),
                    email.trim(),
                    phone.trim(),
                    password,
                    photoFile
                )
            },
            enabled  = canSubmit,
            modifier = Modifier.fillMaxWidth().testTag("register_btn")
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Create Account")
            }
        }

        TextButton(
            onClick  = onNavigateToLogin,
            modifier = Modifier.testTag("register_login_link")
        ) {
            Text("Already have an account? Sign In")
        }
    }
}
