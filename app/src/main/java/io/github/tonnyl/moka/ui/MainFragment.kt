package io.github.tonnyl.moka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.airbnb.mvrx.MvRx
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ViewerQuery
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.ui.timeline.TimelineArgs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber

class MainFragment : Fragment() {

    private val compositeDisposable = CompositeDisposable()

    private val getViewerInfoCall = NetworkClient.apolloClient
            .query(ViewerQuery.builder().build())
            .httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)
            .watcher()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add a default argument to nav controller.
        val host: NavHostFragment = childFragmentManager.findFragmentById(R.id.main_fragment_nav_host) as NavHostFragment
        val graph = host.navController.navInflater.inflate(R.navigation.navigation_fragment_main)
        val defaultArgs = NavArgument.Builder().setDefaultValue(TimelineArgs("tonnyl")).build()
        graph.addArgument(MvRx.KEY_ARG, defaultArgs)
        host.navController.graph = graph
        nav_view.setupWithNavController(host.navController)

        val viewerInfoDisposable = Rx2Apollo.from(getViewerInfoCall)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    Timber.d("get viewer info call success, resp = $resp")
                    val data = resp.data()
                    if (data != null) {
                        val headerView = nav_view.getHeaderView(0)
                        val avatar = headerView.findViewById<AppCompatImageView>(R.id.avatar_image_view)
                        val username = headerView.findViewById<AppCompatTextView>(R.id.username_text_view)
                        val loginName = headerView.findViewById<AppCompatTextView>(R.id.login_name_text_view)

                        GlideLoader.loadAvatar(data.viewer().avatarUrl().toString(), avatar)
                        username.text = data.viewer().name()
                        loginName.text = data.viewer().login()

                        avatar.setOnClickListener {
                            val bundle = Bundle().apply {
                                putString("login", "tonnyl")
                            }
                            host.navController.navigate(R.id.action_timeline_to_user_profile, bundle)

                            drawer_layout.closeDrawer(GravityCompat.START)
                        }
                    }
                }, {
                    Timber.e(it, "get viewer info call error: ${it.message}")
                }, {

                })
        compositeDisposable.add(viewerInfoDisposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        compositeDisposable.clear()
    }

}