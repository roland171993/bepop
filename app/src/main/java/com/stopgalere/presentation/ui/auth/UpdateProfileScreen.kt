package com.stopgalere.presentation.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stopgalere.presentation.viewmodel.AuthViewModel

/**
 * Update profile screen — editable fields: firstName, lastName, age, phone.
 * Email is read-only (shown for reference only).
 * On success, navigates back to Profile.
 */
@Composable
fun UpdateProfileScreen(
    navController: NavHostController,
    viewModel:     AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.successEvent) {
        if (uiState.successEvent == AuthSuccessEvent.ProfileUpdated) {
            viewModel.consumeSuccessEvent()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        UpdateProfileContent(
            uiState      = uiState,
            initialUser  = uiState.user,
            onSave       = { firstName, lastName, age, phone ->
                viewModel.updateProfile(firstName, lastName, age, phone)
            },
            onClearError = viewModel::clearError,
            modifier     = Modifier.padding(paddingValues)
        )
    }
}

@Composable
internal fun UpdateProfileContent(
    uiState:     AuthUiState,
    initialUser: com.stopgalere.data.remote.dto.UserDto?,
    onSave:      (String?, String?, Int?, String?) -> Unit,
    onClearError: () -> Unit,
    modifier:    Modifier = Modifier
) {
    var firstName by remember(initialUser?.id) { mutableStateOf(initialUser?.firstName ?: "") }
    var lastName  by remember(initialUser?.id) { mutableStateOf(initialUser?.lastName  ?: "") }
    var ageText   by remember(initialUser?.id) { mutableStateOf(initialUser?.age?.toString() ?: "") }
    var phone     by remember(initialUser?.id) { mutableStateOf(initialUser?.phone ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value         = firstName,
                onValueChange = { firstName = it },
                label         = { Text("First name") },
                singleLine    = true,
                modifier      = Modifier.weight(1f).testTag("update_first_name")
            )
            OutlinedTextField(
                value         = lastName,
                onValueChange = { lastName = it },
                label         = { Text("Last name") },
                singleLine    = true,
                modifier      = Modifier.weight(1f).testTag("update_last_name")
            )
        }

        OutlinedTextField(
            value         = ageText,
            onValueChange = { ageText = it.filter { c -> c.isDigit() } },
            label         = { Text("Age") },
            leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().testTag("update_age")
        )

        OutlinedTextField(
            value         = phone,
            onValueChange = { phone = it },
            label         = { Text("Phone") },
            leadingIcon   = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().testTag("update_phone")
        )

        // Read-only email
        initialUser?.let {
            OutlinedTextField(
                value         = it.email,
                onValueChange = {},
                label         = { Text("Email (read-only)") },
                enabled       = false,
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )
        }

        if (!uiState.errorMessage.isNullOrBlank()) {
            Text(
                text  = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("update_error")
            )
        }

        Button(
            onClick  = {
                onSave(
                    firstName.trimOrNull(),
                    lastName.trimOrNull(),
                    ageText.toIntOrNull(),
                    phone.trimOrNull()
                )
            },
            enabled  = !uiState.loading,
            modifier = Modifier.fillMaxWidth().testTag("update_save_btn")
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Save Changes")
            }
        }
    }
}

private fun String.trimOrNull(): String? = trim().ifBlank { null }
