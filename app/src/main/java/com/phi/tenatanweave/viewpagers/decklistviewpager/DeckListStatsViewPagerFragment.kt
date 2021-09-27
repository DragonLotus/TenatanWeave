package com.phi.tenatanweave.viewpagers.decklistviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.TypeEnum
import com.phi.tenatanweave.thirdparty.mpandroidchart.StackedBarChartValueFormatter

class DeckListStatsViewPagerFragment : Fragment() {
    private val deckListViewModel: DeckListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_deck_list_stats, container, false)
        val barChart = root.findViewById<BarChart>(R.id.cost_barchart)

        setBarChart(barChart)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

    }

    private fun setBarChart(costBarChart: BarChart) {
        deckListViewModel.unsectionedCardPrintingDeckList.let { printingList ->
            val colors = arrayOf(
                resources.getColor(R.color.colorBlueVersion),
                resources.getColor(R.color.colorYellowVersion),
                resources.getColor(R.color.colorRedVersion)
            )
            val costList =
                printingList.filter { it.baseCard.getTypeAsEnum() != TypeEnum.EQUIPMENT && it.baseCard.getTypeAsEnum() != TypeEnum.WEAPON }
                    .distinctBy { printing -> printing.getCostSafe() }.map { it.getCostSafe() }.sorted()
            val costEntries = mutableListOf<BarEntry>()
            val costLabels = mutableListOf<String>()
            costList.forEachIndexed { index, cost ->
//                costEntries.add(BarEntry(printingList.count { it.getCostSafe() == cost }.toFloat(), index))
                costEntries.add(
                    BarEntry(
                        arrayOf(
                            printingList.count { it.getCostSafe() == cost && it.getPitchSafe() == 3 }.toFloat(),
                            printingList.count { it.getCostSafe() == cost && it.getPitchSafe() == 2 }.toFloat(),
                            printingList.count { it.getCostSafe() == cost && it.getPitchSafe() == 1 }.toFloat()
                        ).toFloatArray(), index
                    )
                )
                costLabels.add(cost.toString())
            }

            val costBarDataSet = BarDataSet(costEntries, "Cost")
            val costData = BarData(costLabels, costBarDataSet)
            costBarChart.data = costData
            costBarChart.setDescription("")
            costBarChart.axisRight.isEnabled = false
            costBarChart.setDrawValueAboveBar(false)

            costBarChart.xAxis.textColor = resources.getColor(R.color.white)
            costBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            costBarChart.axisLeft.textColor = resources.getColor(R.color.white)
            costBarChart.legend.textColor = resources.getColor(R.color.white)
            costBarDataSet.valueTextColor = resources.getColor(R.color.black)
            costBarDataSet.setColors(colors.toIntArray())
            costBarDataSet.valueFormatter = StackedBarChartValueFormatter()

            costBarChart.animateY(1000)
        }
    }

    companion object {
        private val ARG_CAUGHT = "myFragment_caught"

        fun getInstance(position: Int): DeckListStatsViewPagerFragment {
            val args: Bundle = Bundle()
            args.putSerializable(ARG_CAUGHT, position)
            val fragment = DeckListStatsViewPagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}