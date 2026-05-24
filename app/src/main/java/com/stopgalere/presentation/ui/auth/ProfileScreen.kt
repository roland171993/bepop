package com.stopgalere.presentation.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.stopgalere.navigation.Route
import com.stopgalere.presentation.viewmodel.AuthViewModel
import java.io.File

/**
 * Profile screen — shows current user's data and offers:
 *  - Photo change (camera-roll picker)
 *  - Edit profile (navigate to UpdateProfileScreen)
 *  - Logout
 */
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel:     AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context  = LocalContext.current

    // Load profile when entering the screen
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(uiState.successEvent) {
        when (uiState.successEvent) {
            AuthSuccessEvent.LoggedOut -> {
                viewModel.consumeSuccessEvent()
                navController.navigate(Route.Login.path) {
                    popUpTo(0) { inclusive = true }
                }
            }
            AuthSuccessEvent.ProfileUpdated -> viewModel.consumeSuccessEvent()
            else -> Unit
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val tmpFile = File(context.cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            tmpFile.outputStream().use { output -> input.copyTo(output) }
        }
        viewModel.uploadPhoto(tmpFile)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("profile_logout_btn")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.loading && uiState.user == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.user != null -> {
                val user = uiState.user!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile photo
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { photoPickerLauncher.launch("image/*") }
                            .testTag("profile_photo"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!user.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model              = user.photoUrl,
                                contentDescription = "Profile photo",
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Add photo",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Text("Tap photo to change", style = MaterialTheme.typography.labelSmall)

                    if (uiState.loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    // User info
                    ProfileRow(label = "First name", value = user.firstName)
                    ProfileRow(label = "Last name",  value = user.lastName)
                    ProfileRow(label = "Age",        value = user.age?.toString() ?: "—")
                    ProfileRow(label = "Email",      value = user.email)
                    ProfileRow(label = "Phone",      value = user.phone.ifBlank { "—" })
                    ProfileRow(label = "Provider",   value = user.authProvider)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick  = { navController.navigate(Route.UpdateProfile.path) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_edit_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Profile")
                    }

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Text(
                            text  = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.errorMessage ?: "Unable to load profile.")
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    HorizontalDivider()
}
