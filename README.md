# ListMovies App 🎬

A feature-rich Android movie discovery application built entirely in Java, following modern Android development practices. This app allows users to browse, search, and discover movies using the TMDb API. It features a complete user authentication system powered by Firebase, allowing users to manage their profiles, favorites, and watchlists.


## ✨ Features

-   **Movie Discovery:**
    -   Browse lists for "Top Rated," "Popular," and by "Genre."
    -   **Pagination / Infinite Scrolling:** Automatically loads more movies as the user scrolls.
-   **Movie Details:**
    -   Rich detail screen with movie overview, rating, and release date.
    -   Watch official movie **trailers**.
    -   View a horizontal list of the movie's **cast**.
    -   Tap on an actor to view their details in a dialog.
    -   See legal **Watch Providers** (e.g., Netflix, etc.) based on the user's region.
-  **Favorites & Watchlist:** Users can add movies to their personal lists.
-   **Professional UI/UX:**
    -   Modern UI built with **Material Design 3**.
    -   Full support for **Light & Dark Themes**, switchable from the menu.
    -   Seamless **Shared Element Transition** animation for posters.
    -   **Google AdMob** banner ads for monetization.
    -   Modern **Splash Screen** API implementation.
    -   Graceful handling of loading, empty, and error states.

---

## 🏗️ Architecture

The application is built using the **MVVM (Model-View-ViewModel)** architecture to ensure a clean separation of concerns, making the code scalable, maintainable, and testable.

-   **View (UI Layer):** Activities and Fragments are responsible only for displaying the UI and forwarding user events to the ViewModel.
-   **ViewModel:** Acts as the bridge between the UI and the data layer. It holds all UI-related logic and exposes data to the UI via `LiveData`.
-   **Repository:** Implements the Single Source of Truth principle. It is the sole manager of all app data, deciding whether to fetch it from a remote or local source.
-   **Data Sources:**
    -   **Remote:**
        -   **Retrofit:** For consuming the TMDb REST API.
    -   **Local:**
        -   **Room Persistence Library:** For local data storage (user lists like Favorites/Watchlist in guest mode) and caching.

---

## 🛠️ Tech Stack & Libraries

-   **Language:** Java
-   **Architecture:** MVVM (ViewModel, LiveData)
-   **Android Jetpack:**
    -   Fragments, ViewPager2, RecyclerView
    -   Room Persistence Library
    -   Lifecycle Components
    -   Preference KTX (for settings)
    -   SplashScreen API
-   **Networking:**
    -   Retrofit2 & Gson
-   **Image Loading:** Picasso
-   **UI:** Material Design 3
-   **Ads:** Google AdMob
