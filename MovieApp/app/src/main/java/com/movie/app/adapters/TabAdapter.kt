package com.movie.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.movie.app.databinding.RvTabBinding

class TabAdapter(private val onClickItem: (Int) -> Unit) :
    ListAdapter<String, TabAdapter.TabViewHolder>(TabDiffUtilCallBack) {

    private var currentTab = 0

    inner class TabViewHolder(
        private val binding: RvTabBinding,
        private val onClickItem: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentIndex: Int? = null

        init {
            binding.clTab.setOnClickListener {
                currentIndex?.let {
                    val oldCurrentTab = currentTab
                    currentTab = it
                    notifyItemChanged(oldCurrentTab)
                    notifyItemChanged(currentTab)
                    onClickItem(it)

                }
            }
        }

        fun bind(tabName: String, underBarVisibility: Int, currentIndex1: Int) {
            binding.tvTabName.text = tabName
            binding.ivCurrentTab.visibility = underBarVisibility
            currentIndex = currentIndex1
            if (currentTab != currentIndex) {
                binding.tvTabName.alpha = 0.24f
            } else {
                binding.tvTabName.alpha = 1f
            }
        }


    }

    fun setCurrentTab(tabIndex:Int){
        val oldCurrentTab = currentTab
        currentTab = tabIndex
        notifyItemChanged(oldCurrentTab)
        notifyItemChanged(currentTab)
    }


    object TabDiffUtilCallBack : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val binding = RvTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TabViewHolder(binding,onClickItem)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        val tabName = getItem(position)
        holder.bind(tabName, if (currentTab == position) View.VISIBLE else View.INVISIBLE, position)
    }
}