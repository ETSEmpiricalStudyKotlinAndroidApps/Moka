package io.github.tonnyl.moka.ui.profile.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.databinding.FragmentEditStatusBinding
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.emojis.EmojisFragment
import io.github.tonnyl.moka.ui.profile.status.EditStatusEvent.ShowClearStatusMenu
import io.github.tonnyl.moka.ui.profile.status.EditStatusEvent.ShowEmojis
import io.github.tonnyl.moka.util.dismissKeyboard
import io.github.tonnyl.moka.util.showOverflowMenu

class EditStatusFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private val args by navArgs<EditStatusFragmentArgs>()

    private lateinit var binding: FragmentEditStatusBinding
    private val editStatusViewModel by viewModels<EditStatusViewModel>(
        factoryProducer = {
            ViewModelFactory(args)
        }
    )
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handle = findNavController().currentBackStackEntry?.savedStateHandle
        handle?.getLiveData<String>(EmojisFragment.RESULT_EMOJI)
            ?.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    editStatusViewModel.updateEmoji(it)
                }
            })

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = editStatusViewModel
            mainViewModel = this@EditStatusFragment.mainViewModel
        }

        binding.editStatusDndCheckBox.setOnClickListener {
            editStatusViewModel.updateLimitedAvailability(binding.editStatusDndCheckBox.isChecked)

            if (editStatusViewModel.message.value.isNullOrEmpty()) {
                editStatusViewModel.updateMessage(getString(R.string.edit_status_busy_message))
            }
        }

        editStatusViewModel.event.observe(viewLifecycleOwner, EventObserver { event ->
            when (event) {
                is ShowClearStatusMenu -> {
                    binding.editStatusClearStatusWhen.showOverflowMenu(
                        R.menu.fragment_edit_status_clear_when_overflow_menu,
                        this
                    )
                }
                is ShowEmojis -> {
                    findNavController().navigate(R.id.emojis_fragment)
                }
            }
        })

        editStatusViewModel.expiresAt.observe(viewLifecycleOwner, Observer { expireAt ->
            binding.editStatusClearStatusWhen.setText(
                when (expireAt) {
                    ExpireAt.In30Minutes -> {
                        R.string.edit_status_clear_status_in_30_minutes
                    }
                    ExpireAt.In1Hour -> {
                        R.string.edit_status_clear_status_in_1_hour
                    }
                    ExpireAt.Today -> {
                        R.string.edit_status_clear_status_today
                    }
                    ExpireAt.Never,
                    null -> {
                        R.string.edit_status_clear_status_never
                    }
                }
            )
        })

        val updateUserStatusObserver = Observer<Resource<UserStatus?>> {
            if (it.status == Status.SUCCESS) {
                mainViewModel.updateUserStatus(it.data)

                findNavController().navigateUp()
            }
        }

        editStatusViewModel.updateStatusState.observe(viewLifecycleOwner, updateUserStatusObserver)
        editStatusViewModel.clearStatusState.observe(viewLifecycleOwner, updateUserStatusObserver)
    }

    override fun onPause() {
        binding.setStatusMessageInputEdit.dismissKeyboard()
        super.onPause()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val expireAt = when (item?.itemId) {
            R.id.never -> {
                ExpireAt.Never
            }
            R.id.in_30_minutes -> {
                ExpireAt.In30Minutes
            }
            R.id.in_1_hour -> {
                ExpireAt.In1Hour
            }
            R.id.today -> {
                ExpireAt.Today
            }
            else -> {
                null
            }
        }

        expireAt?.let {
            editStatusViewModel.updateExpireAt(it)
        }

        return expireAt != null
    }


}