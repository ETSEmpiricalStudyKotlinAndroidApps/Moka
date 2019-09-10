package io.github.tonnyl.moka.ui.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentEditProfileBinding
import io.github.tonnyl.moka.network.Status

class EditProfileFragment : Fragment(), View.OnClickListener {

    private val args by navArgs<EditProfileFragmentArgs>()

    private val viewModel by viewModels<EditProfileViewModel>()

    private lateinit var binding: FragmentEditProfileBinding

    private var name: String? = null
    private var bio: String? = null
    private var email: String? = null
    private var url: String? = null
    private var company: String? = null
    private var location: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = args.name
        bio = args.bio
        email = args.email
        url = args.url
        company = args.company
        location = args.location

        binding.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        binding.fragmentEditProfileEmailInputEdit.setText(email)
        name?.let {
            binding.fragmentEditProfileNameInputEdit.setText(it)
        }
        bio?.let {
            binding.fragmentEditProfileBioInputEdit.setText(it)
        }
        url?.let {
            binding.fragmentEditProfileLinkInputEdit.setText(it)
        }
        company?.let {
            binding.fragmentEditProfileGroupInputEdit.setText(it)
        }
        location?.let {
            binding.fragmentEditProfileLocationInputEdit.setText(it)
        }

        viewModel.loadingStatus.observe(this, Observer { data ->
            when (data.status) {
                Status.SUCCESS -> {
                    parentFragment?.findNavController()?.navigateUp()
                }
                Status.ERROR -> {
                    binding.toolbarDone.isEnabled = true
                }
                Status.LOADING -> {
                    binding.toolbarDone.isEnabled = false
                }
            }
        })

        binding.toolbarDone.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.toolbar_done -> {
                viewModel.updateUserInformation(
                    binding.fragmentEditProfileNameInputEdit.text.toString(),
                    binding.fragmentEditProfileEmailInputEdit.text.toString(),
                    binding.fragmentEditProfileLinkInputEdit.text.toString(),
                    binding.fragmentEditProfileGroupInputEdit.text.toString(),
                    binding.fragmentEditProfileLocationInputEdit.text.toString(),
                    binding.fragmentEditProfileBioInputEdit.text.toString()
                )
            }
        }
    }

}