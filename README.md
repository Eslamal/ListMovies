# ListMovies App 🎬

A feature-rich movie and TV show browser application for Android, built with Java and Material Design 3. 
The app allows users to discover, search, and manage their favorite movies and TV shows, 
with full support for both English and Arabic languages.

---

### ✨ Features

* **Light & Dark Mode:** Full support for modern light and dark themes that can be set manually or follow the system setting.
* **Bilingual (English & Arabic):** Complete UI translation and right-to-left (RTL) support.
* **Dynamic Content Language:** Movie/TV show details (title, overview) are displayed in their original language (Arabic for Arabic content, English for others).
* **Advanced Search:**
    * **Live Search:** Get instant results for both movies and TV shows as you type.
    * **Trending & Recent:** The search screen displays trending content by default and keeps a history of your recent searches.
    * **Genre Filters:** Quick-search by tapping on translated genre chips.
* **Discover Content:**
    * Browse by categories like Popular, Top Rated, and Genres.
    * Dedicated section for Arabic content, subdivided into Egyptian and Gulf categories.
* **Personal Lists:**
    * **Favorites:** Add any movie or TV show to a local favorites list.
    * **Watchlist:** Keep track of what you want to watch later.
* **Modern UI:**
    * Built with Material Design 3 components.
    * Smooth shimmer loading effects.
    * Shared element transitions for a seamless experience.

---

### 🛠️ Tech Stack & Libraries

* **Language:** Java
* **Architecture:** MVVM (ViewModel, Repository, LiveData)
* **UI:** Android SDK, Material Design 3, Shimmer (by Facebook)
* **Networking:** Retrofit 2
* **Database:** Room Persistence Library
* **Image Loading:** Picasso
* **API:** The Movie Database (TMDb) API


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
