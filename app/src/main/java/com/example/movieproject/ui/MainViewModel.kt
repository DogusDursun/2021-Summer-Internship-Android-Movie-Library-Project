package com.example.movieproject.ui

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieproject.model.ConstantFile
import com.example.movieproject.model.Movie
import com.example.movieproject.model.MovieResults
import com.example.movieproject.util.MovieServiceManager
import com.example.movieproject.util.SingleLiveEvent
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var sharedPreferences = application.getSharedPreferences(
        ConstantFile.SHARED_PREFS,
        AppCompatActivity.MODE_PRIVATE
    )

    private val movieMutableLiveData = MutableLiveData<List<Movie>>()
    val movieLiveData: LiveData<List<Movie>> = movieMutableLiveData
    val errorStateLiveData = SingleLiveEvent<String>()
    private var movieList = ArrayList<Movie>()

    fun addMoreMovies(movieCollection: MovieResults) {
        for (item in movieCollection.results) {
            movieList.add(item)
        }
        movieMutableLiveData.value = movieList
    }

    fun getMovies(aKey: String, query: String, pageNumber: Int, clearPage: Boolean) {
        viewModelScope.launch {
            try {
                if (clearPage) {
                    movieList.clear()
                }
                if (query == "") {
                    val collection = MovieServiceManager.movieService.gatherMovies(aKey, pageNumber)
                    addMoreMovies(collection)
                } else {
                    val collection = MovieServiceManager.movieService.gatherSearchedMovies(aKey, query, pageNumber)
                    addMoreMovies(collection)
                }
            } catch (e: Exception) {
                errorStateLiveData.postValue("Error Encountered")
                Log.e("Error", "Error calling service", e)
            }
        }
    }

    fun getMovieId(position: Int): String? {
        return movieList[position].id
    }
}
