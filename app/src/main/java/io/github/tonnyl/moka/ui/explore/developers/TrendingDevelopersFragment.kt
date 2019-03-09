package io.github.tonnyl.moka.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.explore.developers.TrendingDeveloperAdapter
import kotlinx.android.synthetic.main.fragment_explore_developers.*

class TrendingDevelopersFragment : Fragment() {

    private lateinit var developerAdapter: TrendingDeveloperAdapter

    private val viewModel: ExploreViewModel by lazy {
        ViewModelProviders.of(requireParentFragment(), ViewModelFactory()).get(ExploreViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_explore_developers, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        developerAdapter = TrendingDeveloperAdapter("All Languages", "Daily")
        recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.adapter = developerAdapter

        viewModel.trendingDevelopers.observe(this, Observer { response ->
            response.body()?.let {
                developerAdapter.submitList(it)
            }
        })
    }

}