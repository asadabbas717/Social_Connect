## 🚀 Features

### 🔐 Authentication
- User **Sign Up**, **Login**, and **Forgot Password**
- Firebase Authentication with launcher-based login check

### 👤 User Profile
- Bottom sheet **Edit Profile** system (Name, Bio, Image)
- Upload profile picture from **Camera or Gallery**
- Displays name, bio, email, and account joined date
- Shows all your posts on profile screen

### 📝 Post System
- Create posts with **text and optional image**
- Upload images using Firebase Storage
- Home feed shows:
  - User info (name + profile picture)
  - Post content
  - Like button with real-time counter
  - View and add Comments

### ❤️ Likes & 💬 Comments
- Toggle-like system with live update
- Comment screen shows:
  - Commenter name and profile image
  - Timestamp
- Firestore subcollection-based comment storage

### 🔍 Search
- Search users or posts by text
- **Smart switching** between user/post search (via long press)
- Results show:
  - Profile pictures and names
  - Click to open Chat or Profile depending on context
  - Message if "No results found"

### 💬 Chat System
- Start 1:1 **real-time chat** with any user
- Conversations stored using Firestore collections
- Chat UI with sent/received message bubbles
- Automatic scroll and UI refresh

### 📋 UI & UX
- Material design-based UI with modern touch
- **Bottom navigation bar** with scrolling animation
- Shimmer loading on post feed
- Clean layouts using ConstraintLayout and LinearLayout
- Reusable Adapters and Fragments for modularity

---

## 📂 Project Structure

```plaintext
com.example.socialconnect
│
├── adapters/
│   ├── PostAdapter.java
│   ├── CommentAdapter.java
│   ├── ChatAdapter.java
│   └── UserListAdapter.java
│
├── fragments/
│   ├── HomeFragment.java
│   ├── ProfileFragment.java
│   └── SettingsFragment.java
│
├── models/
│   ├── Post.java
│   ├── Comment.java
│   └── Message.java
│
├── activities/
│   ├── LoginActivity.java
│   ├── SignupActivity.java
│   ├── LauncherActivity.java
│   ├── MainActivity.java
│   ├── CreatePostActivity.java
│   ├── CommentActivity.java
│   ├── ConnectionsActivity.java
│   ├── ChatActivity.java
│   └── SearchActivity.java
```

---

## 🧰 Tech Stack

| Layer            | Technology                          |
|------------------|--------------------------------------|
| Language         | Java                                 |
| IDE              | Android Studio (Meerkat)             |
| Backend Services | Firebase Auth, Firestore, Storage    |
| UI Components    | ConstraintLayout, Material, Glide    |
| Image Handling   | Camera, Gallery, Glide               |
| Navigation       | BottomNavigationView, Intents        |
| Realtime DB Ops  | Firestore with snapshot listeners    |

---

## 🖼 Launcher Logo

- Modern logo included (`ic_launcher.png`, `ic_launcher_round.png`)
- Adaptive icon support
- Launcher logo located in `res/mipmap-*` folders

---

## 📦 Final Notes

- Fully **functional app with all core social features**
- Clean, modular, and scalable architecture
- Push notifications **not included** in the final build
- Ready to deploy or extend with additional features
