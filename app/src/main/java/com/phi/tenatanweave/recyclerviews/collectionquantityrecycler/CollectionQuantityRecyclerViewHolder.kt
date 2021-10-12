package com.phi.tenatanweave.recyclerviews.collectionquantityrecycler

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.phi.tenatanweave.R
import com.phi.tenatanweave.data.CollectionEntry
import com.phi.tenatanweave.data.Printing
import com.phi.tenatanweave.data.enums.FinishEnum
import kotlinx.android.synthetic.main.collection_quantity_detail_grid_row.view.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class CollectionQuantityRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCard(
        finish: FinishEnum,
        currentSetCollectionEntryMap: MutableMap<String, CollectionEntry>,
        printing: Printing,
        updateOrAddCollectionEntry: (Int, Printing, FinishEnum, Resources) -> Unit,
        context: Context
    ) {

        itemView.collection_quantity_finish_image.visibility = View.VISIBLE
        when (finish) {
            FinishEnum.RAINBOW -> itemView.collection_quantity_finish_image.setImageResource(R.drawable.rainbow_finish)
            FinishEnum.COLD -> itemView.collection_quantity_finish_image.setImageResource(R.drawable.cold_finish)
            FinishEnum.GOLD -> itemView.collection_quantity_finish_image.setImageResource(R.drawable.gold_finish)
            else -> itemView.collection_quantity_finish_image.visibility = View.GONE
        }

        val quantityEditText = itemView.findViewById<TextInputEditText>(R.id.collection_quantity_edit_text)
        val decreaseCardQuantityButton = itemView.findViewById<ImageView>(R.id.collection_decrease_card_quantity_button)
        val increaseCardQuantityButton = itemView.findViewById<ImageView>(R.id.collection_increase_card_quantity_button)

        val finishIndex = printing.finishList.indexOfFirst { it == finish }
        val collectionEntry = currentSetCollectionEntryMap[printing.id]
        collectionEntry?.let {
            val quantity = if (finishIndex > it.quantityList.size) 0 else it.quantityList[finishIndex]
            quantityEditText.setText(quantity.toString())
        }

        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("CollectionQuantityRecyclerViewHolder", "beforeTextChanged quantityEditText")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("CollectionQuantityRecyclerViewHolder", "onTextChanged quantityEditText")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("CollectionQuantityRecyclerViewHolder", "afterTextChanged quantityEditText")
                if (quantityEditText.hasFocus() && s.toString() != "") {
                    updateOrAddCollectionEntry.invoke(
                        Integer.parseInt(s.toString()),
                        printing,
                        finish,
                        context.resources
                    )
                }
            }
        })

        quantityEditText.onFocusChangeListener = View.OnFocusChangeListener { view: View, b: Boolean ->
            if (b) {
                if (quantityEditText.text.toString().length == quantityEditText.text.toString()
                        .count { it.toString() == "0" }
                )
                    quantityEditText.setText("")
                showKeyboard(view, context)
            } else if (!b) {
                if (quantityEditText.text.toString() == "" || quantityEditText.text.toString().length == quantityEditText.text.toString()
                        .count { it.toString() == "0" }
                ) {
                    quantityEditText.setText("0")
                    updateOrAddCollectionEntry.invoke(
                        Integer.parseInt(quantityEditText.text.toString()),
                        printing,
                        finish,
                        context.resources
                    )
                }
            }
        }

        decreaseCardQuantityButton.setOnClickListener {
            if (quantityEditText.isFocused) {
                quantityEditText.clearFocus()
                hideKeyboard(it, context)
            }
            val quantity = Integer.parseInt(quantityEditText.text.toString())
            if (quantity > 0) {
                quantityEditText.setText((quantity - 1).toString())
                updateOrAddCollectionEntry.invoke(
                    Integer.parseInt(quantityEditText.text.toString()),
                    printing,
                    finish,
                    context.resources
                )
            } else if (quantity < 0) {
                quantityEditText.setText("0")
                updateOrAddCollectionEntry.invoke(
                    Integer.parseInt(quantityEditText.text.toString()),
                    printing,
                    finish,
                    context.resources
                )
            }
        }

        increaseCardQuantityButton.setOnClickListener {
            if (quantityEditText.isFocused) {
                quantityEditText.clearFocus()
                hideKeyboard(it, context)
            }
            val quantity = Integer.parseInt(quantityEditText.text.toString())
            if (quantity in 0..99998) {
                quantityEditText.setText((quantity + 1).toString())
                updateOrAddCollectionEntry.invoke(
                    Integer.parseInt(quantityEditText.text.toString()),
                    printing,
                    finish,
                    context.resources
                )
            } else if (quantity < 0) {
                quantityEditText.setText("0")
                updateOrAddCollectionEntry.invoke(
                    Integer.parseInt(quantityEditText.text.toString()),
                    printing,
                    finish,
                    context.resources
                )
            }
        }

        KeyboardVisibilityEvent.setEventListener(
            context as Activity
        ) { isOpen ->
            if (!isOpen) quantityEditText.clearFocus()
        }
    }

    fun hideKeyboard(view: View?, context: Context) {
        if (view != null) {
            val imm: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(view: View?, context: Context) {
        if (view != null) {
            val imm: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}