package io.github.tonnyl.moka.ui.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentRepositoriesBinding
import io.github.tonnyl.moka.ui.profile.UserProfileFragmentArgs
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs

class RepositoriesFragment : Fragment(), ItemRepositoryActions {

    private lateinit var viewModel: RepositoriesViewModel

    private lateinit var binding: FragmentRepositoriesBinding

    private val adapter by lazy {
        RepositoryAdapter().apply {
            repositoryActions = this@RepositoriesFragment
        }
    }

    companion object {
        const val REPOSITORY_TYPE_STARS = "stars"
        const val REPOSITORY_TYPE_OWNED = "owned"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRepositoriesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginArg = RepositoriesFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).login
        val repositoriesTypeArg = RepositoriesFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).repositoriesType
        val usernameArg = RepositoriesFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).username

        binding.appbarLayout.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        binding.appbarLayout.toolbar.title = context?.getString(if (repositoriesTypeArg == REPOSITORY_TYPE_OWNED) R.string.repositories_owned else R.string.repositories_stars, usernameArg)

        val factory = ViewModelFactory(loginArg, if (repositoriesTypeArg == REPOSITORY_TYPE_OWNED) RepositoryType.OWNED else RepositoryType.STARRED)
        viewModel = ViewModelProviders.of(this, factory).get(RepositoriesViewModel::class.java)

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = this@RepositoriesFragment.adapter
        }

        viewModel.repositoriesResults.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })

    }

    override fun openRepository(login: String, repositoryName: String) {
        val builder = RepositoryFragmentArgs(login, repositoryName)
        findNavController().navigate(R.id.action_to_repository, builder.toBundle())
    }

    override fun openProfile(login: String) {
        val builder = UserProfileFragmentArgs(login)
        findNavController().navigate(R.id.action_to_profile, builder.toBundle())
    }

    override fun starRepositoryClicked(repositoryNameWithOwner: String, star: Boolean) {

    }

}