package io.github.tonnyl.moka.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import com.google.android.material.navigation.NavigationView
import io.github.tonnyl.moka.*
import io.github.tonnyl.moka.util.dp2px
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val compositeDisposable = CompositeDisposable()

    private val getPinnedRepositoryCall = NetworkClient.apolloClient
            .query(PinnedRepositoriesQuery.builder().login("tonnyl").build())
            .httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)
            .watcher()
    private val getViewerInfoCall = NetworkClient.apolloClient
            .query(ViewerQuery.builder().build())
            .httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)
            .watcher()

    private val layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        pinnedRepositoryList.layoutManager = layoutManager
        pinnedRepositoryList.setHasFixedSize(false)

        pinnedRepositoryList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && layoutManager.findFirstCompletelyVisibleItemPosition() != 0 && appbar.elevation == 0f) {
                    ViewCompat.setElevation(appbar, dp2px(4f, resources).toFloat())
                } else if (dy < 0 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && appbar.elevation != 0f) {
                    ViewCompat.setElevation(appbar, 0f)
                }
            }

        })

        val pinnedRepositoryDisposable = Rx2Apollo.from(getPinnedRepositoryCall)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    Log.d("onCreate", "get pinned repositories success, resp = $resp")
                    if (resp.data() != null && pinnedRepositoryList.adapter == null) {
                        pinnedRepositoryList.adapter = RepositoryAdapter(this@MainActivity, resp.data()
                                ?: return@subscribe)
                    }
                }, {
                    Log.e("onCreate", "get pinned repositories call error: ${it.message}")
                }, {

                })

        val viewerInfoDisposable = Rx2Apollo.from(getViewerInfoCall)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resp ->
                    Log.d("onCreate", "get viewer info call success, resp = $resp")
                    val data = resp.data()
                    if (data != null) {
                        val headerView = nav_view.getHeaderView(0)
                        val avatar = headerView.findViewById<AppCompatImageView>(R.id.avatar_image_view)
                        val username = headerView.findViewById<AppCompatTextView>(R.id.username_text_view)
                        val loginName = headerView.findViewById<AppCompatTextView>(R.id.login_name_text_view)

                        GlideApp.with(this)
                                .load(data.viewer().avatarUrl())
                                .circleCrop()
                                .into(avatar)
                        username.text = data.viewer().name()
                        loginName.text = data.viewer().login()
                    }
                }, {
                    Log.e("onCreate", "get viewer info call error: ${it.message}")
                }, {

                })
        compositeDisposable.addAll(pinnedRepositoryDisposable, viewerInfoDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_timeline -> {

            }
            R.id.nav_notifications -> {

            }
            R.id.nav_explore -> {

            }
            R.id.nav_gists -> {

            }
            R.id.nav_settings -> {

            }
            R.id.nav_about -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
