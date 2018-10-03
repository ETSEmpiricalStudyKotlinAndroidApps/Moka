package io.github.tonnyl.moka.ui.timeline

import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.util.dp2px
import kotlinx.android.synthetic.main.fragment_list.*

class TimelineFragment : Fragment(), TimelineAdapter.FetchRepositoryInfoInterface {

    private lateinit var viewModel: EventsViewModel

    companion object {

        fun newInstance(): TimelineFragment = TimelineFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arrayMap = ArrayMap<Class<out ViewModel>, ViewModel>().apply {
            put(EventsViewModel::class.java, EventsViewModel())
        }
        val factory = ViewModelFactory(arrayMap)
        viewModel = ViewModelProviders.of(this, factory).get(EventsViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager

        val appbar = activity?.findViewById<AppBarLayout>(R.id.appbar)
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && layoutManager.findFirstCompletelyVisibleItemPosition() != 0 && appbar?.elevation == 0f) {
                    ViewCompat.setElevation(appbar, dp2px(4f, resources).toFloat())
                } else if (dy < 0 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && appbar != null && appbar?.elevation != 0f) {
                    ViewCompat.setElevation(appbar, 0f)
                }
            }

        })

        viewModel.results.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.isSuccessful && response.body() != null) {
                val timelineAdapter = TimelineAdapter(response.body()!!)
                timelineAdapter.fetchRepositoryInfoInterface = this
                recycler_view.adapter = timelineAdapter
            }
        })
    }

    override fun fetchInfo(position: Int, login: String, repositoryName: String) {
        viewModel.userRepositoryCard(login, repositoryName).observe(viewLifecycleOwner, Observer { userRepoResp ->
            if (!userRepoResp.hasErrors() && recycler_view.adapter is TimelineAdapter) {
                (recycler_view.adapter as TimelineAdapter).updateRepoCard(position, userRepoResp.data()!!)
            } else {
                viewModel.orgRepositoryCard(login, repositoryName).observe(viewLifecycleOwner, Observer { orgRepoResp ->
                    if (!orgRepoResp.hasErrors() && recycler_view.adapter is TimelineAdapter) {
                        (recycler_view.adapter as TimelineAdapter).updateRepoCard(position, orgRepoResp.data()!!)
                    }
                })
            }
        })
    }

}