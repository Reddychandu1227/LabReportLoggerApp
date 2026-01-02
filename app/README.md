# 🧪 Lab Report Logger – Android Application

## 📱 Project Overview

**Lab Report Logger** is an Android mobile application developed as part of the **Mobile Application Development** module at **Teesside University**.  
The application enables users to securely store, manage, and access lab reports digitally, reducing dependency on physical documents and improving accessibility.

The app supports uploading lab reports with images, categorizing reports, marking favorites, viewing statistics, and managing user profiles through a clean and modern user interface.

---

## 🎯 Features

- 🔐 User Registration & Login
- 📤 Upload Lab Reports
    - Category selection
    - Date picker
    - Image upload (camera/gallery)
- 📄 View All Reports
- ⭐ Favorite Reports
    - Mark/unmark reports as favorites
    - Separate Favorites screen
- ✏️ Edit & Delete Reports
- 🖼️ Full-Screen Image Viewer
    - Swipe between images
    - Close and back navigation
- 📊 Report Statistics
    - Total reports
    - Category-wise count
    - Favorite reports count
- 👤 Profile Management
    - View and update name & age
    - Logout functionality
- ℹ️ About Us & Contact Us

---

## 🛠️ Technologies Used

- Programming Language: **Kotlin**
- UI Framework: **Jetpack Compose (Material Design 3)**
- Database: **Firebase Realtime Database**
- Image Hosting: **ImgBB API**
- Image Loading: **Coil**
- Navigation: **Jetpack Navigation Compose**
- Local Storage: **SharedPreferences**

---

## 📂 Project Architecture

- Composable-based UI using Jetpack Compose
- Firebase Realtime Database for cloud data persistence
- External image hosting to keep the database lightweight
- Modular screen structure:
    - Statistics
    - My Reports
    - Favorites
    - Report Details
    - Profile

---

## 🔑 Permissions Used

- 📷 Camera – Capture lab report images
- 🗂️ Storage – Select images from gallery
- 🌐 Internet – Upload images and sync data with Firebase

Permissions are requested only when required, following Android best practices.

---

## 🚀 How to Run the Project

1. Clone the repository:

2. Open the project in **Android Studio**

3. Sync Gradle dependencies

4. Add your Firebase configuration:
- Create a Firebase project
- Enable Realtime Database
- Download `google-services.json`
- Place it in the `app/` directory

5. Run the app on an emulator or physical device


