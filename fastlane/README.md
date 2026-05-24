# Fastlane — Stop Galère Android

## Lanes disponibles

| Lane | Commande | Description |
|---|---|---|
| Tests unitaires | `bundle exec fastlane test` | Lance `./gradlew test` |
| Tests instrumentés | `bundle exec fastlane instrumented_tests` | Lance `./gradlew connectedAndroidTest` |
| Screenshots | `bundle exec fastlane screenshots` | Capture auto avec Screengrab |
| Build Release | `bundle exec fastlane build_release` | Compile l'AAB signé |
| Deploy Internal | `bundle exec fastlane deploy_internal` | Build + upload track internal |
| Deploy Beta | `bundle exec fastlane deploy_beta` | Promeut internal → beta |
| Deploy Production | `bundle exec fastlane deploy_production` | Promeut beta → production (10%) |
| Pipeline complet | `bundle exec fastlane deploy` | test + build + internal |

## Prérequis

### 1. Installer Fastlane

```bash
# Avec Bundler (recommandé)
gem install bundler
cd /chemin/vers/bepop
bundle install

# OU directement
gem install fastlane
```

### 2. Préparer les credentials Google Play

1. Aller sur [Google Play Console](https://play.google.com/console) → Setup → API access
2. Créer un **Service Account** avec le rôle "Release Manager"
3. Télécharger le fichier JSON et le placer dans `fastlane/play-store-key.json`
4. Ne jamais committer ce fichier !

### 3. Préparer le Keystore de signature

```bash
# Générer un keystore si vous n'en avez pas
keytool -genkey -v \
  -keystore fastlane/keystore/stopgalere-release.jks \
  -alias stopgalere \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### 4. Variables d'environnement

Créer un fichier `.env` à la racine (non commité) :

```bash
PLAY_STORE_JSON_KEY=fastlane/play-store-key.json
KEYSTORE_PATH=fastlane/keystore/stopgalere-release.jks
KEYSTORE_PASSWORD=votre_password
KEY_ALIAS=stopgalere
KEY_PASSWORD=votre_key_password
```

Puis lancer avec :

```bash
bundle exec fastlane deploy_internal --env default
```

## CI/CD GitHub Actions

Les secrets GitHub à configurer dans **Settings → Secrets and variables → Actions** :

| Secret | Description |
|---|---|
| `PLAY_STORE_JSON_KEY_BASE64` | Contenu de `play-store-key.json` encodé en base64 |
| `KEYSTORE_BASE64` | Fichier `.jks` encodé en base64 |
| `KEYSTORE_PASSWORD` | Mot de passe du keystore |
| `KEY_ALIAS` | Alias de la clé (ex: `stopgalere`) |
| `KEY_PASSWORD` | Mot de passe de la clé |

Encoder les fichiers pour GitHub Secrets :

```bash
base64 -i fastlane/play-store-key.json | pbcopy   # macOS
base64 -i fastlane/keystore/stopgalere-release.jks | pbcopy
```

## Screenshots automatiques

Les screenshots sont capturés avec **Screengrab** sur un émulateur connecté.

```bash
# Prérequis : émulateur démarré ou appareil connecté
adb devices

# Lancer les screenshots
bundle exec fastlane screenshots
```

Les captures sont sauvegardées dans `fastlane/metadata/android/{locale}/images/`.

## Workflow de publication recommandé

```
develop branch → PR → main
                           ↓
                    CI: tests unitaires
                           ↓
                    Deploy Internal (draft)
                           ↓
                    Tests manuels QA
                           ↓
              fastlane deploy_beta (promote)
                           ↓
              fastlane deploy_production (10% rollout)
```
