# Free-tier development policy

Loopnest uses **Firebase Spark**, with no billing account linked. This policy covers development; free usage is limited, not unlimited. This repository cannot enforce or inspect the live project's billing plan.

## Console setup

1. In Firebase Console, open the project matching the client configuration and verify **Spark** in Usage and billing. Do not upgrade to Blaze or link a Cloud Billing account.
2. Enable Authentication → Email/Password. Do not enable phone/SMS authentication.
3. Use a Cloud Firestore **Standard edition** database within Spark's free quota. Keep existing collections and UIDs when using the existing project. Use a separate Spark test project if you need isolated test data, and download its Android/iOS client files as described in the mobile README.
4. Review Firestore access rules before using real accounts. This repository does not yet contain verified deployable rules; do not use permissive test-mode rules with real user data.
5. Do not enable Cloud Storage, Cloud Functions, Firebase Extensions that require billing, App Hosting, or paid Google Cloud services. No such SDK or backend deployment is needed by the migrated app.

Cloud Storage requires Blaze as of February 3, 2026, including access to older default buckets. Storage SDKs, upload code, photo selection, and remote photo loading have been removed from the React Native app and original Android reference. Existing media objects and document fields were not deleted or migrated. The `storage_bucket` field in Google's client configuration is metadata, not a Storage integration.

## Usage limits

Firestore's free allowance includes 1 GiB stored data, 50,000 document reads/day, 20,000 writes/day, 20,000 deletes/day, and 10 GiB outbound transfer/month. Only one database per project receives the free quota. Avoid optional billed features such as backups, PITR, TTL deletes, restores, and clones. Spark usage stops or errors when applicable limits are exceeded; it does not automatically become paid. Recheck official quotas as pricing can change.

Feed queries are limited to 30 live posts and comments to 100. Profile reads, pagination, initial snapshots, reconnects, and transactions still consume quota; query limits are not a complete usage cap. Use small test datasets and close unused clients.

Use local Android development builds to avoid dependence on cloud-build quotas or paid plans. Firebase Local Emulator Suite is an optional way to avoid cloud usage entirely, but it is **not configured or tested here** and requires additional local setup, including Java. Native builds and live Firebase operations remain unverified in this workspace.

Sources: [Firebase plans](https://firebase.google.com/docs/projects/billing/firebase-pricing-plans), [Firestore quotas](https://firebase.google.com/docs/firestore/quotas), [Storage billing change](https://firebase.google.com/docs/storage/faqs-storage-changes-announced-sept-2024).
