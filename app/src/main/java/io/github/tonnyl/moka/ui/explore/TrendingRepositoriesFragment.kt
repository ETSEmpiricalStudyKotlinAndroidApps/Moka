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
import kotlinx.android.synthetic.main.fragment_explore_page.*

class TrendingRepositoriesFragment : Fragment() {

    private lateinit var repositoryAdapter: TrendingRepositoryAdapter

    private val viewModel: ExploreViewModel by lazy {
        ViewModelProviders.of(this, ViewModelFactory()).get(ExploreViewModel::class.java)
    }

    companion object {

        fun newInstance(): TrendingRepositoriesFragment = TrendingRepositoriesFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_explore_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repositoryAdapter = TrendingRepositoryAdapter("All Languages", "Daily")
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = repositoryAdapter

        viewModel.trendingRepositories.observe(this, Observer { response ->
            response.body()?.let {
                repositoryAdapter.submitList(it)
            }
        })
    }

}