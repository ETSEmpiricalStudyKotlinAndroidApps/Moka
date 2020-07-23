package io.github.tonnyl.moka.ui.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.databinding.FragmentInboxBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.MainNavigationFragment
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.inbox.NotificationItemEvent.*
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.widget.ListCategoryDecoration

class InboxFragment : MainNavigationFragment(), EmptyViewActions {

    private val viewModel by viewModels<InboxViewModel> {
        ViewModelFactory(
            requireContext().applicationContext as MokaApp
        )
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentInboxBinding

    private val inboxAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val adapter = InboxAdapter(viewLifecycleOwner, viewModel)
        adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )
        adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInboxBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            emptyViewActions = this@InboxFragment
            viewModel = this@InboxFragment.viewModel
            mainViewModel = this@InboxFragment.mainViewModel
            lifecycleOwner = viewLifecycleOwner

            with(binding.recyclerView) {
                if (adapter == null) {
                    addItemDecoration(
                        ListCategoryDecoration(
                            this,
                            getString(R.string.navigation_menu_inbox)
                        )
                    )

                    adapter = inboxAdapter
                }
            }
        }

        val notificationsObserver = Observer<PagingData<Notification>> {
            inboxAdapter.submitData(lifecycle, it)
        }

        mainViewModel.currentUser.observe(viewLifecycleOwner, Observer {
            viewModel.userId = it.id
            viewModel.login = it.login

            var needRefresh = false
            if (viewModel.notificationResult.hasObservers()) {
                viewModel.notificationResult.removeObserver(notificationsObserver)

                needRefresh = true
            }

            viewModel.notificationResult.observe(viewLifecycleOwner, notificationsObserver)

            if (needRefresh) {
                inboxAdapter.refresh()
            }
        })

        viewModel.event.observe(viewLifecycleOwner, Observer {
            when (val event = it.getContentIfNotHandled()) {
                is ViewNotification -> {

                }
                is ViewProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(event.login, event.type).toBundle()
                    )
                }
                is ViewRepository -> {
                    findNavController().navigate(
                        R.id.repository_fragment,
                        RepositoryFragmentArgs(event.login, event.name, event.type).toBundle()
                    )
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            inboxAdapter.refresh()
        }

        with(binding.emptyContent) {
            emptyContentTitleText.text = getString(R.string.timeline_content_empty_title)
            emptyContentActionText.text = getString(R.string.timeline_content_empty_action)
        }

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // trick: use the position as item view's order.
//        val notification = inboxAdapter.geti viewModel.data.value?.get(item.order)

        when (item.itemId) {
            R.id.notification_menu_mark_as_read -> {
                // todo
            }
            R.id.notification_menu_unsubscribe -> {
                // todo
            }
        }

        return true
    }

    override fun retryInitial() {
        inboxAdapter.refresh()
    }

    override fun doAction() {

    }

}