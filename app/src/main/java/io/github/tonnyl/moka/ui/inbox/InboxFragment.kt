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
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentInboxBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.ui.*
import io.github.tonnyl.moka.ui.inbox.NotificationItemEvent.*
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.widget.ListCategoryDecoration

class InboxFragment : MainNavigationFragment(),
    EmptyViewActions, PagingNetworkStateActions {

    private val viewModel by viewModels<InboxViewModel> {
        ViewModelFactory(
            MokaDataBase.getInstance(
                requireContext(),
                mainViewModel.currentUser.value?.id ?: 0L
            ).notificationsDao()
        )
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentInboxBinding

    private val adapterWrapper by lazy(LazyThreadSafetyMode.NONE) {
        PagedListAdapterWrapper(
            LoadStateAdapter(this),
            InboxAdapter(viewLifecycleOwner, viewModel),
            LoadStateAdapter(this)
        )
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
        }

        viewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    addItemDecoration(
                        ListCategoryDecoration(
                            this,
                            getString(R.string.navigation_menu_inbox)
                        )
                    )

                    adapter = adapterWrapper.mergeAdapter
                }

                adapterWrapper.pagingAdapter.submitList(it)
            }
        })

        viewModel.previousNextLoadStatusLiveData.observe(
            viewLifecycleOwner,
            adapterWrapper.observer
        )

        mainViewModel.currentUser.observe(viewLifecycleOwner, Observer {
            viewModel.refreshData(it.login, false)
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
            viewModel.refreshData(
                mainViewModel.currentUser.value?.login
                    ?: return@setOnRefreshListener, true
            )
        }

        with(binding.emptyContent) {
            emptyContentTitleText.text = getString(R.string.timeline_content_empty_title)
            emptyContentActionText.text = getString(R.string.timeline_content_empty_action)
        }

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // trick: use the position as item view's order.
        val notification = viewModel.data.value?.get(item.order)

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
        mainViewModel.currentUser.value?.login?.let {
            viewModel.refreshData(it, true)
        }
    }

    override fun doAction() {

    }

    override fun retryLoadPreviousNext() {
        viewModel.retryLoadPreviousNext()
    }

}