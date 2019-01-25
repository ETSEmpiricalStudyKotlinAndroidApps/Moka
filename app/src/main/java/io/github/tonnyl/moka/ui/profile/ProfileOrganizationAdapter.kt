package io.github.tonnyl.moka.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.UserQuery
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.item_profile_organization_simple.view.*

class ProfileOrganizationAdapter(
        private val organizations: List<UserQuery.Node>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ProfileOrganizationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_organization_simple, parent, false))

    override fun getItemCount(): Int = organizations.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            val data = organizations[position]

            GlideLoader.loadAvatar(data.avatarUrl().toString(), item_profile_organization_avatar)
        }
    }

    class ProfileOrganizationViewHolder(view: View) : RecyclerView.ViewHolder(view)

}