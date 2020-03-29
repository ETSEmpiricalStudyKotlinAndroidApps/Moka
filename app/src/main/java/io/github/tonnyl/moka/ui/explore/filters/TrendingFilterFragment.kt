package io.github.tonnyl.moka.ui.explore.filters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentExploreFilterBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.explore.ExploreTimeSpanType
import io.github.tonnyl.moka.ui.explore.ExploreViewModel
import io.github.tonnyl.moka.ui.explore.ViewModelFactory
import io.github.tonnyl.moka.ui.explore.filters.FilterEvent.*

class TrendingFilterFragment : BottomSheetDialogFragment() {

    private val filterAdapter: FilterAdapter by lazy {
        FilterAdapter(viewLifecycleOwner, exploreViewModel)
    }

    private val exploreViewModel by viewModels<ExploreViewModel>(
        ownerProducer = {
            requireParentFragment()
        },
        factoryProducer = {
            MokaDataBase.getInstance(
                requireContext(),
                mainViewModel.currentUser.value?.id ?: 0L
            ).let {
                ViewModelFactory(it.trendingDevelopersDao(), it.trendingRepositoriesDao())
            }
        }
    )
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentExploreFilterBinding

    companion object {

        fun newInstance(): TrendingFilterFragment = TrendingFilterFragment()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreFilterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = exploreViewModel
        }

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        updateToolbarTitle()

        exploreViewModel.languages.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = filterAdapter
                }

                filterAdapter.updateDataSource(it)
            }
        })

        exploreViewModel.filterEvent.observe(viewLifecycleOwner, Observer {
            when (val event = it.getContentIfNotHandled()) {
                is SelectLanguage -> {
                    updateToolbarTitle()
                }
                is ConfirmSelection -> {
                    // viewModel.refreshAll(queryData ?: return)

                    dismiss()
                }
                is SelectTimeSpan -> {

                }
            }
        })

        exploreViewModel.loadLanguagesData(requireContext().assets.open("languages.json"))

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialogInterface ->
            val d = dialogInterface as BottomSheetDialog
            d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.run {
                BottomSheetBehavior.from(this).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                    isHideable = true
                }
            }
        }

        return bottomSheetDialog
    }

    private fun updateToolbarTitle() {
        binding.toolbar.title = getString(
            R.string.explore_filter_info,
            exploreViewModel.queryData.value?.second?.name
                ?: getString(R.string.explore_trending_filter_all_languages),
            getString(
                when (exploreViewModel.queryData.value?.first) {
                    ExploreTimeSpanType.WEEKLY -> R.string.explore_trending_filter_time_span_weekly
                    ExploreTimeSpanType.MONTHLY -> R.string.explore_trending_filter_time_span_monthly
                    // including ExploreTimeSpanType.DAILY
                    else -> R.string.explore_trending_filter_time_span_daily
                }
            )
        )
    }

}