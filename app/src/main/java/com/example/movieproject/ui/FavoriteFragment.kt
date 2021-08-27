package com.example.movieproject.ui

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieproject.R
import com.example.movieproject.model.ConstantFile
import com.example.movieproject.model.MovieDetails
import com.example.movieproject.ui.MainFragment.Companion.KEY
import com.example.movieproject.util.MovieServiceManager
import com.example.movieproject.util.OnItemClickListener
import com.example.movieproject.util.PaginationScrollListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteFragment : Fragment(R.layout.movie_favorite), OnItemClickListener {

    private val viewModel: FavViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var homeButton: MenuItem
    private lateinit var gridButton2: MenuItem
    private lateinit var adapter: MovieAdapter
    private lateinit var gridAdapter: MovieAdapter
    private var currentPage = 1
    private var fav_layout_flag = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        adapter = MovieAdapter(R.layout.item_list, this, activity)
        gridAdapter = MovieAdapter(R.layout.item_list2, this, activity)
        initViews(view)
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as MainActivity?
        val sharedPref = activity?.getSharedPreferences(ConstantFile.SHARED_PREFS, Context.MODE_PRIVATE)
        if (sharedPref != null) {
            fav_layout_flag = sharedPref.getInt("favLayoutFlag", 1)
        }
        activity?.showUpButton()
    }

    override fun onPause() {
        super.onPause()
        val sharedPref = activity?.getSharedPreferences(ConstantFile.SHARED_PREFS, Context.MODE_PRIVATE)
        if (recyclerView.adapter == adapter) {
            sharedPref?.edit()?.putInt("favLayoutFlag", 1)?.apply()
        } else if (recyclerView.adapter == gridAdapter) {
            sharedPref?.edit()?.putInt("favLayoutFlag", 2)?.apply()
        }
    }

    private fun initViews(view: View) {
        currentPage = 1
        viewModel.setIdArray()
        viewModel.getFavMovies(currentPage, true)
        recyclerView = view.findViewById(R.id.recycler2)
    }

    override fun onItemClick(position: Int) {
        val idMovie = viewModel.getMovieId(position)
        val action = idMovie?.let { FavoriteFragmentDirections.actionFavoriteFragmentToDetailFragment(it) }
        view?.let {
            if (action != null) {
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    private fun initResultRecyclerView() {
        recyclerView.layoutManager?.let {
            recyclerView.addOnScrollListener(object : PaginationScrollListener(it) {
                override fun loadMoreItems() {
                    currentPage += 1
                    viewModel.getFavMovies(currentPage, false)
                    isLoading = false
                }

                override val isLastPage: Boolean = false
                override var isLoading: Boolean = false
            })
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.fav_icon).isVisible = false
        val linearLayoutManager = LinearLayoutManager(activity)
        val gridLayoutManager = GridLayoutManager(activity, 2)
        var noModeChangeSoFar = true
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        gridButton2 = menu.findItem(R.id.grid_icon)
        if (fav_layout_flag == 1) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = linearLayoutManager
            gridButton2.setIcon(R.drawable.ic_list)
        } else if (fav_layout_flag == 2) {
            recyclerView.adapter = gridAdapter
            recyclerView.layoutManager = gridLayoutManager
            gridButton2.setIcon(R.drawable.ic_grid)
        }
        gridButton2.setOnMenuItemClickListener {
            if (recyclerView.layoutManager == linearLayoutManager) {
                recyclerView.adapter = gridAdapter
                recyclerView.layoutManager = gridLayoutManager
                gridButton2.setIcon(R.drawable.ic_grid)
            } else {
                recyclerView.adapter = adapter
                recyclerView.layoutManager = linearLayoutManager
                gridButton2.setIcon(R.drawable.ic_list)
            }
            if (noModeChangeSoFar) {
                noModeChangeSoFar = false
                initResultRecyclerView()
            }
            return@setOnMenuItemClickListener true
        }
        homeButton = menu.findItem(R.id.home_icon)
        homeButton.setOnMenuItemClickListener {
            val fm: FragmentManager = requireActivity().supportFragmentManager
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            view?.let { it1 -> Navigation.findNavController(it1).navigate(FavoriteFragmentDirections.actionFavoriteFragmentToMainFragment()) }
            return@setOnMenuItemClickListener true
        }
        initResultRecyclerView()

        super.onPrepareOptionsMenu(menu)
    }

    private fun observeViewModel() {
        viewModel.movieLiveData.observe(viewLifecycleOwner) {
            adapter.apply {
                gatheredMovies = it
                notifyDataSetChanged()
            }
            gridAdapter.apply {
                gatheredMovies = it
                notifyDataSetChanged()
            }
        }
        viewModel.errorStateLiveData.observe(this) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }
}


