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

class TrendingFilterFragment : BottomSheetDialogFragment(), FilterActions {

    private val filterAdapter: FilterAdapter by lazy {
        FilterAdapter().apply {
            filterActions = this@TrendingFilterFragment
        }
    }

    private val viewModel by viewModels<ExploreViewModel>(
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

    private var queryData: Pair<ExploreTimeSpanType, LocalLanguage?>? = null

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

        binding.apply {
            filterActions = this@TrendingFilterFragment
            lifecycleOwner = this@TrendingFilterFragment.viewLifecycleOwner
        }

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        updateToolbarTitle()

        viewModel.languages.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = filterAdapter
                }

                filterAdapter.updateDataSource(it)
            }
        })

        viewModel.queryData.observe(this, Observer {
            queryData = it
        })

        viewModel.loadLanguagesData(requireContext().assets.open("languages.json"))

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialogInterface ->
            val d = dialogInterface as BottomSheetDialog
            d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet).run {
                BottomSheetBehavior.from(this).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                    isHideable = true
                }
            }
        }

        return bottomSheetDialog
    }

    override fun actionSelect(language: LocalLanguage) {
        queryData = queryData?.copy(second = language)

        updateToolbarTitle()
    }

    override fun actionSelectDone() {
        // viewModel.refreshAll(queryData ?: return)

        dismiss()
    }

    override fun timeSpanSelect(id: Int) {
        queryData = queryData?.copy(
            first = when (id) {
                R.id.item_trending_filter_time_span_daily -> {
                    ExploreTimeSpanType.DAILY
                }
                R.id.item_trending_filter_time_span_weekly -> {
                    ExploreTimeSpanType.WEEKLY
                }
                // including R.id.item_trending_filter_time_span_monthly
                else -> {
                    ExploreTimeSpanType.MONTHLY
                }
            }
        )
    }

    private fun updateToolbarTitle() {
        binding.toolbar.title = getString(
            R.string.explore_filter_info,
            queryData?.second?.name ?: getString(R.string.explore_trending_filter_all_languages),
            getString(
                when (queryData?.first) {
                    ExploreTimeSpanType.WEEKLY -> R.string.explore_trending_filter_time_span_weekly
                    ExploreTimeSpanType.MONTHLY -> R.string.explore_trending_filter_time_span_monthly
                    // including ExploreTimeSpanType.DAILY
                    else -> R.string.explore_trending_filter_time_span_daily
                }
            )
        )
    }

}