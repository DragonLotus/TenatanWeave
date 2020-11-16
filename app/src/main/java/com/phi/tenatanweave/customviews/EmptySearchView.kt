package com.phi.tenatanweave.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SearchView
import com.phi.tenatanweave.R

class EmptySearchView : SearchView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        super.setOnQueryTextListener(listener)
        val mSearchSrcTextView = findViewById<SearchAutoComplete>(
            R.id.search_src_text
        )
        mSearchSrcTextView.setOnEditorActionListener { v, actionId, event ->
            listener?.onQueryTextSubmit(query.toString())
            true
        }
    }

}