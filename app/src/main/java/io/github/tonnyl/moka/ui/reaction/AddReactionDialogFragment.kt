package io.github.tonnyl.moka.ui.reaction

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.tonnyl.moka.databinding.DialogFragmentAddReactionBinding
import io.github.tonnyl.moka.ui.MainViewModel

class AddReactionDialogFragment : AppCompatDialogFragment() {

    private val args by navArgs<AddReactionDialogFragmentArgs>()

    private lateinit var binding: DialogFragmentAddReactionBinding

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val addReactionViewModel by viewModels<AddReactionViewModel> {
        ViewModelFactory(args)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentAddReactionBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            reactableId = args.reactableId
            mainViewModel = this@AddReactionDialogFragment.mainViewModel
            viewModel = addReactionViewModel
        }

    }

}