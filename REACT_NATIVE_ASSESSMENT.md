# React Native migration assessment

## Conclusion

The repository can be rebuilt as a React Native application for Android and iOS. This is a frontend rewrite, not an automatic Java-to-JavaScript conversion. Existing Firebase accounts, Firestore documents, Storage media, and visual assets can potentially be retained, subject to checking the live Firebase configuration and access rules.

## Repository inspected

- `main` contains only the portfolio README.
- `master` contains the native Android application; inspected commit: `d0f0629`.
- The app uses Java, Android XML layouts, Firebase Authentication, Firestore, Storage, and Messaging.
- No React Native or iOS application is present.
- Assessment is based on source inspection; neither the Android app nor the live Firebase backend was tested.

## Actual feature inventory

| Feature | Source evidence | Migration work |
| --- | --- | --- |
| Email registration, login, password reset, session launch | SignupActivity, LoginActivity, ForgotPasswordActivity, LauncherActivity | React Native screens and auth state handling |
| Live feed | HomeFragment and PostAdapter | List screen and Firestore subscriptions |
| Text/image posts | CreatePostActivity, PostActivity | Composer and cross-platform image selection/upload |
| Likes | PostAdapter | Preserve per-user likes map and toggle behavior |
| Comments | CommentActivity and CommentAdapter | Comments screen and subscription |
| Profile editing | ProfileFragment and EditProfileBottomSheet | Profile screen, edit form, image picker |
| Notifications | FCMService and NotificationSender | Reassess delivery; use a trusted backend for sending |
| Search and direct chat | Described on main's README, but implementation not found on master | Treat as new features |
| Profile post list | No post query found in ProfileFragment | Treat as additional work |

## Backend compatibility

Keep the existing Firebase project and user UIDs where feasible. Preserve these observed paths and fields:

- `users/{uid}`: `name`, `bio`, `imageUrl`, `createdAt`, `fcmToken`.
- `posts/{postId}`: `uid`, `text`, `imageUrl`, `timestamp`, optional `likes` map keyed by UID.
- `posts/{postId}/comments`: inspect CommentActivity when implementing to preserve its precise fields.
- Storage: `posts/{uid}_{timestamp}.jpg` and `profiles/{uid}.jpg`; a second post composer also exists, so reconcile its upload convention before migration.

Firebase access rules, indexes, actual documents, and account configuration were not verified. Register the new platform applications in Firebase and obtain their platform configuration. Reusing accounts and data does not imply existing Android login sessions transfer to a replacement app.

## Recommended implementation

Use React Native with TypeScript, Expo development builds, and React Native Firebase for Authentication, Firestore, and Storage. Add Messaging only when notification requirements are settled. React Native Firebase requires native modules and a development build; it does not run in Expo Go.

Create the replacement in a separate `mobile/` directory so the Android source remains available for reference. Implement shared navigation, screens, and Firebase services rather than copying Android activities one-for-one.

1. Establish the app shell, navigation, Firebase setup, and authentication.
2. Implement profiles, feed, post creation, likes, and comments against the existing data structure.
3. Verify media permissions, loading/error states, listener cleanup, and pagination.
4. Test with dedicated accounts on Android and iOS, including cross-client data compatibility.
5. Add search, chat, and production notifications as explicitly scoped features.

## Issues to correct during migration

- Profile saving uses `set(profile)` without merging, replaces `createdAt`, and clears `imageUrl` when no replacement image is selected. Preserve existing fields and original creation time.
- There are two post-creation activities with differing write behavior; consolidate the schema.
- NotificationSender attempts to send from the client using a hardcoded authorization value. Do not carry this approach into the new application; assess the value and move privileged sending to a trusted backend.
- The repository has Android Firebase configuration but no iOS configuration.

## Documentation

- React Native components: https://reactnative.dev/docs/components-and-apis
- Expo Firebase guidance: https://docs.expo.dev/guides/using-firebase/
- React Native Firebase setup: https://rnfirebase.io/

No React Native app has been generated or built as part of this feasibility assessment.
