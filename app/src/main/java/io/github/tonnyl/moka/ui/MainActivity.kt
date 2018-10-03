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
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import com.google.android.material.navigation.NavigationView
import io.github.tonnyl.moka.GlideApp
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ViewerQuery
import io.github.tonnyl.moka.ui.timeline.TimelineFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val compositeDisposable = CompositeDisposable()

    private val getViewerInfoCall = NetworkClient.apolloClient
            .query(ViewerQuery.builder().build())
            .httpCachePolicy(HttpCachePolicy.NETWORK_FIRST)
            .watcher()

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
        compositeDisposable.add(viewerInfoDisposable)
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
                val timelineFragment = TimelineFragment.newInstance()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, timelineFragment)
                        .commit()
                toolbar_title.setText(R.string.navigation_menu_timeline)
            }
            R.id.nav_notifications -> {
                toolbar_title.setText(R.string.navigation_menu_notifications)
            }
            R.id.nav_explore -> {
                toolbar_title.setText(R.string.navigation_menu_explore)
            }
            R.id.nav_gists -> {
                toolbar_title.setText(R.string.navigation_menu_gists)
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
