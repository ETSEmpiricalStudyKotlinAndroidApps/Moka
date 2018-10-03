package io.github.tonnyl.moka.ui.timeline

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.GlideApp
import io.github.tonnyl.moka.OrgRepositoryCardInfoQuery
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.UserRepositoryCardInfoQuery
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.util.formatNumberWithSuffix
import kotlinx.android.synthetic.main.item_event.view.*

class TimelineAdapter(
        private val events: List<Event>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private val userRepoCardMap = mutableMapOf<Int, UserRepositoryCardInfoQuery.Data?>()
    private val orgRepoCardMap = mutableMapOf<Int, OrgRepositoryCardInfoQuery.Data?>()

    var fetchRepositoryInfoInterface: FetchRepositoryInfoInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = events[position]
        val userRepo = userRepoCardMap[position]
        val orgRepo = orgRepoCardMap[position]
        with(holder.itemView) {
            GlideApp.with(context)
                    .load(item.actor.avatarUrl)
                    .circleCrop()
                    .into(event_user_avatar)

            event_actor_username.text = item.actor.login
            event_action.text = when (item.type) {
                Event.WATCH_EVENT -> "starred"
                Event.CREATE_EVENT -> "created a repository"
                Event.FORK_EVENT -> "forked"
                else -> ""
            }
            event_repository_name.text = item.repo.name
            event_create_time.text = DateUtils.getRelativeTimeSpanString(item.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)

            event_actor_username.setOnClickListener(this@TimelineAdapter)
            event_user_avatar.setOnClickListener(this@TimelineAdapter)
            event_repository_name.setOnClickListener(this@TimelineAdapter)

            if (userRepo == null && orgRepo == null) {
                val slices = item.repo.name.split("/")
                fetchRepositoryInfoInterface?.fetchInfo(position, slices[0], slices[1])
                event_repository_title.visibility = View.INVISIBLE
                event_repository_star_count_text.visibility = View.INVISIBLE
                event_repository_fork_count_text.visibility = View.INVISIBLE
                event_repository_language_text.visibility = View.INVISIBLE
                event_repository_avatar.visibility = View.INVISIBLE
                event_repository_loading.visibility = View.VISIBLE
                event_repository_loading.playAnimation()
            } else {
                event_repository_title.visibility = View.VISIBLE
                event_repository_star_count_text.visibility = View.VISIBLE
                event_repository_fork_count_text.visibility = View.VISIBLE
                event_repository_language_text.visibility = View.VISIBLE
                event_repository_avatar.visibility = View.VISIBLE
                event_repository_loading.visibility = View.GONE
                event_repository_loading.cancelAnimation()

                GlideApp.with(context)
                        .load(userRepo?.user()?.avatarUrl() ?: orgRepo?.organization()?.avatarUrl())
                        .circleCrop()
                        .into(event_repository_avatar)

                event_repository_title.text = userRepo?.user()?.repository()?.name() ?: orgRepo?.organization()?.repository()?.name()
                event_repository_star_count_text.visibility = View.VISIBLE
                event_repository_star_count_text.text = formatNumberWithSuffix(userRepo?.user()?.repository()?.stargazers()?.totalCount()
                        ?: orgRepo?.organization()?.repository()?.stargazers()?.totalCount() ?: 0)
                event_repository_fork_count_text.text = formatNumberWithSuffix(userRepo?.user()?.repository()?.forks()?.totalCount()
                        ?: orgRepo?.organization()?.repository()?.forks()?.totalCount() ?: 0)

                val color = userRepo?.user()?.repository()?.primaryLanguage()?.color()
                        ?: orgRepo?.organization()?.repository()?.primaryLanguage()?.color()
                if (color != null) {
                    event_repository_language_text.text = userRepo?.user()?.repository()?.primaryLanguage()?.name() ?: orgRepo?.organization()?.repository()?.primaryLanguage()?.name()
                    (event_repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.parseColor(color))
                } else {
                    event_repository_language_text.text = context.getString(R.string.programming_language_unknown)
                    (event_repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.BLACK)
                }
            }
        }
    }

    override fun getItemCount(): Int = events.size

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.event_user_avatar, R.id.event_actor_username -> {

            }
            R.id.event_repository_name -> {

            }
        }
    }

    fun updateRepoCard(position: Int, data: UserRepositoryCardInfoQuery.Data) {
        userRepoCardMap[position] = data
        notifyItemChanged(position)
    }

    fun updateRepoCard(position: Int, data: OrgRepositoryCardInfoQuery.Data) {
        orgRepoCardMap[position] = data
        notifyItemChanged(position)
    }

    interface FetchRepositoryInfoInterface {

        fun fetchInfo(position: Int, login: String, repositoryName: String)

    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view)

}