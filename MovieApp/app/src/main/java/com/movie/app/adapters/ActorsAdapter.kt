package com.movie.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.movie.app.databinding.RvMovieBgBinding
import com.movie.app.model.ActorModel

class ActorsAdapter :
    ListAdapter<ActorModel, ActorsAdapter.MovieBackgroundViewHolder>(
        MovieBackgroundDiffUtilCallback
    ) {

    inner class MovieBackgroundViewHolder(private val binding: RvMovieBgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(actorModel: ActorModel) {
            with(binding) {
                tvCrewName.text = actorModel.userName
                Glide.with(ivCrew.context).load(actorModel.userPhotoURL).circleCrop()
                    .into(ivCrew)
                tvCrewRole.text = actorModel.userRole
            }
        }
    }

    object MovieBackgroundDiffUtilCallback : DiffUtil.ItemCallback<ActorModel>() {
        override fun areItemsTheSame(
            oldItem: ActorModel,
            newItem: ActorModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ActorModel,
            newItem: ActorModel
        ): Boolean {
            return oldItem.userName == newItem.userName && oldItem.userRole == newItem.userRole
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieBackgroundViewHolder {
        val binding = RvMovieBgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieBackgroundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieBackgroundViewHolder, position: Int) {
        val movieBackgroundModel = getItem(position)
        holder.bind(movieBackgroundModel)
    }
}