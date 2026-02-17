# 🚍 Bus Tracking App

A **native Android mobile application built completely in Java** that enables users to **track bus locations in real-time** using GPS and map integration.

This application helps passengers monitor bus movement, view live location updates, and improve travel planning efficiency.

---

## 📌 Project Overview

The **Bus Tracking App** is developed using:

- **Java (Core Android Development)**
- **Android Studio**
- **WebView Integration for Map Rendering**
- **Leaflet.js (JavaScript Mapping Library)**
- **OpenStreetMap (Map Data Provider)**
- **Location Services (GPS / Fused Location Provider)**

The app provides a real-time visualization of bus movement on a map interface.

---

## ✨ Features

- ✅ **Real-Time Bus Tracking**
- ✅ **Live GPS Location Updates**
- ✅ **Interactive Map Integration**
- ✅ **User-Friendly Interface**
- ✅ **Android Native Development (Java)**
- ✅ **Location Permission Handling**

---

## 🏗️ Tech Stack

| Technology | Purpose |
|------------|----------|
| **Java** | Application logic |
| **Android Studio** | Development Environment |
| **Android SDK** | Core mobile framework |
| **WebView + Leaflet.js** | Interactive map rendering inside Android |
| **OpenStreetMap (OSM)** | Map data provider |
| **GPS / Location Services** | Real-time location tracking |

---

## 📂 Project Structure

```
Bus-Tracking-App/
│
├── app/
│   ├── src/main/java/        # Java source files
│   ├── src/main/res/         # Layouts, Drawables, Values
│   ├── AndroidManifest.xml
│   └── google-services.json 
│
├── gradle/
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## ⚙️ Installation Guide

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/GAYATHRI-475/Bus-Tracking-App.git
```

### 2️⃣ Open in Android Studio

- Open Android Studio  
- Click **Open Existing Project**  
- Select the cloned folder  

### 3️⃣ Configure API Key

Since the project uses Leaflet with OpenStreetMap:

- No API key is required.
- Load the Leaflet map inside an HTML file.
- Add OpenStreetMap tile layer:
```
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);
```

- Display the map inside Android using WebView.
- Pass latitude & longitude from Java to JavaScript to update the bus marker.

### 4️⃣ Enable Location Services

Make sure:
- Device GPS is turned ON
- Location permissions are granted

### 5️⃣ Run the Application

- Connect Android device or use emulator
- Click **Run ▶️**

---

## 🔍 How It Works

1. The app requests **Location Permission**.
2. The device GPS retrieves real-time coordinates.
3. The app updates the bus position on the map.
4. Users can visually track movement dynamically.

---

## 🚀 Future Enhancements

- 🔹 Estimated Time of Arrival (ETA)
- 🔹 Bus Route & Stop Details
- 🔹 Push Notifications for Bus Arrival
- 🔹 Driver & Passenger Login System
- 🔹 Firebase Backend Integration
- 🔹 Admin Dashboard

---

## 🤝 Contribution

Contributions are welcome!

1. Fork the repository
2. Create a new branch
3. Commit changes
4. Open a Pull Request

---

## 📜 License

This project currently does not include a license.
You may add **MIT** or **Apache 2.0** if needed.

---

## 👩‍💻 Authors

**Gayathri & Raaja Hari Vignesh**

If you like this project, don't forget to ⭐ the repository!

---
