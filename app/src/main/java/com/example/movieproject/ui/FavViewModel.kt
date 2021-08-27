package com.example.movieproject.ui

import android.app.Application
import android.content.Context
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
import org.json.JSONArray

class FavViewModel(application: Application) : AndroidViewModel(application) {

    private var sharedPreferences = application.getSharedPreferences(
        ConstantFile.SHARED_PREFS,
        AppCompatActivity.MODE_PRIVATE
    )

    private val movieMutableLiveData = MutableLiveData<List<Movie>>()
    val movieLiveData: LiveData<List<Movie>> = movieMutableLiveData
    val errorStateLiveData = SingleLiveEvent<String>()
    private var movieList = ArrayList<Movie>()
    private var idList = ArrayList<String>()

    fun setIdArray() {
        val traverseSet = sharedPreferences.getStringSet("idSet", null)
        if (traverseSet != null) {
            for (item in traverseSet) {
                idList.add(item)
            }
        }
    }

    fun getFavMovies(page: Int, clearPage: Boolean) {
        viewModelScope.launch {
            try {
                if (clearPage) {
                    movieList.clear()
                }
                val firstIndex = (page-1)*20
                var lastIndex = ((page)*20)-1
                if (idList.lastIndex < lastIndex) {
                    lastIndex = idList.lastIndex
                }
                var counter = firstIndex
                while (counter <= lastIndex) {
                    val detailJSONArray = JSONArray(sharedPreferences.getString(idList[counter], "[]"))
                    val tempArray = ArrayList<String>()
                    for (i in 0..3) {
                        tempArray.add(detailJSONArray.get(i).toString())
                    }
                    val movieInstance = Movie(tempArray[3], tempArray[0], tempArray[1], tempArray[2])
                    movieList.add(movieInstance)
                    counter++
                }
                movieMutableLiveData.value = movieList
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
