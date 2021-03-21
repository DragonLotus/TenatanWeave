package com.phi.tenatanweave.fragments.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.phi.tenatanweave.R
import com.phi.tenatanweave.databinding.FragmentDeckDetailDialogBinding
import com.phi.tenatanweave.fragments.decks.DeckViewModel

class DeckDetailsDialogFragment : DialogFragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rootView: View = View.inflate(activity, R.layout.fragment_deck_detail_dialog, null)

        DataBindingUtil.bind<FragmentDeckDetailDialogBinding>(rootView)?.apply {
            this.lifecycleOwner = lifecycleOwner
            this.viewmodel = deckViewModel
        }

        val deckNameEditText = rootView.findViewById<TextInputEditText>(R.id.deck_name_edit_text)
        val deckTypeSpinner = rootView.findViewById<Spinner>(R.id.deck_type_spinner)

        val deckIndex = this.arguments?.getInt(getString(R.string.deck_index), -1)
        val deck = if (deckIndex!! >= 0) deckIndex.let { deckViewModel.userDeckList.value?.get(it) } else null

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            .setView(rootView)
            .setPositiveButton(getString(R.string.label_ok)) { _, _ ->
                if (deck == null)
                    deckViewModel.formatSelection.value?.let { deckViewModel.formatList.value?.get(it) }?.let {
                        deckViewModel.addDeck(
                            deckNameEditText.text.toString(),
                            it,
                            resources
                        )
                    }
                else {
                    deckViewModel.formatSelection.value?.let { deckViewModel.formatList.value?.get(it) }?.let {
                        if(it != deck.format){
                            deck.format = it
                            deck.setNewLastModifiedDate()
                        }
                    }
                    deckNameEditText.text.toString().let {
                        if(it != deck.deckName){
                            deck.deckName = it
                            deck.setNewLastModifiedDate()
                        }
                    }
                    deckViewModel.updateOrAddDeck(deck, resources)
                }
            }
            .setNegativeButton(getString(R.string.label_cancel)) { _, _ ->
                this.arguments = null
            }

        if (deck == null)
            builder.setTitle(getString(R.string.title_new_deck))
        else
            builder.setTitle(getString(R.string.title_edit_deck))


        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        deckNameEditText.doAfterTextChanged {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !it.isNullOrEmpty()
        }

        deck?.let {
            val index = deckViewModel.formatList.value?.indexOfFirst { it == deck.format }
            deckViewModel.formatSelection.value = index
            deckNameEditText.setText(deck.deckName)
        }

        return dialog
    }

    companion object {
        const val TAG = "NewDeckDialogFragment"

        @JvmStatic
        fun newInstance(bundle: Bundle): DeckDetailsDialogFragment {
            val fragment = DeckDetailsDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}