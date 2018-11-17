package io.github.tonnyl.moka.ui.repositories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.GlideApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.util.formatNumberWithSuffix
import kotlinx.android.synthetic.main.item_repository.view.*

class RepositoryAdapter : PagedListAdapter<RepositoryAbstract, RecyclerView.ViewHolder>(DIFF_CALLBACK), View.OnClickListener {

    private var onItemClickListener: OnItemClickListener? = null

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RepositoryAbstract>() {

            override fun areItemsTheSame(oldItem: RepositoryAbstract, newItem: RepositoryAbstract): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RepositoryAbstract, newItem: RepositoryAbstract): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = RepositoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            val repositoryAbstract = getItem(position)
            GlideApp.with(context)
                    .load(repositoryAbstract?.owner?.avatarUrl)
                    .circleCrop()
                    .into(item_repository_user_avatar)
            item_repository_title.text = repositoryAbstract?.name
            item_repository_caption.text = repositoryAbstract?.description
            item_repository_language_text.text = repositoryAbstract?.primaryLanguageName
            item_repository_star_count_text.text = formatNumberWithSuffix(repositoryAbstract?.stargazersCount
                    ?: 0)
            item_repository_fork_count_text.text = formatNumberWithSuffix(repositoryAbstract?.forksCount
                    ?: 0)
            item_repository_star.setImageResource(if (repositoryAbstract?.viewerHasStarred == true) R.drawable.ic_unstar_24 else R.drawable.ic_star_border_24)

            if (repositoryAbstract?.primaryLanguageColor != null) {
                item_repository_language_text.text = repositoryAbstract.primaryLanguageName
                (item_repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.parseColor(repositoryAbstract.primaryLanguageColor))
            } else {
                item_repository_language_text.text = context.getString(R.string.programming_language_unknown)
                (item_repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.BLACK)
            }

            this.setOnClickListener(this@RepositoryAdapter)
            this.tag = repositoryAbstract?.name
        }
    }

    override fun onClick(v: View?) {
        v ?: return
        onItemClickListener?.onItemClick(v, v.tag as String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickListener {

        fun onItemClick(view: View, repositoryName: String)

    }

}