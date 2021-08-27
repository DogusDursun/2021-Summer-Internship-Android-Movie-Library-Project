package com.example.movieproject.ui

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.* // ktlint-disable no-wildcard-imports
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.movieproject.R
import com.example.movieproject.model.ConstantFile
import com.example.movieproject.model.MovieDetails
import com.example.movieproject.ui.MainFragment.Companion.KEY
import com.example.movieproject.util.MovieServiceManager
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFragment : Fragment(R.layout.movie_detail), Callback<MovieDetails> {

    private lateinit var buttonFav: Button
    private var imageView: ImageView? = null
    private var textViewTitle: TextView? = null
    private var textViewBudget: TextView? = null
    private var textViewLanguage: TextView? = null
    private var textViewRevenue: TextView? = null
    private var textViewGenre: TextView? = null
    private var textViewProduction: TextView? = null
    private var textViewStatus: TextView? = null
    private var textViewReleaseDate: TextView? = null
    private var textViewScore: TextView? = null
    private var textViewVoteCount: TextView? = null
    private var textViewRuntime: TextView? = null
    private lateinit var textViewOverview: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var idMovieGathered: String

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var homeButton: MenuItem
    private lateinit var favIconButton: MenuItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        idMovieGathered = args.idMovie
        initViews(view)
        MovieServiceManager.movieService.gatherDetails(idMovieGathered, KEY).enqueue(this)
    }

    private fun initViews(view: View) {
        imageView = view.findViewById(R.id.imageofmovie)
        textViewTitle = view.findViewById(R.id.textView)
        textViewBudget = view.findViewById(R.id.textView2)
        textViewLanguage = view.findViewById(R.id.textView3)
        textViewRevenue = view.findViewById(R.id.textView4)
        textViewGenre = view.findViewById(R.id.textView5)
        textViewProduction = view.findViewById(R.id.textView6)
        textViewStatus = view.findViewById(R.id.textView7)
        textViewReleaseDate = view.findViewById(R.id.textView8)
        textViewScore = view.findViewById(R.id.textView10)
        textViewVoteCount = view.findViewById(R.id.textView11)
        textViewRuntime = view.findViewById(R.id.textView12)
        textViewOverview = view.findViewById(R.id.textView14)
        buttonFav = view.findViewById(R.id.fav_button)
        progressBar = view.findViewById(R.id.loading)
        progressBar.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as MainActivity?
        activity?.showUpButton()
        val sharedPref =
            activity?.getSharedPreferences(ConstantFile.SHARED_PREFS, Context.MODE_PRIVATE)
        if (sharedPref != null) {
            if (sharedPref.contains(idMovieGathered)) {
                buttonFav.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_fav_falser, 0)
                buttonFav.setText(R.string.remove_from_favorite)
                buttonFav.setBackgroundColor(resources.getColor(R.color.reeed))
            } else {
                buttonFav.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_favorite_24, 0)
                buttonFav.setText(R.string.add_to_favorite)
                buttonFav.setBackgroundColor(resources.getColor(R.color.green))
            }
        }
    }

    override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
        if (response.isSuccessful) {
            val apiResponse: MovieDetails? = response.body()
            apiResponse?.let {
                imageView?.let { it1 ->
                    Glide.with(it1.context)
                        .load(ConstantFile.IMAGE_BASE + it.posterPath)
                        .error(R.drawable.brokenimg)
                        .into(imageView!!)
                }
                textViewTitle?.text = it.title
                val displayBudget = "Budget: ${it.budget}$"
                textViewBudget?.text = displayBudget
                var passString: String = ""
                var first_flag = true
                for (item in it.genres!!) {
                    if (!first_flag) {
                        passString += ", "
                    } else {
                        first_flag = false
                    }
                    passString += item?.genreName
                }
                val displayGenres = "Genre: $passString"
                textViewGenre?.text = displayGenres
                val displayLanguage = "Language: ${it.originalLanguage}"
                textViewLanguage?.text = displayLanguage
                textViewOverview.text = it.overview
                textViewOverview.movementMethod = ScrollingMovementMethod()
                passString = ""
                first_flag = true
                for (item in it.productionCountries!!) {
                    if (!first_flag) {
                        passString += ", "
                    } else {
                        first_flag = false
                    }
                    passString += item?.productCountry
                }
                val displayProductionCountries = "Production: $passString"
                textViewProduction?.text = displayProductionCountries
                val displayReleaseDate = "Release Date: ${it.releaseDate}"
                textViewReleaseDate?.text = displayReleaseDate
                val displayRevenue = "Revenue: ${it.revenue}$"
                textViewRevenue?.text = displayRevenue
                val displayRuntime = "Runtime: ${it.runtime} minutes"
                textViewRuntime?.text = displayRuntime
                val displayScore = "Score: ${it.voteAverage}"
                textViewScore?.text = displayScore
                val displayStatus = "Status: ${it.status}"
                textViewStatus?.text = displayStatus
                val displayVoteCount = "Vote Count: ${it.voteCount}"
                textViewVoteCount?.text = displayVoteCount
                val favVoteAverage = it.voteAverage
                val favTitle = it.title
                val favPosterPath = it.posterPath
                buttonFav.setOnClickListener {
                    val sharedPref = activity?.getSharedPreferences(
                        ConstantFile.SHARED_PREFS,
                        Context.MODE_PRIVATE
                    )
                    val idSetPasser: Set<String> =
                        sharedPref?.getStringSet("idSet", HashSet<String>()) as Set<String>
                    val idSetHolder = HashSet<String>(idSetPasser)
                    if (idSetHolder.contains(idMovieGathered)) {
                        buttonFav.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_baseline_favorite_24,
                            0
                        )
                        buttonFav.setText(R.string.add_to_favorite)
                        buttonFav.setBackgroundColor(resources.getColor(R.color.green))
                        idSetHolder.remove(idMovieGathered)
                        sharedPref.edit()?.remove(idMovieGathered)?.apply()
                    } else {
                        idSetHolder.add(idMovieGathered)
                        buttonFav.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_fav_falser,
                            0
                        )
                        buttonFav.setText(R.string.remove_from_favorite)
                        buttonFav.setBackgroundColor(resources.getColor(R.color.reeed))
                        val details = setOf(
                            favTitle,
                            favVoteAverage,
                            favPosterPath,
                            idMovieGathered
                        )
                        val jsonArray = JSONArray(details)
                        sharedPref.edit()?.putString(idMovieGathered, jsonArray.toString())?.apply()
                    }
                    sharedPref.edit()?.putStringSet("idSet", idSetHolder)?.apply()
                }
            }
        }
        progressBar.visibility = View.GONE
    }

    override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
        Log.e("service response", "fail", t)
        progressBar.visibility = View.GONE
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.grid_icon).isVisible = false
        homeButton = menu.findItem(R.id.home_icon)
        favIconButton = menu.findItem(R.id.fav_icon)
        homeButton.setOnMenuItemClickListener {
            val fm: FragmentManager = requireActivity().supportFragmentManager
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            view?.let { it1 ->
                Navigation.findNavController(it1)
                    .navigate(DetailFragmentDirections.actionDetailFragmentToMainFragment())
            }
            return@setOnMenuItemClickListener true
        }
        favIconButton.setOnMenuItemClickListener {
            view?.let { it1 ->
                Navigation.findNavController(it1)
                    .navigate(DetailFragmentDirections.actionDetailFragmentToFavoriteFragment())
            }
            return@setOnMenuItemClickListener true
        }
        super.onPrepareOptionsMenu(menu)
    }
}
