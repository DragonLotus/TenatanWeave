package com.phi.tenatanweave.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class DisabledScrollView : ScrollView {
    var disableScroll = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun scrollTo(x: Int, y: Int) {
        if (disableScroll)
            disableScroll = false
        else
            super.scrollTo(x, y)
    }

}