package io.github.tonnyl.moka.ui.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.databinding.ItemProjectBinding

class ProjectAdapter : PagingDataAdapter<Project, ProjectAdapter.ProjectViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        return ProjectViewHolder(
            ItemProjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_project
    }

    class ProjectViewHolder(
        private val binding: ItemProjectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Project) {
            with(binding) {
                project = data

                executePendingBindings()
            }
        }

    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Project>() {

            override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem == newItem
            }

        }

    }

}