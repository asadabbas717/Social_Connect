# Social Connect

A full-featured Android social application built with Java, XML, and Firebase. The project demonstrates authentication, user-generated content, media upload, search, comments, likes, and real-time one-to-one messaging in a native Android application.

## Engineering Highlights

- Firebase Authentication for sign-up, login, password recovery, and session-aware launch flow
- Firestore-backed social feed with real-time updates
- Firebase Storage integration for profile and post images
- Real-time one-to-one chat with conversation persistence
- Modular Android structure using activities, fragments, adapters, and model classes
- Search across users and posts
- Profile editing with camera/gallery image selection
- Reusable UI patterns for feed, comments, chat, and profile screens

## Features

### Authentication

- User sign-up and login
- Forgot-password flow
- Launcher-based authentication check

### User Profiles

- Edit name, bio, and profile image
- Upload profile picture from camera or gallery
- Display user details and joined date
- Show a user's posts on their profile

### Posts

- Create text posts with optional images
- Upload images through Firebase Storage
- Display author information and post content
- Like posts with live counters
- View and add comments

### Likes and Comments

- Toggle-like behavior
- Live like-count updates
- Commenter information and timestamps
- Firestore subcollection-based comments

### Search

- Search users or posts
- Switch search context between people and content
- Open matching profiles, conversations, or posts from results

### Real-Time Chat

- Start one-to-one conversations with users
- Firestore-backed conversation storage
- Sent and received message bubbles
- Live conversation updates

## Project Structure

```text
com.example.socialconnect
├── activities/
├── adapters/
├── fragments/
└── models/
```

The application separates screen behavior, reusable list adapters, fragments, and data models rather than placing all functionality in a single activity.

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Java |
| UI | Android XML, Material components |
| IDE | Android Studio |
| Authentication | Firebase Authentication |
| Database | Cloud Firestore |
| Media | Firebase Storage, Glide |
| Architecture | Activities, Fragments, Adapters, Models |
| Real-time updates | Firestore snapshot listeners |

## Scope

The application implements the core social-network workflows listed above. Push notifications are not included in the current version.

## Author

Developed by **Asad Abbas** during an Android development internship project.
