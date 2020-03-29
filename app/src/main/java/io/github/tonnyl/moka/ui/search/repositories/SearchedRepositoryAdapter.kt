package io.github.tonnyl.moka.ui.search.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.databinding.ItemSearchedRepositoryBinding
import io.github.tonnyl.moka.ui.PagedResourceAdapter
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class SearchedRepositoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: SearchedRepositoriesViewModel,
    override val retryActions: PagingNetworkStateActions
) : PagedResourceAdapter<SearchedRepositoryItem>(DIFF_CALLBACK, retryActions) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchedRepositoryItem>() {

            override fun areItemsTheSame(
                oldItem: SearchedRepositoryItem,
                newItem: SearchedRepositoryItem
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SearchedRepositoryItem,
                newItem: SearchedRepositoryItem
            ): Boolean = oldItem.id == newItem.id

        }

    }

    override fun initiateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RepositoryViewHolder(
            lifecycleOwner,
            viewModel,
            ItemSearchedRepositoryBinding.inflate(inflater, parent, false)
        )
    }

    override fun bindHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        (holder as RepositoryViewHolder).bindTo(item)
    }

    override fun getViewType(position: Int): Int {
        return R.layout.item_searched_repository
    }

    class RepositoryViewHolder(
        private val owner: LifecycleOwner,
        private val model: SearchedRepositoriesViewModel,
        private val binding: ItemSearchedRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(repo: SearchedRepositoryItem) {
            with(binding) {
                lifecycleOwner = owner
                data = repo
                viewModel = model

                executePendingBindings()
            }
        }

    }

}