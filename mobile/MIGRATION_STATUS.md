# Migration status

## Development paused — October 5, 2026

Paused at the owner's request. Resume only when requested. The current changes remove billing-required Firebase Storage and photo features while retaining email authentication and Firestore workflows. Type checking, lint, all 12 tests, formatting, Android native project generation, and the Android Firebase client configuration check passed. Native device builds and live Firebase billing/access-rule verification remain pending. Begin future work by reviewing this file and `firebase/FREE_TIER.md`; keep development on Spark with no linked billing account.

## Scope delivered

React Native implementation of the existing Android core workflows in a separate `mobile/` application. Firebase stays the backend. The original Android app is retained as a migration reference. Subsequent work moved the migration to `main`, renamed the repository/app Loopnest, and removed Cloud Storage and media selection from both apps. No live Firebase changes have been performed.

Implemented: email authentication/reset, protected routes, profile setup/edit, text post creation, feed and older-page loading, transactional likes, live comments, sign-out. Reused the repository's logo. Shared services own Firebase access; domain functions preserve legacy field compatibility and validation. Existing image metadata is retained, but photos are not loaded.

The implementation incorporates the supplied audit's priorities: correctness before cosmetics, small useful boundaries, metadata integrity, duplicate-submit protection, cleanup, honest documentation, meaningful tests, configuration without privileged credentials, and repeatable checks.

## Verification performed

- TypeScript strict checking: passed.
- Expo ESLint: passed.
- 11 domain regression tests and one Firebase dependency policy check: passed.
- Expo Doctor: 21/21 passed.
- Android Firebase package-ID check: passed using the existing client file; no remote requests.
- Android native project generation: passed with Firebase config plugins. Generated files are ignored.
- Android and iOS Hermes JavaScript exports: passed. These are bundle checks, not native app builds.
- iOS client configuration check: fails as expected because `GoogleService-Info.plist` is absent.
- Git whitespace/source cleanup checks: performed before handoff.

The GitHub workflow runs installation, typing, lint, tests, formatting, and both bundle exports. Its remote execution status has not been verified.

## Pending device/backend verification

No APK/IPA build, emulator/device launch, live account login, Firebase read/write, security-rule test, cross-client concurrency test, or visual accessibility test has been completed. JDK/Android SDK setup is missing locally; iOS requires its Firebase client file and a macOS/Xcode or cloud build environment. A development build is required, not Expo Go.

Confirm the project is on Spark with no linked billing account, then verify ownership, comment parent existence, and profile field permissions in Firebase rules using isolated test accounts. See `firebase/FREE_TIER.md`.

Comments are limited to the newest 100. Newest 30 posts update live; older pages refresh when reloaded. Full offline reconciliation, durable submission idempotency, public/private profile separation, moderation, and account deletion remain release work.

## Dependency review

The latest dependency removal reported 68 advisories: 59 high and 9 moderate. The earlier review reported 37; the dependency tree still requires advisory assessment before release. Direct advisory-bearing transitive packages include `@grpc/grpc-js`, `braces`, `decode-uri-component`, `node-forge`, and `uuid`. Some are build tooling or JavaScript fallback dependencies; this does not establish that every advisory is exploitable in native apps.

Automated suggestions included replacing Expo with a much older major and replacing Firebase packages with another major. These forced changes were intentionally not applied. Assess reachability and compatible upstream patches before release. Expo Doctor passing does not mean the dependency tree is vulnerability-free.

## Next steps

1. Provide the iOS Firebase client file for the existing project.
2. Build/install Android and iOS development clients and test the main workflows using isolated data.
3. Verify Firebase rules, then add emulator integration tests.
4. Resolve relevant dependency advisories, test recovery/accessibility, and establish release signing and rollback.

Chat/search/notifications and profile post lists are new feature work, not unfinished ports of code that existed in the inspected repository. This migration is an implemented, statically checked foundation awaiting native and backend validation; it is not a production release.
