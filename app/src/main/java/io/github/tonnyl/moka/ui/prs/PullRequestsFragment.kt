package io.github.tonnyl.moka.ui.prs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.pr.PullRequestFragmentArgs
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.android.synthetic.main.fragment_prs.*

class PullRequestsFragment : Fragment() {

    private val adapter by lazy {
        PullRequestAdapter()
    }

    private lateinit var owner: String
    private lateinit var name: String

    private lateinit var viewModel: PullRequestsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_prs, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = PullRequestsFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments"))
        owner = args.owner
        name = args.name

        toolbar.setTitle(R.string.pull_requests)
        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        swipe_refresh.setColorSchemeColors(
                ResourcesCompat.getColor(resources, R.color.indigo, null),
                ResourcesCompat.getColor(resources, R.color.teal, null),
                ResourcesCompat.getColor(resources, R.color.lightBlue, null),
                ResourcesCompat.getColor(resources, R.color.yellow, null),
                ResourcesCompat.getColor(resources, R.color.orange, null)
        )

        viewModel = ViewModelProviders.of(this, ViewModelFactory(owner, name)).get(PullRequestsViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter
        adapter.onItemClick = { number, title, _ ->
            val pullRequestFragmentArgs = PullRequestFragmentArgs.Builder(owner, name, number, title)
            parentFragment?.findNavController()?.navigate(R.id.action_to_pr, pullRequestFragmentArgs.build().toBundle())
        }

        viewModel.issuesResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

}