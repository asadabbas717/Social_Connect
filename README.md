# 📱 Social Connect - Android App

**Social Connect** is a Firebase-powered native Android social media app built in Java using Android Studio. It provides real-time user interaction through posts, likes, comments, profiles, and notifications, making it a feature-rich starter for social networking applications.

---

## 🚀 Features

- **Authentication**
  - User Signup/Login with Firebase Authentication
  - Forgot Password & Launcher checks

- **User Profile**
  - Edit profile via a Bottom Sheet
  - Upload profile image from Camera or Gallery
  - Display name, bio, email, and joined date
  - View your own posts under your profile

- **Create Posts**
  - Post text and upload images from gallery or camera
  - Image uploads handled via Firebase Storage
  - Posts shown on Home screen with username and profile image

- **Post Feed**
  - Real-time post feed on Home Fragment using Firestore
  - Posts include:
    - Username & profile image
    - Post text & optional image
    - Like button with live count
    - View & Write Comment buttons

- **Comment Section**
  - Add and view comments on any post
  - Comment UI includes username, comment time, and text
  - Stored in Firestore as subcollection under each post

- **Likes**
  - Realtime like/unlike toggle
  - Likes are user-specific and stored in post document
  - Like updates trigger Firestore UI refresh

- **Push Notifications (FCM)**
  - Receive notifications when someone:
    - Likes your post
    - Comments on your post
  - Device tokens stored and updated in Firestore

- **Modern UI/UX**
  - Bottom Navigation View
  - Shimmer loading while fetching data
  - Material design components used
  - Circle profile images using Glide + CircularImageView

---

## 🛠 Tech Stack

| Layer            | Technology                          |
|------------------|--------------------------------------|
| Language         | Java                                 |
| IDE              | Android Studio (Giraffe/Meerkat)     |
| Backend Services | Firebase (Firestore, Auth, Storage)  |
| Push Messages    | Firebase Cloud Messaging (FCM)       |
| Image Handling   | Glide, Camera/Gallery Intents        |
| Recyclerview     | Adapter-based post feed & comments   |
| Notification API | OkHttp with JSON (FCM Manual Trigger)|
| Build System     | Gradle + libs.versions.toml          |

---

## 📂 Project Structure

com.example.socialconnect
│
├── adapters/
│ └── PostAdapter.java, CommentAdapter.java
│
├── fragments/
│ ├── HomeFragment.java
│ ├── ProfileFragment.java
│ └── SettingsFragment.java
│
├── models/
│ └── Post.java, Comment.java
│
├── utils/
│ └── NotificationSender.java
│
├── activities/
│ ├── LoginActivity.java
│ ├── SignupActivity.java
│ ├── MainActivity.java
│ ├── ProfileActivity.java (replaced by BottomSheet)
│ ├── CreatePostActivity.java
│ ├── CommentActivity.java
│ └── LauncherActivity.java
