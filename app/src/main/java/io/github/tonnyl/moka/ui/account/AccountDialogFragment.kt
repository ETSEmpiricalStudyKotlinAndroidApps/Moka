package io.github.tonnyl.moka.ui.account

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.databinding.DialogFragmentAccountBinding
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.util.moveAccountToFirstPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AccountDialogFragment : AppCompatDialogFragment() {

    private val viewModel by viewModels<AccountViewModel>()

    private val am by lazy(LazyThreadSafetyMode.NONE) {
        AccountManager.get(requireContext())
    }

    private lateinit var binding: DialogFragmentAccountBinding

    private val accountAdapter by lazy(LazyThreadSafetyMode.NONE) {
        AccountAdapter(viewModel, viewLifecycleOwner)
    }

    private val accounts: LiveData<List<Triple<Account, String, AuthenticatedUser>>>?
        get() = (context?.applicationContext as? MokaApp)?.loginAccounts

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // In case we are showing as a dialog, use getLayoutInflater() instead.
        binding = DialogFragmentAccountBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }

        val currentAccount = (requireContext().applicationContext as MokaApp).loginAccounts
            .value?.firstOrNull()?.third ?: return

        binding.run {
            viewModel = this@AccountDialogFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        accounts?.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = accountAdapter
                }
            }

            accountAdapter.submitList(it)

            binding.accountSize = it.size

            viewModel.currentId.value = it.firstOrNull()?.third?.id
        })

        viewModel.event.observe(viewLifecycleOwner, Observer {
            when (val event = it.getContentIfNotHandled()) {
                AccountEvent.VIEW_PROFILE -> {
                    val args = ProfileFragmentArgs(currentAccount.login, ProfileType.USER)
                    findNavController().navigate(R.id.profile_fragment, args.toBundle())
                }
                AccountEvent.ADD_ANOTHER_ACCOUNT -> {
                    findNavController().navigate(R.id.auth_activity)
                }
                AccountEvent.MANAGER_GITHUB_ACCOUNTS -> {
                    startActivity(Intent(Settings.ACTION_SYNC_SETTINGS))
                }
                AccountEvent.VIEW_PRIVACY_POLICY -> {

                }
                AccountEvent.VIEW_TERMS_OF_SERVICE -> {

                }
                null -> {
                    Timber.w("event $event not handled")
                }
            }
        })

        viewModel.newSelectedAccount.observe(viewLifecycleOwner, Observer { event ->
            val current = accounts?.value
                ?.firstOrNull()
                ?.third
                ?.id

            if (event.peekContent() != current) {
                lifecycleScope.launch {
                    val newSelectedAccountId = event.getContentIfNotHandled()
                    val newSelected = accounts?.value
                        ?.firstOrNull {
                            it.third.id == newSelectedAccountId
                        } ?: return@launch
                    val ac = newSelected.first

                    try {
                        withContext(Dispatchers.IO) {
                            am.moveAccountToFirstPosition(ac)
                        }

                        findNavController().navigateUp()
                    } catch (e: Exception) {
                        Timber.e(e, "moveAccountToFirstPosition error")
                    }
                }
            }
        })

    }

}