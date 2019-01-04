package io.github.tonnyl.moka.ui.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.net.Status
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: EditProfileViewModel

    private val name: String? by lazy {
        EditProfileFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).name
    }
    private val bio: String? by lazy {
        EditProfileFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).bio
    }
    private val email: String by lazy {
        EditProfileFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).email
    }
    private val url: String? by lazy {
        EditProfileFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).url
    }
    private val company: String? by lazy {
        EditProfileFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).company
    }
    private val location: String? by lazy {
        EditProfileFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).location
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        val factory = ViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(EditProfileViewModel::class.java)

        fragment_edit_profile_email_input_edit.setText(email)
        name?.let {
            fragment_edit_profile_name_input_edit.setText(it)
        }
        bio?.let {
            fragment_edit_profile_bio_input_edit.setText(it)
        }
        url?.let {
            fragment_edit_profile_link_input_edit.setText(it)
        }
        company?.let {
            fragment_edit_profile_group_input_edit.setText(it)
        }
        location?.let {
            fragment_edit_profile_location_input_edit.setText(it)
        }

        viewModel.data.observe(viewLifecycleOwner, Observer { data ->
            when (data.status) {
                Status.SUCCESS -> {
                    parentFragment?.findNavController()?.navigateUp()
                }
                Status.ERROR -> {
                    toolbar_done.isEnabled = true
                }
                Status.LOADING -> {
                    toolbar_done.isEnabled = false
                }
            }
        })

        ViewCompat.setElevation(appbar, if (fragment_edit_profile_scroll_view.canScrollVertically(-1)) resources.getDimension(R.dimen.toolbar_elevation) else 0f)

        fragment_edit_profile_scroll_view.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (scrollY == 0 || oldScrollY == 0) {
                ViewCompat.setElevation(appbar, if (appbar.elevation == 0f) resources.getDimension(R.dimen.toolbar_elevation) else 0f)
            }
        }

        toolbar_done.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.toolbar_done -> {
                viewModel.data.updateUserInformation(
                        fragment_edit_profile_name_input_edit.text.toString(),
                        fragment_edit_profile_email_input_edit.text.toString(),
                        fragment_edit_profile_link_input_edit.text.toString(),
                        fragment_edit_profile_group_input_edit.text.toString(),
                        fragment_edit_profile_location_input_edit.text.toString(),
                        fragment_edit_profile_bio_input_edit.text.toString()
                )
            }
        }
    }

}