package com.phi.tenatanweave.fragments.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.phi.tenatanweave.R
import com.phi.tenatanweave.databinding.FragmentNewDeckDialogBinding
import com.phi.tenatanweave.fragments.decks.DeckViewModel

class NewDeckDialogFragment : DialogFragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rootView: View = View.inflate(activity, R.layout.fragment_new_deck_dialog, null)

        DataBindingUtil.bind<FragmentNewDeckDialogBinding>(rootView)?.apply {
            this.lifecycleOwner = lifecycleOwner
            this.viewmodel = deckViewModel
        }

        val deckNameEditText = rootView.findViewById<TextInputEditText>(R.id.deck_name_edit_text)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.title_new_deck))
            .setView(rootView)
            .setPositiveButton(getString(R.string.label_ok)) { _, _ ->
                deckViewModel.formatSelection.value?.let { deckViewModel.formatList.value?.get(it).toString() }?.let {
                    deckViewModel.addDeck(
                        deckNameEditText.text.toString(),
                        it,
                        resources
                    )
                }
            }
            .setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        deckNameEditText.doAfterTextChanged {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !it.isNullOrEmpty()
        }

        return dialog
    }

    companion object {
        const val TAG = "NewDeckDialogFragment"
    }
}