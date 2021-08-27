package com.example.movieproject.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.movieproject.R
import com.example.movieproject.model.ConstantFile
import com.example.movieproject.model.Movie
import com.example.movieproject.util.OnItemClickListener

class MovieAdapter(
    private val layoutId: Int,
    private val listener: OnItemClickListener,
    private val adapterContext: Context?
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    var gatheredMovies: List<Movie>? = null

    inner class ViewHolder(elementView: View) : RecyclerView.ViewHolder(elementView), View.OnClickListener {
        private val imageView: ImageView = elementView.findViewById(R.id.imageofmovie)
        private val textViewTitle: TextView = elementView.findViewById(R.id.textView)
        private val textViewScore: TextView = elementView.findViewById(R.id.textView2)
        private val iconView: ImageView = elementView.findViewById(R.id.favIcon)

        init {
            elementView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        fun bind(instanceMovie: Movie) {
            val circularProgressDrawable = CircularProgressDrawable(adapterContext!!)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            Glide.with(imageView.context)
                .load(ConstantFile.IMAGE_BASE + instanceMovie.poster_path)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.brokenimg)
                .into(imageView)
            val sharedPref = adapterContext.getSharedPreferences(ConstantFile.SHARED_PREFS, Context.MODE_PRIVATE)
            if (sharedPref != null) {
                if (sharedPref.contains(instanceMovie.id)) {
                    iconView.visibility = View.VISIBLE
                } else {
                    iconView.visibility = View.GONE
                }
            }
            textViewTitle.text = instanceMovie.title
            val displayScore = "Score: ${instanceMovie.vote_average}"
            textViewScore.text = displayScore
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        gatheredMovies?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount() = gatheredMovies?.size ?: 0
}
