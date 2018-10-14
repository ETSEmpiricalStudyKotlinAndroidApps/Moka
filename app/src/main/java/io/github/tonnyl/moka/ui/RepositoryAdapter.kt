package io.github.tonnyl.moka.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.GlideApp
import io.github.tonnyl.moka.PinnedRepositoriesQuery
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.util.formatNumberWithSuffix
import kotlinx.android.synthetic.main.item_repository.view.*

class RepositoryAdapter(
        private val repositories: PinnedRepositoriesQuery.Data
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = RepositoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false))

    override fun getItemCount(): Int = repositories.user()?.pinnedRepositories()?.nodes()?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            val user = repositories.user() ?: return@with
            val repository = user.pinnedRepositories().nodes()?.get(position) ?: return@with
            GlideApp.with(context)
                    .load(user.avatarUrl())
                    .circleCrop()
                    .into(item_repository_user_avatar)
            item_repository_title.text = repository.name()
            item_repository_caption.text = repository.description()
            item_repository_language_text.text = repository.primaryLanguage()?.name()
            item_repository_star_count_text.text = formatNumberWithSuffix(repository.stargazers().totalCount())
            item_repository_fork_count_text.text = formatNumberWithSuffix(repository.forks().totalCount())
            (item_repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.parseColor(repository.primaryLanguage()?.color()))

            item_repository_star.setImageResource(if (repository.viewerHasStarred()) R.drawable.ic_unstar_24 else R.drawable.ic_star_border_24)
        }
    }

    class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

}