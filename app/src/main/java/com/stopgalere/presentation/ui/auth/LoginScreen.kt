package com.stopgalere.presentation.ui.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.stopgalere.navigation.Route
import com.stopgalere.presentation.viewmodel.AuthViewModel
import com.stopgalere.util.AppConstants.TAG
import java.time.Instant

/**
 * Login screen — email/password + Google Sign-In + Apple Sign-In (via Firebase).
 *
 * Notes:
 * - Uses [AuthViewModel] (hiltViewModel) which is scoped to the NavBackStackEntry,
 *   so it is automatically cleared when the screen leaves the back stack.
 * - The Google Sign-In launcher holds a reference to a lambda, not an Activity,
 *   avoiding leaks.
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context  = LocalContext.current

    // Navigate to Main on successful login
    LaunchedEffect(uiState.successEvent) {
        if (uiState.successEvent == AuthSuccessEvent.LoggedIn) {
            viewModel.consumeSuccessEvent()
            navController.navigate(Route.Main.path) {
                popUpTo(Route.Login.path) { inclusive = true }
            }
        }
    }

    // ----------------------------------------------------------------
    // Google Sign-In launcher
    // ----------------------------------------------------------------
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            account?.idToken?.let { googleIdToken ->
                // Exchange Google ID token for Firebase credential, then get Firebase ID token
                val firebaseAuth = FirebaseAuth.getInstance()
                val credential   = GoogleAuthProvider.getCredential(googleIdToken, null)
                firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener { authResult ->
                        authResult.user?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
                            val firebaseIdToken = tokenResult.token ?: return@addOnSuccessListener
                            Log.d(TAG, "[${Instant.now()}] Google sign-in: got Firebase ID token")
                            viewModel.oauthSignIn(firebaseIdToken)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "[${Instant.now()}] Firebase credential exchange failed: ${e.message}", e)
                    }
            }
        }
    }

    LoginScreenContent(
        uiState         = uiState,
        onLogin         = { email, password -> viewModel.login(email, password) },
        onGoogleSignIn  = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_WEB_CLIENT_ID")   // replace with your OAuth 2.0 web client ID from Firebase
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(context, gso)
            googleSignInLauncher.launch(client.signInIntent)
        },
        onAppleSignIn = {
            // Apple Sign-In on Android uses Firebase's OAuthProvider
            val activity = context as? Activity ?: return@LoginScreenContent
            val provider = com.google.firebase.auth.OAuthProvider.newBuilder("apple.com")
                .setScopes(listOf("email", "name"))
                .build()
            FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider(activity, provider)
                .addOnSuccessListener { result ->
                    result.user?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
                        val firebaseIdToken = tokenResult.token ?: return@addOnSuccessListener
                        Log.d(TAG, "[${Instant.now()}] Apple sign-in: got Firebase ID token")
                        viewModel.oauthSignIn(firebaseIdToken)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "[${Instant.now()}] Apple sign-in failed: ${e.message}", e)
                }
        },
        onNavigateToRegister = {
            navController.navigate(Route.Register.path)
        },
        onClearError = viewModel::clearError
    )
}

@Composable
internal fun LoginScreenContent(
    uiState:             AuthUiState,
    onLogin:             (String, String) -> Unit,
    onGoogleSignIn:      () -> Unit,
    onAppleSignIn:       () -> Unit,
    onNavigateToRegister: () -> Unit,
    onClearError:        () -> Unit
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text  = "Sign In",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        OutlinedTextField(
            value         = email,
            onValueChange = { email = it },
            label         = { Text("Email") },
            leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().testTag("login_email")
        )

        // Password
        OutlinedTextField(
            value         = password,
            onValueChange = { password = it },
            label         = { Text("Password") },
            leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon  = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().testTag("login_password")
        )

        // Error message
        if (!uiState.errorMessage.isNullOrBlank()) {
            Text(
                text  = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("login_error")
            )
        }

        // Login button
        Button(
            onClick  = { onLogin(email, password) },
            enabled  = !uiState.loading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().testTag("login_btn")
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Sign In")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Google Sign-In
        OutlinedButton(
            onClick  = onGoogleSignIn,
            enabled  = !uiState.loading,
            modifier = Modifier.fillMaxWidth().testTag("login_google_btn")
        ) {
            Text("Continue with Google")
        }

        // Apple Sign-In
        OutlinedButton(
            onClick  = onAppleSignIn,
            enabled  = !uiState.loading,
            modifier = Modifier.fillMaxWidth().testTag("login_apple_btn")
        ) {
            Text("Continue with Apple")
        }

        // Navigate to register
        TextButton(
            onClick  = onNavigateToRegister,
            modifier = Modifier.testTag("login_register_link")
        ) {
            Text("Don't have an account? Sign Up")
        }
    }
}
