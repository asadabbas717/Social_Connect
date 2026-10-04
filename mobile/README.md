# Loopnest — React Native migration

Expo SDK 57, React Native 0.86, TypeScript, Expo Router, and React Native Firebase. Android source in the parent project's `app/` directory remains separate. Firebase users and existing document fields are reused; no database migration or live backend changes have been performed.

The product is now named **Loopnest**, with Expo slug/deep-link scheme `loopnest` and package name `loopnest-mobile`. The native identifier `com.example.socialconnect` is retained to preserve the existing Firebase registration. Branding changes require rebuilding installed native clients.

The renamed icon is `assets/loopnest-icon.png`, edited with the built-in image-generation tool. Edit prompt: replace only the white word “SocialConnect” beneath the symbol with exactly “Loopnest”, centered in the same position with the same white sans-serif type; preserve the dark blue background, blue rounded square, white chat bubble, red heart, lighting, texture, and composition; output a square image with no additional text or elements. The original artwork remains in the Android reference project and Git history.

## Implemented

Email sign-in/registration/reset, protected navigation, sign-out, profile editing, live newest-post feed, older-post pagination, text posts, transactional likes, and live comments. Loading, empty/error states, accessible controls, input limits, repeated-tap protection, listener cleanup, and profile metadata preservation are part of the implementation.

Search, direct chat, notifications, account deletion/moderation, and the profile post list are outside existing-source parity. There is no demo-data fallback that pretends to be Firebase.

## Install and run

Use Node 22.13+ (Node 24 LTS recommended) and npm. Dependencies are locked in `package-lock.json`.

```powershell
cd mobile
npm ci
npm run check
node scripts/check-config.mjs android
npm run android
```

`npm run android` builds a development client and needs Android SDK/JDK. Once installed, `npm start` serves it. React Native Firebase requires a development build; Expo Go cannot run this app. Local iOS builds use `npm run ios` on macOS with Xcode; use local builds for free development. Optional cloud-build configuration exists in `eas.json`; no cloud jobs are required or launched.

Android currently uses `../app/google-services.json`. To test safely against a dedicated Firebase project, register the same package there and set `FIREBASE_ANDROID_CONFIG` to its client-file path before building. iOS requires registering `com.example.socialconnect` in the chosen Firebase project and providing `firebase/GoogleService-Info.plist` or `FIREBASE_IOS_CONFIG`. Verify with `node scripts/check-config.mjs ios`. No service-account key belongs in the app.

If you separately choose to use EAS, provide client files in the build context or use EAS file environment variables with the two configuration names above. Cloud build requires your Expo account/project setup. No EAS jobs or remote writes have been initiated. The same Android application ID preserves Firebase registration, but installing over a previously signed app requires a compatible signing key; otherwise use a separate device/test package with a corresponding Firebase registration.

## Data and architecture

Routes in `src/app` render screens. Components and hooks handle shared presentation and pending actions. `src/services` owns Firebase operations. Pure domain functions validate input and decode legacy documents; screens do not write arbitrary Firestore documents.

Existing paths are retained: `users/{uid}`, `posts/{id}`, and `posts/{id}/comments/{id}`. Field names include `name`, `bio`, `imageUrl`, `createdAt`, `uid`, `text`, `timestamp`, and `likes`. Authentication retains original UIDs. Android session state is not assumed to transfer to the new app.

Media uploads and remote media display are removed to keep development compatible with Firebase Spark. Existing `imageUrl` values remain stored for compatibility but are not fetched. Profile edits preserve them; new posts write an empty image URL. Avatars show initials. See [the free-tier policy](firebase/FREE_TIER.md).

Profile creation/edit and like/comment operations use transactions. New post documents retain established fields. Comment IDs are generated once per submission, and server timestamps replace client-clock ordering. Server-enforced ownership and field/size validation must also exist in Firebase rules; client validation alone is insufficient. Live security rules are not available in this repository and have not been verified.

Newest 30 posts are live; older pages are snapshots until reloaded. Comments show the latest 100, ordered oldest to newest within that window. Transactions need a network connection. There is no promise of exactly-once submission across process restarts or full offline synchronization.

## Quality checks and remaining release gates

`npm run check` runs TypeScript, Expo lint, and Node's test runner. Tests protect profile field preservation, legacy document decoding, input boundaries, password handling,  and safe provider errors. They do not exercise native Firebase modules.

Before release: build/install on Android and iOS, verify authentication with dedicated test accounts, run multi-client like/profile tests and rules/emulator tests, check accessibility and keyboard behavior on devices, assess dependency advisories, and establish signing/rollback and backup procedures. A JavaScript bundle export is not an APK or IPA and does not prove native integration.

Decisions follow the supplied engineering audit: keep the backend/data format, add boundaries only where they protect behavior, use meaningful tests, and state what has not been verified. See `MIGRATION_STATUS.md` for the checks performed in this workspace.
