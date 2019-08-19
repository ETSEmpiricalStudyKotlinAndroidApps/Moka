package io.github.tonnyl.moka.ui.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.databinding.ItemProjectBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class ProjectAdapter(
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<Project>(
    DIFF_CALLBACK,
    retryActions
) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Project>() {

            override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
                return oldItem == newItem
            }

        }

        const val VIEW_TYPE_PROJECT = R.layout.item_project

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProjectViewHolder(
            ItemProjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is ProjectViewHolder) {
            holder.bindTo(item)
        }
    }

    override fun getViewType(position: Int): Int = VIEW_TYPE_PROJECT

    class ProjectViewHolder(
        private val binding: ItemProjectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: Project) {
            binding.apply {
                project = data
            }

            binding.executePendingBindings()
        }

    }

}