package io.github.tonnyl.moka.ui.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Status
import io.github.tonnyl.moka.util.dp2px
import kotlinx.android.synthetic.main.fragment_repositories.*

class RepositoriesFragment : Fragment() {

    private lateinit var viewModel: RepositoriesViewModel

    companion object {
        const val REPOSITORY_TYPE_STARS = "stars"
        const val REPOSITORY_TYPE_OWNED = "owned"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_repositories, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginArg = RepositoriesFragmentArgs.fromBundle(arguments).login
        val repositoriesTypeArg = RepositoriesFragmentArgs.fromBundle(arguments).repositoriesType
        val usernameArg = RepositoriesFragmentArgs.fromBundle(arguments).username

        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        toolbar.title = context?.getString(if (repositoriesTypeArg == REPOSITORY_TYPE_OWNED) R.string.repositories_owned else R.string.repositories_stars, usernameArg)

        swipe_refresh.setColorSchemeColors(
                ResourcesCompat.getColor(resources, R.color.indigo, null),
                ResourcesCompat.getColor(resources, R.color.teal, null),
                ResourcesCompat.getColor(resources, R.color.lightBlue, null),
                ResourcesCompat.getColor(resources, R.color.yellow, null),
                ResourcesCompat.getColor(resources, R.color.orange, null)
        )

        val factory = ViewModelFactory(loginArg, repositoriesTypeArg)
        viewModel = ViewModelProviders.of(this, factory).get(RepositoriesViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager

        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && layoutManager.findFirstCompletelyVisibleItemPosition() != 0 && appbar?.elevation == 0f) {
                    ViewCompat.setElevation(appbar, dp2px(4f, resources).toFloat())
                } else if (dy < 0 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && appbar != null && appbar.elevation != 0f) {
                    ViewCompat.setElevation(appbar, 0f)
                }
            }

        })

        viewModel.repositoriesResults.observe(viewLifecycleOwner, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    recycler_view.adapter = RepositoryAdapter(resources.data ?: return@Observer)
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

    }

}