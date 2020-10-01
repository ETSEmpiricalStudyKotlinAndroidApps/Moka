package io.github.tonnyl.moka.ui.reaction

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.setContent
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.util.isDarkModeOn

class AddReactionDialogFragment : AppCompatDialogFragment() {

    private val args by navArgs<AddReactionDialogFragmentArgs>()

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val addReactionViewModel by viewModels<AddReactionViewModel> {
        ViewModelFactory(args)
    }

    private lateinit var contentView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .create()
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(R.layout.fragment_container, null, false)
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(contentView)
        }

        (contentView as ViewGroup).setContent(Recomposer.current()) {
            MokaTheme(darkTheme = resources.isDarkModeOn) {
                AddReactionDialogScreenContent(
                    addReactionViewModel.isContentSelected(ReactionContent.THUMBS_UP),
                    addReactionViewModel.isContentSelected(ReactionContent.THUMBS_DOWN),
                    addReactionViewModel.isContentSelected(ReactionContent.LAUGH),
                    addReactionViewModel.isContentSelected(ReactionContent.HOORAY),
                    addReactionViewModel.isContentSelected(ReactionContent.CONFUSED),
                    addReactionViewModel.isContentSelected(ReactionContent.HEART),
                    addReactionViewModel.isContentSelected(ReactionContent.ROCKET),
                    addReactionViewModel.isContentSelected(ReactionContent.EYES)
                ) { content, bool ->
                    mainViewModel.react(content, args.reactableId, bool)
                    dismissAllowingStateLoss()
                }
            }
        }
    }

}