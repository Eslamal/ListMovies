# ListMovies App
Android application developed in Java fetches and displays a list of movies from The Movie Database (TMDb) API. 
It supports caching movies in a local Room database for offline use and includes functionalities like showing top-rated and popular movies, 
search functionality, and movie details. The app also supports changing the language between Arabic and English from the settings.

## Features
- Fetch Movies: Fetches top-rated and popular movies from the TMDb API.
- Movie Details: Displays detailed information about selected movies.
- Search Movies: Search for movies using the TMDb API.
- Offline Mode: Caches movies in a local Room database for offline access.
- Language Switch: Switch between Arabic and English languages from the settings menu.
- Error Handling: Shows a message if there is a network error.

## Technologies Used

- **Java**: Programming language used for the app.
- **Retrofit**: Library for making HTTP requests to fetch data from TMDb API.
- **Room**: Library for local data storage.
- **ViewModel & LiveData**: Architecture components for managing UI-related data.
- **Picasso**: Library for loading and caching images.
- **RecyclerView**: For displaying a list of movies.

## Project Structure

- `MainActivity.java`: The main entry point of the app.
- `TopRatedFragment.java`: Fragment for displaying top-rated movies.
- `PopularFragment.java`: Fragment for displaying popular movies.
- `SearchActivity.java`: Activity for searching movies.
- `DetailsActivity.java`: Activity for displaying movie details.
- `MovieAdapter.java`: RecyclerView adapter for displaying movies.
- `MovieViewModel.java`: ViewModel for managing UI-related movie data.
- `MovieRepository.java`: Repository for managing data operations.
- `MovieDao.java`: Data Access Object for Room database.
- `AppDatabase.java`: Room database configuration.
- `TMDbApi.java`: API interface for TMDb.
- `MovieMapper.java`: Class for mapping data between model and entity objects.
