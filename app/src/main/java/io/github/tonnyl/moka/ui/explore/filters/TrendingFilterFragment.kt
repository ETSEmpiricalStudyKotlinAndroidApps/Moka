package io.github.tonnyl.moka.ui.explore.filters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import io.github.tonnyl.moka.ui.ViewModelFactory as MainViewModelFactory

class TrendingFilterFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private val filterAdapter: FilterAdapter by lazy {
        FilterAdapter()
    }

    private lateinit var viewModel: ExploreViewModel
    private val mainViewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)
    }

    private lateinit var binding: FragmentExploreFilterBinding

    private var queryData: Triple<ExploreTimeSpanType, String, String>? = null

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

        queryData = viewModel.queryData.value?.copy()

        MokaDataBase.getInstance(requireContext(), mainViewModel.userId.value ?: return).let {
            viewModel = ViewModelProviders.of(
                requireParentFragment(),
                ViewModelFactory(it.trendingDevelopersDao(), it.trendingRepositoriesDao())
            ).get(ExploreViewModel::class.java)
        }

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        updateToolbarTitle()

        filterAdapter.onRadioButtonClickListener = { v: View, type: ExploreTimeSpanType ->
            when (v.id) {
                R.id.item_trending_filter_time_span_weekly,
                R.id.item_trending_filter_time_span_monthly,
                R.id.item_trending_filter_time_span_daily -> {
                    queryData = queryData?.copy(first = type)

                    updateToolbarTitle()
                }
            }
        }

        filterAdapter.onLanguageItemClickListener =
            { _: View, languageParam: String, languageName: String ->
                queryData = queryData?.copy(second = languageParam, third = languageName)

                updateToolbarTitle()
            }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = filterAdapter
        }

        viewModel.languages.observe(this, Observer {
            filterAdapter.updateDataSource(it)
        })

        viewModel.loadLanguagesData(requireContext().assets.open("languages.json"))

        binding.toolbarDone.setOnClickListener(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialogInterface ->
            val d = dialogInterface as BottomSheetDialog
            val bottomSheet =
                d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            BottomSheetBehavior.from(bottomSheet).isHideable = true
        }

        return bottomSheetDialog
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.toolbar_done -> {
                viewModel.queryData.value = queryData

                dismiss()
            }
        }
    }

    private fun updateToolbarTitle() {
        binding.toolbar.title = getString(
            R.string.explore_filter_info,
            getString(
                when (queryData?.first) {
                    ExploreTimeSpanType.WEEKLY -> R.string.explore_trending_filter_time_span_weekly
                    ExploreTimeSpanType.MONTHLY -> R.string.explore_trending_filter_time_span_monthly
                    // including ExploreTimeSpanType.DAILY
                    else -> R.string.explore_trending_filter_time_span_daily
                }
            ),
            queryData?.third
        )
    }

}