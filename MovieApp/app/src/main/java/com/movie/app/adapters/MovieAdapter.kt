package com.movie.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.movie.app.databinding.RvMoviesBinding
import com.movie.app.model.MovieModel

class MovieAdapter(private val onClickItem: (MovieModel) -> Unit) :
    ListAdapter<MovieModel, MovieAdapter.MovieViewHolder>(MovieDiffUtilCallBack) {
    private var currentItemIndex = 0
    private var allList: List<MovieModel>? = null

    inner class MovieViewHolder(
        private val binding: RvMoviesBinding,
        private val onClickItem: (MovieModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var movieModel1: MovieModel? = null


        init {
            binding.clMovie.setOnClickListener {
                movieModel1?.let {
                    if (it.poster.isNullOrEmpty()) return@setOnClickListener
                    onClickItem(it)
                }
            }
        }

        fun bind(movieModel: MovieModel, currentPosition: Int) {
            with(binding) {
                Glide.with(ivMovie.context).load(movieModel.poster).apply(
                    RequestOptions.bitmapTransform(
                        RoundedCorners(64)
                    )
                ).into(ivMovie)
                tvMovie.text = movieModel.title
            }

            when (currentPosition) {
                currentItemIndex -> {
                    binding.clMovie.rotation = 0f
                    binding.ivMovie.alpha = 1f
                    binding.tvMovie.alpha = 1f
                }
                currentItemIndex - 1 -> {
                    binding.clMovie.rotation = -10f
                    binding.ivMovie.alpha = 0.24f
                    binding.tvMovie.alpha = 0.24f
                }
                currentItemIndex + 1 -> {
                    binding.clMovie.rotation = 10f
                    binding.ivMovie.alpha = 0.24f
                    binding.tvMovie.alpha = 0.24f
                }
            }
            movieModel1 = movieModel
        }
    }

    fun setCurrentItemIndex(currentItemIndex1: Int) {
        if (currentItemIndex == currentItemIndex1) return
        currentItemIndex = currentItemIndex1
        if (currentItemIndex1 - 1 != -1)
            notifyItemChanged(currentItemIndex1 - 1)
        notifyItemChanged(currentItemIndex1)
        if (currentItemIndex + 1 < itemCount)
            notifyItemChanged(currentItemIndex1 + 1)
    }


    fun searchMovieByName(name: String, chip: Int, chipName: String?) {
        val mutableList: MutableList<MovieModel> = mutableListOf()
        allList?.let {
            //selected chip name + search text
            if (chip != -1 && chip >= 0) {
                for (movie in it) {//currentList
                    chipName?.let { chipName1 ->
                        if (movie.title.lowercase()
                                .contains(name.lowercase()) && movie.genres.lowercase()
                                .contains(chipName1.lowercase())
                        ) {
                            mutableList.add(movie)
                        }
                    }

                }
            } else {
                //only search text
                for (movie in it) {//currentList
                    if (movie.title.lowercase().contains(name.lowercase())) {
                        mutableList.add(movie)
                    }
                }
            }
            if (mutableList.size == 0) {
                val count = itemCount
                submitList(null)
                notifyItemRangeRemoved(0, count)
            } else {
                submitList(mutableList.toList())
                notifyItemChanged(0, mutableList.size)
            }
        }
    }

    //permutation
    //non selected
    //only selected chip
    //only selected search text
    //selected chip + search text
    fun searchMovieByType(typeName: String, searchViewText: String) {
        val mutableList: MutableList<MovieModel> = mutableListOf()
        allList?.let {
            //selected chip name + search text
            if (searchViewText.isNotEmpty() || searchViewText.isNotBlank()) {
                for (movie in it) {

                    if (movie.genres.lowercase()
                            .contains(typeName.lowercase()) && movie.title.lowercase()
                            .contains(searchViewText.lowercase())
                    ) {
                        mutableList.add(movie)
                    }
                }
            } else {
                //only selected chip
                for (movie in it) {

                    if (movie.genres.lowercase().contains(typeName.lowercase())) {
                        mutableList.add(movie)
                    }
                }
            }

            if (mutableList.size == 0) {
                val count = itemCount
                submitList(null)
                notifyItemRangeRemoved(0, count)
            } else {
                submitList(mutableList.toList())
                notifyItemChanged(0, mutableList.size)
            }
        }
    }

    fun getWatchListMovie(name: String): List<MovieModel>? {
        allList?.let{
            return it.filter { movieModel ->
                movieModel.title.equals(name,true)
            }
        }
        return null
    }

    fun resetAllList() {
        allList?.let {
            submitList(it)
            notifyItemRangeChanged(0, it.size)

        }

    }


    fun addList(list: List<MovieModel>) {
        allList = list
    }

    object MovieDiffUtilCallBack : DiffUtil.ItemCallback<MovieModel>() {
        override fun areItemsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
            return oldItem.title == newItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = RvMoviesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding, onClickItem)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie, position)
    }


}