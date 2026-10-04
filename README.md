# Loopnest

Repository: [asadabbas717/loopnest](https://github.com/asadabbas717/loopnest).

Social app being migrated to React Native for Android and iOS, using the existing Firebase backend. The active application is in [`mobile/`](mobile/README.md), and ongoing development uses the `main` branch.

Implemented workflows: email registration/login/password reset, feed, text/image posts, likes, comments, profile editing, and logout. Search, direct messaging, and a profile post list are not implemented. Client-side notification sending was removed during review because privileged sending belongs on a trusted backend.

## React Native setup and run

```powershell
cd mobile
npm ci
npm run check
npm run android
```

Use a native development build, not Expo Go. See the [mobile setup guide](mobile/README.md) for Android requirements, Firebase client files, and iOS configuration. See [migration status](mobile/MIGRATION_STATUS.md) for verified checks and pending device/backend validation. The quality-check workflow is in `.github/workflows/mobile.yml`.

## Original Android project (migration reference)

The `app/` sources and root Gradle files remain as a behavioral reference until the migrated app has passed device testing. They are separate from `mobile/android/`, which Expo generates for the React Native application. Do not edit generated Expo native files by hand.

Use Android Studio, JDK 17, Android SDK 36, and network access to Google Maven, Maven Central, JitPack, and Gradle distributions. The wrapper specifies Gradle 8.13 and the build uses Android Gradle Plugin 8.11.0. Minimum device API is 23.

Configure a dedicated development Firebase project with email/password Authentication, Firestore, and Storage. Register `com.example.socialconnect` and place its downloaded client configuration in `app/google-services.json`. Never put service-account keys or privileged notification credentials in the app. Review backend access rules before using real accounts or data. There are no environment variables required by application code; local SDK/JDK configuration is machine-specific.

Open in Android Studio and run the `app` configuration on an emulator/device. From PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`. Release packaging uses `assembleRelease`, but release signing and distribution are not configured. The debug build is not a release artifact.

## Original Android structure

- Activities/fragments: screens and navigation.
- Adapters: recycled feed/comment rows.
- `models`: Firestore data mapping.
- `data`: profile update fields and atomic persistence.
- `res`: Android layouts and visual resources.

Most Firebase access remains in UI code. This is an incremental improvement of a student project, not a completed production architecture.

## Original Android limits and verification

Feed shows the newest 50 posts with a lifecycle-scoped live listener. Comments show the latest 100 comments, ordered oldest to newest within that window. Older-history pagination is pending. Transactions for profiles and likes require network connectivity; other Firebase operations may remain pending offline. Upload/database partial failures still need recovery design.

Android profile field regression tests have been added. Original Android build, lint, and tests have not run successfully in the current review environment because Java is unavailable. Device, backend authorization, notification, and accessibility verification remain pending. Backend security-rule files are not provided yet.

See [the engineering audit](ENGINEERING_AUDIT.md) for findings and deferred work. [The migration assessment](REACT_NATIVE_ASSESSMENT.md) describes the original inspected source; some issues there have since been addressed. React Native core workflows are now implemented and statically checked; native device/backend validation is pending. Firebase remains the backend; no Supabase migration has been performed.
