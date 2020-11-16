package com.phi.tenatanweave.recyclerviews.typerecycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.enums.FilterStateEnum

class TypeRecyclerAdapter(
    val context: Context,
    val typeList: MutableList<FilterStateEnum>,
    val onClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<TypeRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeRecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.type_detail_row, parent, false)
        return TypeRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeRecyclerViewHolder, position: Int) {
        holder.bindSet(context, position, typeList[position], onClickListener)

    }

    override fun getItemCount(): Int {
        return typeList.size
    }
}