package com.phi.tenatanweave.thirdparty.mpandroidchart

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class StackedBarChartValueFormatter : ValueFormatter {

    private val mDecimalFormat = DecimalFormat("#")

    override fun getFormattedValue(
        value: Float,
        entry: Entry?,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler?
    ): String {
        return if (value > 0) {
            mDecimalFormat.format(value)
        } else {
            ""
        }
    }

}