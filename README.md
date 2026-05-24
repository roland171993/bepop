# Stop Galère — Application Android

> **v4.1.2** — Application Android native Kotlin pour la plateforme Stop Galère (offres d'emploi, lettres de motivation, assistance client avec chat temps réel et appel vidéo WebRTC).

---

## Table des matières

1. [Stack technique](#stack-technique)
2. [Architecture](#architecture)
3. [Fonctionnalités](#fonctionnalités)
4. [Structure du projet](#structure-du-projet)
5. [Navigation](#navigation)
6. [Authentification](#authentification)
7. [Chat Assistance Client](#chat-assistance-client)
8. [Appel Vidéo WebRTC](#appel-vidéo-webrtc)
9. [Firebase & Crashlytics](#firebase--crashlytics)
10. [Configuration requise](#configuration-requise)
11. [Permissions Android](#permissions-android)
12. [Tests](#tests)
13. [Lancer le projet](#lancer-le-projet)
14. [AI Lettre de Motivation](#ai-lettre-de-motivation)
15. [AI Photo Professionnelle](#ai-photo-professionnelle)
16. [CI/CD — Fastlane](#cicd--fastlane)

---

## Stack technique

| Couche | Technologie |
|---|---|
| Langage | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Injection de dépendances | Hilt (Dagger) |
| Navigation | Navigation Compose (sealed `Route`) |
| Réseau REST | Retrofit 2 + OkHttp 4 + Gson |
| Temps réel | Socket.io client 2.1 |
| Appel vidéo | WebRTC (`stream-webrtc-android 1.1.0`) |
| Authentification | Firebase Auth (Google Sign-In + Apple ID) |
| Monitoring | Firebase Crashlytics |
| Persistance locale | Room 2.7 + SharedPreferences |
| Chargement d'images | Coil Compose |
| Pagination | Paging 3 |
| Push notifications | OneSignal 5 |
| OCR | Google ML Kit Text Recognition 16.0 |
| Recadrage image | Yalantis uCrop 2.2.8 |
| Tests unitaires | JUnit 4/5 + MockK + Turbine |
| Tests UI | Compose Test + Hilt Testing |
| Min SDK | 24 (Android 7.0) |
| Compile SDK | 35 (Android 15) |

---

## Architecture

```
MVVM strict — Clean Architecture en couches
```

```
data/          ← Sources de données (API, Room, SharedPrefs, Socket.io, WebRTC)
domain/        ← Interfaces + Use Cases (logique métier pure)
presentation/  ← ViewModels + Screens Compose
navigation/    ← NavGraph + Routes scellées
di/            ← Modules Hilt
util/          ← Constantes, intercepteurs, helpers
```

### Flux de données

```
Screen (Compose)
  └── collectAsStateWithLifecycle()
        └── ViewModel (StateFlow<UiState>)
              └── UseCase / SocketManager / WebRtcManager
                    ├── ApiService (Retrofit)
                    ├── SocketManager (Socket.io — chat + signalisation)
                    ├── WebRtcManager (PeerConnection WebRTC)
                    └── Room / Prefs
```

### Injection JWT automatique

`AuthInterceptor` (OkHttp) lit le token dans `Prefs` et injecte l'en-tête `Authorization: Bearer <token>` sur toutes les requêtes non publiques.

---

## Fonctionnalités

### Authentification & Profil
- Inscription : prénom, nom, âge, email, téléphone, photo de profil
- Connexion locale (email + mot de passe)
- OAuth Google Sign-In via Firebase Auth
- OAuth Apple ID via Firebase Auth (flow web intégré)
- Consultation et mise à jour du profil
- Upload de photo (multipart)
- Déconnexion avec effacement du token local

### Offres d'emploi
- Listing paginé avec recherche (Paging 3 + RemoteMediator)
- Détail d'une offre
- Cache Room pour navigation hors-ligne

### Lettres de motivation
- Listing paginé
- Détail avec contenu HTML
- Suppression

### Chat Assistance Client
- Connexion Socket.io authentifiée par JWT
- Historique de messages chargé via API REST
- Envoi de messages texte en temps réel (Socket.io)
- Partage de fichiers : images + documents (picker système)
- Bulles de message différenciées user / support
- Indicateur "est en train d'écrire…" avec debounce 2 s
- Badge de connexion Socket.io (vert / gris)
- Marquage automatique des messages lus
- Déduplication des messages (ID-based)
- **Bouton d'appel vidéo** (📹) dans la barre de titre
- **Dialogue appel entrant** avec Accepter / Refuser

### Appel Vidéo WebRTC *(nouveau)*
- Appel vidéo P2P entre utilisateur et agent support
- Signalisation SDP offer/answer via Socket.io
- Échange ICE candidates (Google STUN servers)
- Vue plein écran vidéo distante + PiP vidéo local
- Contrôles : muet, caméra on/off, retourner caméra, raccrocher
- Transitions d'état : IDLE → CALLING → CONNECTED → ENDED
- Overlay "Appel entrant" avec Accepter / Refuser
- Gestion propre des ressources WebRTC (release au raccroché)
- Toutes les erreurs remontées à Firebase Crashlytics

### Intro & Splash
- Écran d'intro wizard (Accompanist Pager)
- SplashScreen → redirige vers Login ou Main selon l'état de connexion

---

## Structure du projet

```
app/src/main/java/com/stopgalere/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   └── Prefs.kt
│   ├── model/
│   ├── remote/
│   │   ├── ApiService.kt               # Retrofit (auth + jobs + chat)
│   │   ├── NetworkMonitor.kt
│   │   ├── dto/
│   │   │   ├── AuthDto.kt
│   │   │   ├── AiDto.kt                 # DTOs + enum AiProvider ← nouveau
│   │   │   ├── CallDto.kt              # Payloads signalisation WebRTC ← nouveau
│   │   │   ├── ChatDto.kt
│   │   │   ├── CoverLetterDto.kt
│   │   │   └── JobDto.kt
│   │   ├── socket/
│   │   │   └── SocketManager.kt        # Socket.io — chat + signalisation WebRTC
│   │   └── webrtc/
│   │       └── WebRtcManager.kt        # PeerConnection, MediaStream, ICE ← nouveau
│   └── repository/
│       ├── AiRepository.kt              # ← nouveau
│       ├── AuthRepository.kt
│       ├── ChatRepository.kt
│       ├── CoverLetterRepository.kt
│       └── JobRepository.kt
├── domain/
│   ├── repository/
│   │   ├── AiRepoInterface.kt           # ← nouveau
│   │   ├── AuthRepoInterface.kt
│   │   ├── ChatRepoInterface.kt
│   │   ├── CoverLetterRepoInterface.kt
│   │   └── JobRepoInterface.kt
│   └── usecase/
│       ├── ai/
│       │   ├── GenerateCoverLetterUseCase.kt  # ← nouveau
│       │   └── GenerateProPhotoUseCase.kt     # ← nouveau
│       ├── auth/
│       │   ├── GetProfileUseCase.kt
│       │   ├── LoginUseCase.kt
│       │   ├── RegisterUseCase.kt
│       │   └── UpdateProfileUseCase.kt
│       └── chat/
│           ├── GetMessagesUseCase.kt
│           └── UploadChatFileUseCase.kt
├── presentation/
│   ├── ui/
│   │   ├── ai/
│   │   │   ├── AiCoverLetterUiState.kt  # ← nouveau
│   │   │   ├── AiCoverLetterScreen.kt   # ← nouveau
│   │   │   ├── AiProPhotoUiState.kt     # ← nouveau
│   │   │   └── AiProPhotoScreen.kt      # ← nouveau
│   │   ├── auth/
│   │   │   ├── AuthUiState.kt
│   │   │   ├── LoginScreen.kt
│   │   │   ├── RegisterScreen.kt
│   │   │   ├── ProfileScreen.kt
│   │   │   └── UpdateProfileScreen.kt
│   │   ├── call/
│   │   │   ├── CallUiState.kt          # CallState enum + CallUiState ← nouveau
│   │   │   └── VideoCallScreen.kt      # Écran appel vidéo WebRTC ← nouveau
│   │   ├── chat/
│   │   │   ├── ChatUiState.kt          # + incomingCall field
│   │   │   └── ChatScreen.kt           # + bouton appel + dialog entrant
│   │   ├── coverletter/
│   │   ├── intro/
│   │   ├── job/
│   │   ├── main/
│   │   └── splash/
│   └── viewmodel/
│       ├── AiCoverLetterViewModel.kt    # ← nouveau
│       ├── AiProPhotoViewModel.kt       # ← nouveau
│       ├── AuthViewModel.kt
│       ├── CallViewModel.kt            # Orchestre WebRTC + signalisation ← nouveau
│       ├── ChatViewModel.kt            # + observe callIncoming
│       ├── MainViewModel.kt
│       └── ...
├── navigation/
│   └── NavGraph.kt                     # + Route.VideoCall
├── di/
│   ├── AppModule.kt                    # + provideWebRtcManager
│   ├── DispatchersModule.kt
│   └── Qualifiers.kt
└── util/
    ├── AppConstants.kt
    └── AuthInterceptor.kt
```

---

## Navigation

| Route | Chemin | Description |
|---|---|---|
| `Splash` | `splash` | Écran de démarrage |
| `Intro` | `intro` | Wizard de bienvenue |
| `Login` | `login` | Connexion |
| `Register` | `register` | Inscription |
| `Profile` | `profile` | Profil utilisateur |
| `UpdateProfile` | `updateProfile` | Modifier le profil |
| `Main` | `main` | Écran principal (offres) |
| `Chat` | `chat/{userId}` | Chat assistance |
| `VideoCall` | `videoCall/{roomId}` | Appel vidéo WebRTC ← nouveau |
| `JobDetail` | `jobDetail/{jobId}` | Détail offre |
| `AiCoverLetter` | `aiCoverLetter/{jobId}/{jobTitle}/{jobDescription}/{jobCompany}` | Lettre IA ← nouveau |
| `AiProPhoto` | `aiProPhoto/{jobId}/{jobTitle}` | Photo pro IA ← nouveau |
| `CoverLetter` | `coverLetter` | Lettres de motivation |
| `CoverLetterDetail` | `coverLetterDetail/{id}` | Détail lettre |
| `About` | `about` | À propos |

### Extensions de navigation

```kotlin
navController.navigateToChat(userId)
navController.navigateToVideoCall(roomId)
navController.navigateToJobDetail(jobId)
navController.navigateToCoverLetterDetail(id)
navController.navigateSingleTopTo(route)
```

---

## Authentification

### Flux local
1. `RegisterScreen` → `AuthViewModel.register()` → `RegisterUseCase` → `AuthRepository`
2. Token JWT reçu → stocké dans `Prefs`
3. `AuthInterceptor` injecte le token sur les requêtes suivantes

### Flux OAuth (Google / Apple)
1. `LoginScreen` déclenche `GoogleSignIn` / `OAuthProvider("apple.com")` via Firebase Auth
2. Firebase retourne un **ID Token**
3. `AuthViewModel.oauthSignIn(idToken)` → `POST /api/auth/oauth`
4. Token JWT applicatif reçu → stocké dans `Prefs`

---

## Chat Assistance Client

### SocketManager (Singleton Hilt)

```kotlin
// Connexion (JWT lu automatiquement depuis Prefs)
socketManager.connect()

// Flows chat
socketManager.isConnected:     StateFlow<Boolean>
socketManager.newMessages:     SharedFlow<MessageDto>
socketManager.userTyping:      SharedFlow<UserTypingEvent>
socketManager.messagesRead:    SharedFlow<MessagesReadEvent>

// Flows signalisation vidéo
socketManager.callIncoming:      SharedFlow<CallOfferEvent>
socketManager.callAnswered:      SharedFlow<CallAnswerEvent>
socketManager.callRejected:      SharedFlow<CallRejectEvent>
socketManager.callEnded:         SharedFlow<CallEndEvent>
socketManager.remoteIceCandidate: SharedFlow<IceCandidateEvent>
```

---

## Appel Vidéo WebRTC

### Flux d'un appel sortant

```
1. Utilisateur appuie sur 📹 dans ChatScreen
2. Navigation vers VideoCallScreen(roomId)
3. CallViewModel.startCall(roomId)
   └── WebRtcManager.createOffer { sdp → socketManager.emitCallOffer(roomId, sdp) }
4. Agent reçoit call_incoming → AppellantScreen affiche l'overlay "Appel entrant"
5. Agent accepte → setRemoteOffer(sdp) + createAnswer { sdp → emitCallAnswer }
6. Utilisateur reçoit call_answered → setRemoteAnswer → state = CONNECTED
7. Échange ICE candidates (WebRtcManager.onIceCandidate callback → emitIceCandidate)
8. Connexion P2P WebRTC établie — vidéo fluide
```

### WebRtcManager (Singleton Hilt)

```kotlin
webRtcManager.initLocalStream(localRenderer)   // capture caméra + micro
webRtcManager.setRemoteRenderer(remoteRenderer) // attache le flux distant
webRtcManager.createOffer { sdp -> ... }
webRtcManager.setRemoteOffer(sdp)
webRtcManager.createAnswer { sdp -> ... }
webRtcManager.setRemoteAnswer(sdp)
webRtcManager.addRemoteIceCandidate(candidate)
webRtcManager.switchCamera()
webRtcManager.toggleMic()    // retourne true si muet
webRtcManager.toggleCamera() // retourne true si caméra désactivée
webRtcManager.release()      // libère toutes les ressources
```

### États de l'appel

```kotlin
enum class CallState { IDLE, CALLING, INCOMING, CONNECTED, ENDED }
```

### STUN servers configurés

```
stun:stun.l.google.com:19302
stun:stun1.l.google.com:19302
```

---

## AI Lettre de Motivation

Accessible depuis l'écran **Détail Offre** via le bouton **"✨ Lettre IA"**.

### Fonctionnalités
- Sélection ou capture de **1 à 5 photos** de documents (diplômes, certificats)
- **OCR automatique** via Google ML Kit Text Recognition
- **Prompt vocal** : enregistrement → Speech-to-Text → contexte supplémentaire
- Sélection du **provider IA** : ChatGPT (GPT-4o) / Gemini (1.5 Flash) / DeepSeek
- Génération de la lettre via `POST /api/ai/cover-letter`
- **Text-to-Speech** pour lire la lettre générée
- Envoi optionnel **par email** (SMTP)

### Technologies
| Fonctionnalité | Technologie |
|---|---|
| OCR | Google ML Kit `TextRecognition` + `InputImage.fromFilePath` |
| Speech-to-Text | Android `SpeechRecognizer` via `RecognizerIntent` |
| Text-to-Speech | Android `TextToSpeech` (langue FR) |
| Upload photos | `ActivityResultContracts.GetMultipleContents` + `TakePicture` |
| FileProvider | `androidx.core.content.FileProvider` |

### Flux
```
JobDetailScreen → [✨ Lettre IA]
  └── AiCoverLetterScreen
        ├── Pick/photo documents → OCR (ML Kit) → ocrTexts[]
        ├── Enregistrement vocal → STT → voiceTranscript
        ├── Sélection provider (ChatGPT / Gemini / DeepSeek)
        └── POST /api/ai/cover-letter
              └── Résultat → TTS + copier + email optionnel
```

### AiProvider enum
```kotlin
enum class AiProvider(val value: String, val label: String) {
    OPENAI("openai", "ChatGPT"),
    GEMINI("gemini", "Gemini"),
    DEEPSEEK("deepseek", "DeepSeek")
}
```

---

## AI Photo Professionnelle

Accessible depuis l'écran **Détail Offre** via le bouton **"📸 Photo IA"**.

### Fonctionnalités
- Sélection ou capture d'**1 photo** (galerie ou caméra)
- **Recadrage** avec Yalantis **uCrop** (ratio 1:1, max 512×512)
- Options de style : **avec / sans costume**, style de fond personnalisable
- **Prompt vocal** : description du look souhaité → Speech-to-Text
- Génération via **DALL·E 3** (OpenAI) : `POST /api/ai/professional-photo`
- Affichage du résultat avec Coil `AsyncImage`
- Envoi optionnel **par email**
- **TTS** : confirmation vocale à la fin de la génération

### Technologies
| Fonctionnalité | Technologie |
|---|---|
| Recadrage | Yalantis uCrop 2.2.8 |
| Image Generation | OpenAI DALL·E 3 (via backend) |
| Speech-to-Text | Android `SpeechRecognizer` |
| Text-to-Speech | Android `TextToSpeech` |
| Affichage image | Coil `AsyncImage` |

### Flux uCrop
```kotlin
val intent = UCrop.of(sourceUri, destinationUri)
    .withAspectRatio(1f, 1f)
    .withMaxResultSize(512, 512)
    .getIntent(context)
uCropLauncher.launch(intent)
```

### États
```
JobDetailScreen → [📸 Photo IA]
  └── AiProPhotoScreen
        ├── Pick/photo → uCrop → croppedImageUri
        ├── Style (avec/sans costume + fond)
        ├── Enregistrement vocal → STT → voiceDescription
        └── POST /api/ai/professional-photo (multipart)
              └── Résultat DALL·E → AsyncImage + email optionnel
```

---

## Firebase & Crashlytics

### google-services.json

Placer le fichier `google-services.json` fourni par la console Firebase dans :
```
app/google-services.json
```
> ⚠️ Ne jamais committer ce fichier dans un dépôt public.

### Crashlytics

- **Debug** : collection désactivée (`isCrashlyticsCollectionEnabled = false`)
- **Release** : activée automatiquement
- Toutes les exceptions attrapées dans `AuthViewModel`, `ChatViewModel` et `CallViewModel` sont remontées via `FirebaseCrashlytics.getInstance().recordException(e)`
- `WebRtcManager` enregistre aussi les erreurs de PeerConnection et de capture caméra

---

## Configuration requise

| Fichier | Variable | Description |
|---|---|---|
| `util/AppConstants.kt` | `BASE_URL` | URL de l'API (prod ou dev) |
| `util/AppConstants.kt` | `CASE_DEBUG` | `true` en dev, `false` en prod |
| `app/google-services.json` | — | Fichier Firebase réel |

> **Variables IA** : les clés API (OpenAI, Gemini, DeepSeek) et la configuration SMTP sont gérées côté backend. L'app Android pointe uniquement vers `BASE_URL` dans `AppConstants.kt` — aucune clé API n'est stockée côté client.

```kotlin
// AppConstants.kt
const val DEV_URL  = "http://192.168.100.22:3000/api/"
const val PROD_URL = "https://stopgalere.rolandassoh.com/api/"
const val BASE_URL = PROD_URL   // changer ici pour le dev
```

---

## Permissions Android

```xml
<!-- Réseau -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Appel vidéo WebRTC -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<!-- Galerie (fonctionnalités IA) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Push notifications -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

> `CAMERA` et `RECORD_AUDIO` sont des permissions **dangereuses** (runtime). Elles doivent être demandées à l'utilisateur avant d'ouvrir l'écran d'appel. Utiliser `rememberLauncherForActivityResult(RequestMultiplePermissions)` dans `VideoCallScreen`.

---

## Tests

### Tests unitaires (JVM)

```bash
./gradlew test
```

| Fichier | Tests | Couverture |
|---|---|---|
| `AuthViewModelTest.kt` | 7 | Login, register, OAuth, profil, erreurs |
| `ChatViewModelTest.kt` | 8 | enterRoom, sendMessage, socket events, upload, erreurs |
| `CallViewModelTest.kt` | 7 | startCall → CALLING, callAnswered → CONNECTED, callRejected/callEnded → ENDED, acceptCall, rejectCall, endCall |
| `AiCoverLetterViewModelTest.kt` | 6 | addImageUris, selectProvider, generate success/error, voiceTranscript, clearError |
| `AiProPhotoViewModelTest.kt` | 7 | setSelectedImage, setCroppedImage, setWithSuit, generate success/error, selectProvider, clearError |

Technologies : **MockK** + **Turbine** (test des Flows) + `StandardTestDispatcher`.

### Tests instrumentés (Compose UI)

```bash
./gradlew connectedAndroidTest
```

| Fichier | Tests | Couverture |
|---|---|---|
| `LoginScreenTest.kt` | 7 | Rendu, validation, callbacks, OAuth buttons |
| `ChatScreenTest.kt` | 8 | Rendu, loading, messages, send, typing, attach |
| `VideoCallScreenTest.kt` | 6 | INCOMING overlay, CONNECTED barre de contrôles, CALLING spinner, callbacks |

Technologies : `createComposeRule()` + **testTags** + **Hilt Test Runner**.

### Hilt Test Runner

```kotlin
testInstrumentationRunner = "com.stopgalere.HiltTestRunner"
```

---

## Lancer le projet

### Prérequis
- Android Studio Hedgehog ou supérieur
- JDK 17
- Fichier `app/google-services.json` valide

### Build & lancement

```bash
# Debug
./gradlew assembleDebug
./gradlew installDebug

# Release
./gradlew assembleRelease

# Tests unitaires
./gradlew test

# Tests instrumentés (émulateur ou appareil connecté)
./gradlew connectedAndroidTest
```

### Modifier l'URL de l'API

```kotlin
// app/src/main/java/com/stopgalere/util/AppConstants.kt
const val BASE_URL = DEV_URL    // pour pointer sur le serveur local
```

---

## CI/CD — Fastlane

Pipeline automatisé pour tester, capturer des screenshots et publier sur le Google Play Store.

### Installation

```bash
cd /chemin/vers/bepop
gem install bundler
bundle install
```

### Lanes disponibles

| Commande | Description |
|---|---|
| `bundle exec fastlane test` | Tests unitaires JVM (`./gradlew test`) |
| `bundle exec fastlane instrumented_tests` | Tests instrumentés sur émulateur/appareil |
| `bundle exec fastlane screenshots` | Captures auto Play Store (Screengrab, fr-FR + en-US) |
| `bundle exec fastlane build_release` | Compile l'AAB signé + incrémente versionCode |
| `bundle exec fastlane deploy_internal` | Build + upload track **internal** (draft) |
| `bundle exec fastlane deploy_beta` | Promote internal → **beta** |
| `bundle exec fastlane deploy_production` | Promote beta → **production** (rollout 10 %) |
| `bundle exec fastlane deploy` | Pipeline complet : test + build + internal |

### Structure Fastlane

```
fastlane/
├── Appfile              # package_name + chemin clé Play Store
├── Fastfile             # Définition des 8 lanes
├── Screengrabfile       # Config captures automatiques
├── README.md            # Documentation détaillée
├── .gitignore           # Exclut clés et keystore
├── metadata/
│   └── android/
│       ├── fr-FR/       # Fiche Play Store française
│       │   ├── title.txt
│       │   ├── short_description.txt
│       │   ├── full_description.txt
│       │   └── changelogs/default.txt
│       └── en-US/       # Fiche Play Store anglaise
Gemfile                  # Dépendances Ruby (fastlane ~2.220, screengrab ~1.2)
.env.fastlane            # Template variables d'environnement (non commité)
.github/workflows/
└── fastlane-ci.yml      # CI GitHub Actions (test + deploy_internal sur main)
```

### Variables d'environnement requises

```bash
# Copier et remplir
cp .env.fastlane .env

# Contenu :
PLAY_STORE_JSON_KEY=fastlane/play-store-key.json
KEYSTORE_PATH=fastlane/keystore/stopgalere-release.jks
KEYSTORE_PASSWORD=votre_password
KEY_ALIAS=stopgalere
KEY_PASSWORD=votre_key_password
```

### Mise en service Google Play

1. [Google Play Console](https://play.google.com/console) → **Setup → API access → Create service account**
2. Rôle : **Release Manager** → télécharger le JSON
3. Placer dans `fastlane/play-store-key.json` (**ne jamais committer**)

### Keystore de signature

```bash
keytool -genkey -v \
  -keystore fastlane/keystore/stopgalere-release.jks \
  -alias stopgalere \
  -keyalg RSA -keysize 2048 -validity 10000
```

### Secrets GitHub Actions

Configurer dans **Settings → Secrets and variables → Actions** :

| Secret | Génération |
|---|---|
| `PLAY_STORE_JSON_KEY_BASE64` | `base64 -i fastlane/play-store-key.json` |
| `KEYSTORE_BASE64` | `base64 -i fastlane/keystore/stopgalere-release.jks` |
| `KEYSTORE_PASSWORD` | Mot de passe keystore |
| `KEY_ALIAS` | `stopgalere` |
| `KEY_PASSWORD` | Mot de passe de la clé |

### Workflow de publication recommandé

```
commit → push → CI (tests)
                    ↓
           deploy_internal (draft)
                    ↓
            QA / tests manuels
                    ↓
           fastlane deploy_beta
                    ↓
      fastlane deploy_production (10% rollout)
```
