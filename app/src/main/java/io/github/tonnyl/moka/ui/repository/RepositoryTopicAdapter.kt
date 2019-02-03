package io.github.tonnyl.moka.ui.repository

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Topic
import kotlinx.android.synthetic.main.item_repository_topic.view.*

class RepositoryTopicAdapter(
        private val topics: List<Topic>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = RepositoryTopicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repository_topic, parent, false))

    override fun getItemCount(): Int = topics.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = topics[position]
        if (holder is RepositoryTopicViewHolder) {
            holder.bindTo(data)
        }
    }

    class RepositoryTopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindTo(data: Topic) {
            with(itemView) {
                item_repository_topic_name.text = data.name
            }
        }

    }

}