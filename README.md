# 🍽️ Food Planner - Android Application

> **Plan your meals. Simplify your life.**

**Food Planner** is a modern Android application designed to help users discover delicious recipes, organize weekly meal plans, and maintain a stress-free cooking routine. The app is built following **Clean Architecture** principles and the **MVP (Model-View-Presenter)** design pattern.

---

## 📱 About the App

Food Planner makes meal planning easy and enjoyable. Whether you're looking for daily inspiration, saving your favorite recipes, or organizing your entire week's meals, this app keeps everything in one place—supporting both **Online** and **Offline** modes.



---

## ✨ Features

* ✅ **Daily Inspiration:** View a random "Meal of the Day" for instant inspiration.
* ✅ **Browse & Explore:** Browse meals by category, country, or specific ingredients.
* ✅ **Smart Search:** Search for recipes by name or main ingredients.
* ✅ **Favorites:** Save your preferred meals to a local wishlist for quick access.
* ✅ **Weekly Planner:** Schedule your meals across the current week.
* ✅ **Cloud Sync:** Synchronize and backup your data using Firebase to access it from any device.
* ✅ **Offline Mode:** Full support for viewing favorites and your weekly plan without an internet connection.
* ✅ **Multimedia Experience:** Integrated YouTube video instructions inside the app.
* ✅ **Modern UI:** Built with Material Design 3, Lottie animations, and Dark Mode support.

---

## 🛠️ Tech Stack

| Category | Tools & Libraries |
| :--- | :--- |
| **Language** | Java |
| **Architecture** | MVP (Model–View–Presenter) |
| **Networking** | Retrofit, RxJava (Mandatory) |
| **Local Database** | Room Database |
| **Cloud/Backend** | Firebase (Auth & Firestore) |
| **UI / UX** | Material Design 3, Glide, Lottie Animations |

---

## 🏗️ Architecture Overview

The app utilizes the **MVP** pattern to ensure scalability, testability, and a clean separation of concerns:

* **Model:** Manages data sources (API, Room, Firebase) using the **Repository Pattern**.
* **View:** Activities and Fragments that handle UI rendering and user interactions.
* **Presenter:** Acts as the bridge; contains business logic, processes user actions, and updates the View via **RxJava** streams.



---

## 📡 Data Source (API)

This project consumes **TheMealDB API** for fetching comprehensive meal and recipe data:
* [TheMealDB API Official Website](https://www.themealdb.com/)

---

## 🚀 Getting Started

Follow these steps to get the project running on your local machine:

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/Omar-Amer/food-planner.git](https://github.com/Omar-Amer/food-planner.git)
    ```
2.  **Open in Android Studio:** Open the project and let Gradle sync the dependencies.
3.  **Firebase Configuration:** Add your own `google-services.json` file into the `app/` directory.
4.  **Run:** Build and run the application on an emulator or a physical device (API 24+ recommended).

---

## 👨‍💻 Developer

**Omar Amer Fathy Ali**
*Mid-Level Flutter Developer & ITI Scholar (Intake 46 - Mobile Native Track)*

* 🌐 **Portfolio:** [devomaramer.github.io](https://devomaramer.github.io/My_Portfolio/)
* 💼 **LinkedIn:** [Omar Amer](https://www.linkedin.com/in/omar-amer-fathy/)
* 📧 **Email:** dv.omar9@gmail.com

---

## 📄 License

© 2026 Food Planner. Developed as part of the ITI 9-Month Professional Program.

---
⭐ **If you find this project helpful, please consider giving it a star!**
