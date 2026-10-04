# Engineering Audit

## Executive Summary

Reviewed the Android source on `master` at baseline `d0f0629`, build/configuration files, resources, example tests, and available Git history. Compared it against the portfolio README on `main`. This is a student social-app prototype with direct Firebase access from screens/adapters, limited failure handling, and no meaningful original regression suite.

The user subsequently asked to defer time-consuming measures. This report records an initial audit and incremental fixes already made, not completion of the full requested production-hardening programme. No remote repository, live database, access rules, or deployment was modified.

## Original Score and Final Score

Scores are engineering judgments based on source evidence, not measured certification. The final column is provisional until build and runtime verification succeeds. Overall is rounded arithmetic mean of the categories below.

| Category | Original | Current provisional |
|---|---:|---:|
| Architecture | 3 | 4 |
| Code Quality | 4 | 5 |
| SOLID / Design | 3 | 4 |
| Domain Modeling | 3 | 4 |
| Security | 2 | 3 |
| Reliability | 2 | 4 |
| Testing | 1 | 2 |
| Database/Persistence | 3 | 4 |
| Performance | 3 | 4 |
| Configuration | 3 | 3 |
| Dependencies | 4 | 5 |
| Logging/Observability | 2 | 3 |
| UI/UX Robustness | 3 | 4 |
| Accessibility | 2 | 3 |
| Documentation | 2 | 5 |
| Developer Experience | 3 | 4 |
| CI/CD | 0 | 0 |
| Deployment/Release | 1 | 1 |
| Repository Hygiene | 3 | 4 |
| Overall | 2.6 | 3.6 |

## Major Problems Found

- **P0 candidate, unverified live impact:** no versioned backend authorization rules or rules tests. Cannot determine whether users can overwrite other users' records. Production access requires a live configuration audit.
- **P1:** profile `set()` replaced documents, reset join time, and cleared the avatar on text-only edits. New code merges editable fields in a transaction; adds creation time only when the document is absent.
- **P1:** launcher attempted to start a bottom-sheet fragment as an activity for users without profiles. Launcher now routes authenticated users to MainActivity, where profile editing is available.
- **P1:** client notification sender embedded a value labeled as a server key and called a legacy endpoint. The value's actual privilege was not established; it must not be presumed to be a working server credential. Removed the sender and its call sites. No replacement delivery backend has been built.
- **P1:** likes used a read-then-write sequence vulnerable to concurrent updates. Toggle now reads/writes in a Firestore transaction using a UID field path.
- **P1:** only arithmetic/template tests existed; important workflows were unprotected.
- **P2:** unchecked sessions, raw provider error messages, repeated submissions, recycled-holder callback races, unbounded queries, and sensitive token/post-content logs.
- **P2:** duplicate post composers; the unused composer was not declared in the manifest or referenced by navigation. Removed it and its layout; kept the active composer and existing data fields.
- **P2:** duplicate OkHttp declarations and unused navigation/Cast dependencies. Removed these without upgrading the remaining packages.
- **P2:** Firebase client configuration and IDE files are already tracked. Ignore patterns do not remove tracked files or history. Client Firebase API identifiers are not service-account secrets, but project configuration/restrictions still require owner review.

## Engineering-Control Signals

The main README claimed chat/search and live updates not supported by the inspected baseline. Two composers had different write conventions. Boilerplate tests did not protect app behavior. Notification TODOs and generic comments obscured unfinished functionality. These are concrete consistency problems; source alone cannot establish whether AI generated the code.

## Changes Implemented

Introduced a small profile persistence boundary (`ProfileUpdate`, `ProfileStore`) to protect merge semantics. Fixed launcher routing, guarded main-screen authentication, preserved fragment restoration, and cleared navigation history on logout. Added duplicate-submission prevention and recoverable failure feedback to auth/post/comment/profile operations; passwords are no longer trimmed.

Feed now uses a scoped listener removed on stop, reads likes from the feed snapshot, and limits loading to 50 posts. Comment loading is capped at the latest 100. Adapter callbacks verify the bound record before updating reused rows. Added nullable-image handling, server comment timestamps, token refresh persistence without sensitive logging, and some accessibility labels/touch-target improvements. Disabled application backup as a conservative local-data default; Android-version-specific backup behavior is not verified.

Significant modules: launcher/main/auth activities, CreatePostActivity, CommentActivity, EditProfileBottomSheet, FCMService, both adapters, Home/Profile/Settings fragments, Post model, new profile data classes/tests, dependency files, manifest, two layouts, ignore rules, and README. Removed unused PostActivity/layout, NotificationSender, and the arithmetic test.

## Architecture

```text
Activities / fragments / adapters -> Firebase Android SDK
Profile editor -> ProfileStore -> ProfileUpdate + Firestore transaction
Firestore models -> feed/comment rendering
```

Current decision: keep Java/Android/Firebase and existing data paths during this pass. A framework or database migration would make bug attribution and data compatibility harder. Alternative: immediate React Native/Supabase rewrite. That remains a separate project phase, not a prerequisite for these fixes. The small profile boundary solves a demonstrated data-loss problem; no generic repository hierarchy was introduced.

## Critical Workflows

| Workflow | Current protection | Still required |
|---|---|---|
| Registration/login/reset | Pending buttons, preserve password input, generic errors | Auth integration and policy tests |
| Session launch/logout | Valid activity routing, main session guard, clear logout stack | Device navigation and expiry tests |
| Edit profile | Merge transaction and field regression tests | Emulator concurrency/creation tests |
| Create image/text post | Validation, pending state, upload/download/write errors | Partial-failure and upload-size tests |
| Feed refresh | Live limited query, listener cleanup | Device lifecycle and paging tests |
| Like/unlike | Firestore transaction and field path | Multi-client/rules/offline tests |
| Comment submission | Server timestamp, pending state, failure feedback | Missing-parent and authorization tests |
| Token refresh | Persist without token/body logging | Multiple devices, logout cleanup, delivery backend |

## Testing Strategy and Execution

Added two JUnit regression tests for text-only profile edits preserving avatar/metadata and selected images changing only editable fields. These test actual update-field construction, not mocks; Firestore merge/transaction behavior still needs integration tests.

Executed `gradlew.bat testDebugUnitTest lintDebug assembleDebug`: failed before Gradle tasks started because JAVA_HOME is unset and Java is not on PATH. No JUnit, Android lint, compilation, APK generation, or instrumentation tests passed. Executed source/reference review, XML parsing, source delimiter checks, and `git diff --check`; these limited checks do not substitute for compilation or runtime tests. Full E2E/backend/security/restore tests are deferred.

## Security Review and Data Integrity

Removed privileged client sending and sensitive logs. The hardcoded value remains in Git history: inspect its real type/restrictions and revoke/rotate if privileged or abused; history was not rewritten. The locally downloaded google-services configuration remains tracked in the baseline despite new ignore rules. No credentials were rotated or deployed.

Profile transactions preserve existing metadata, and likes transactions serialize toggles. Both need online connectivity. Document paths and established fields remain compatible; no schema migration was applied. Still unresolved: server-enforced ownership, upload limits/content checks, missing-parent comments, public profile/token separation, bounded like maps, destructive operations, orphan uploads, pending offline writes, backups and restore testing.

Firebase supports field-change constraints in [security rules](https://firebase.google.com/docs/firestore/security/rules-fields). Notification sending requires a [trusted sending environment](https://firebase.google.com/docs/cloud-messaging/send/v1-api). These principles inform the findings; they do not verify this project's live backend.

## Remaining Technical Debt and Risks

No CI, release signing, dependency locking/verification, vulnerability/license scan, rules suite, crash reporting, backup procedure, or tested rollback. Remaining SDK calls in UI, N+1 profile lookups, incomplete loading/empty states, hardcoded UI strings, incomplete accessibility/dark-mode checks, lifecycle edge cases, and pending image-picker modernisation. Profile avatar uploads can replace the old image before the metadata save succeeds. Feed/comment caps limit visible history until pagination exists.

Most importantly, all code changes are uncompiled in this environment. Treat them as reviewable work in progress, not a validated release. Current classification remains **student project / prototype**, not production quality.

## Recommended Next Steps

1. On resuming hardening, configure JDK 17/SDK 36; compile, run tests/lint, and fix real diagnostics.
2. Verify rules and ownership with Firebase emulators and dedicated test data; protect device tokens separately.
3. Add workflow integration/device tests, then establish CI using isolated test configuration.
4. Address upload/write recovery, pagination, accessibility, offline UX, and release/restore procedures.
5. Make a separate React Native/Supabase decision after checking existing users/data and target product scope.

Broader work is intentionally deferred at the user's request. No database migration, framework rewrite, Git history rewrite, credential rotation, or remote changes were made.
