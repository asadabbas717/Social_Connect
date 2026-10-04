# Migration status

## Scope delivered

React Native implementation of the existing Android core workflows in a separate `mobile/` application. Firebase stays the backend. The Android source and earlier audit edits were left as they stood when migration began. No commits, pushes, live database writes, deployments, credential rotations, or history changes were performed.

Implemented: email authentication/reset, protected routes, profile setup/edit, text/image post creation, feed and older-page loading, transactional likes, live comments, sign-out, and camera/library selection. Reused the repository's logo. Shared services own Firebase access; domain functions preserve legacy field compatibility and validation. New profile image paths avoid destructive overwrite before metadata commit.

The implementation incorporates the supplied audit's priorities: correctness before cosmetics, small useful boundaries, metadata integrity, duplicate-submit protection, cleanup, honest documentation, meaningful tests, configuration without privileged credentials, and repeatable checks.

## Verification performed

- TypeScript strict checking: passed.
- Expo ESLint: passed.
- 12 domain regression tests: passed.
- Expo Doctor: 21/21 passed.
- Android Firebase package-ID check: passed using the existing client file; no remote requests.
- Android native project generation: passed with Firebase config plugins. Generated files are ignored.
- Android and iOS Hermes JavaScript exports: passed. These are bundle checks, not native app builds.
- iOS client configuration check: fails as expected because `GoogleService-Info.plist` is absent.
- Git whitespace/source cleanup checks: performed before handoff.

The GitHub workflow runs installation, typing, lint, tests, formatting, and both bundle exports. It was added locally and has not run on GitHub.

## Pending device/backend verification

No APK/IPA build, emulator/device launch, live account login, Firebase read/write, security-rule test, cross-client concurrency test, native image-picker test, or visual accessibility test has been completed. JDK/Android SDK setup is missing locally; iOS requires its Firebase client file and a macOS/Xcode or cloud build environment. A development build is required, not Expo Go.

Confirm existing Firebase authorization rules, especially ownership, comment parent existence, image paths/MIME/size, and profile field permissions. New avatars use `profiles/{uid}/{generatedId}.{ext}`; older URLs are preserved. Use a dedicated test project and accounts to validate without touching real user data.

Comments are limited to the newest 100. Newest 30 posts update live; older pages refresh when reloaded. Full offline reconciliation, durable submission idempotency, upload garbage collection, public/private profile separation, moderation, and account deletion remain release work.

## Dependency review

`npm audit` reported 37 advisories in the resolved dependency tree: 27 high, 10 moderate, no critical. Direct advisory-bearing transitive packages include `@grpc/grpc-js`, `braces`, `decode-uri-component`, `node-forge`, and `uuid`. Some are build tooling or JavaScript fallback dependencies; this does not establish that every advisory is exploitable in native apps.

Automated suggestions included replacing Expo with a much older major and replacing Firebase packages with another major. These forced changes were intentionally not applied. Assess reachability and compatible upstream patches before release. Expo Doctor passing does not mean the dependency tree is vulnerability-free.

## Next steps

1. Provide the iOS Firebase client file for the existing project.
2. Build/install Android and iOS development clients and test the main workflows using isolated data.
3. Verify Firebase rules and media-path compatibility, then add emulator integration tests.
4. Resolve relevant dependency advisories, test recovery/accessibility, and establish release signing and rollback.

Chat/search/notifications and profile post lists are new feature work, not unfinished ports of code that existed in the inspected repository. This migration is an implemented, statically checked foundation awaiting native and backend validation; it is not a production release.
