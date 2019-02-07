package io.github.tonnyl.moka.ui.issues

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
import io.github.tonnyl.moka.ui.issue.IssueFragmentArgs
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.android.synthetic.main.fragment_issues.*

class IssuesFragment : Fragment() {

    private val adapter: IssueAdapter by lazy {
        IssueAdapter()
    }

    private lateinit var owner: String
    private lateinit var name: String

    private lateinit var viewModel: IssuesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_issues, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = IssuesFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments"))
        owner = args.owner
        name = args.name

        toolbar.setTitle(R.string.issues)
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

        viewModel = ViewModelProviders.of(this, ViewModelFactory(owner, name)).get(IssuesViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager
        adapter.onItemClick = { i, s, _ ->
            val issueFragmentArgs = IssueFragmentArgs.Builder(owner, name, i, s)
            parentFragment?.findNavController()?.navigate(R.id.action_to_issue, issueFragmentArgs.build().toBundle())
        }
        recycler_view.adapter = adapter

        viewModel.issuesResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

}