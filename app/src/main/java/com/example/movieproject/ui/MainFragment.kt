package com.example.movieproject.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieproject.R
import com.example.movieproject.model.ConstantFile
import com.example.movieproject.util.OnItemClickListener
import com.example.movieproject.util.PaginationScrollListener

@SuppressLint("NotifyDataSetChanged")
class MainFragment : Fragment(R.layout.main_fragment), OnItemClickListener {

    companion object {
        const val KEY = "126c1841f7790261a01e18e9a7408d9b"
    }

    private val viewModel: MainViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonSearch: Button
    private lateinit var gridButton2: MenuItem
    private lateinit var favIconButton: MenuItem
    private var initialString = ""
    private lateinit var adapter: MovieAdapter
    private lateinit var gridAdapter: MovieAdapter
    private var currentPage = 1
    private var layout_flag = 1

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
            layout_flag = sharedPref.getInt("layoutFlag", 1)
        }
        activity?.hideUpButton()
    }

    override fun onPause() {
        super.onPause()
        val sharedPref = activity?.getSharedPreferences(ConstantFile.SHARED_PREFS, Context.MODE_PRIVATE)
        if (recyclerView.adapter == adapter) {
            sharedPref?.edit()?.putInt("layoutFlag", 1)?.apply()
        } else if (recyclerView.adapter == gridAdapter) {
            sharedPref?.edit()?.putInt("layoutFlag", 2)?.apply()
        }
    }

    private fun initViews(view: View) {
        currentPage = 1
        viewModel.getMovies(KEY, initialString, currentPage, true)
        recyclerView = view.findViewById(R.id.recycler)
        buttonSearch = view.findViewById(R.id.search_button)
        buttonSearch.setOnClickListener {
            currentPage = 1
            val prompt = view.findViewById<EditText>(R.id.prompt)
            initialString = prompt.text.toString()
            viewModel.getMovies(KEY, initialString, currentPage, true)
            recyclerView.layoutManager?.scrollToPosition(0)
        }
    }

    override fun onItemClick(position: Int) {
        val idMovie = viewModel.getMovieId(position)
        val action = idMovie?.let { MainFragmentDirections.actionMainFragmentToDetailFragment(it) }
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
                    viewModel.getMovies(KEY, initialString, currentPage, false)
                    isLoading = false
                }

                override val isLastPage: Boolean = false
                override var isLoading: Boolean = false
            })
        }
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.home_icon).isVisible = false
        val linearLayoutManager = LinearLayoutManager(activity)
        val gridLayoutManager = GridLayoutManager(activity, 2)
        var noModeChangeSoFar = true
        gridButton2 = menu.findItem(R.id.grid_icon)
        if (layout_flag == 1) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = linearLayoutManager
            gridButton2.setIcon(R.drawable.ic_list)
        } else if (layout_flag == 2) {
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
        favIconButton = menu.findItem(R.id.fav_icon)
        favIconButton.setOnMenuItemClickListener {
            view?.let { it1 -> Navigation.findNavController(it1).navigate(MainFragmentDirections.actionMainFragmentToFavoriteFragment()) }
            return@setOnMenuItemClickListener true
        }
        initResultRecyclerView()

        super.onPrepareOptionsMenu(menu)
    }
}
