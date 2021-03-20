package com.phi.tenatanweave.fragments.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.phi.tenatanweave.R
import com.phi.tenatanweave.fragments.decks.DeckViewModel

class DeleteConfirmationDialogFragment : DialogFragment() {

    private val deckViewModel: DeckViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val deckIndex = this.arguments?.getInt(getString(R.string.deck_index), -1)
        val deck = if (deckIndex!! > 0) deckIndex.let { deckViewModel.userDeckList.value?.get(it) } else null

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.title_delete))
            .setMessage(getString(R.string.message_delete, deck?.deckName))
            .setPositiveButton(getString(R.string.label_yes)) { _, _ ->
                if (deck != null)
                    deckViewModel.deleteDeck(deck, resources)
            }
            .setNegativeButton(getString(R.string.label_no)) { _, _ -> }

        val dialog: AlertDialog = builder.create()
        dialog.show()

        return dialog
    }

    companion object {
        const val TAG = "DeleteConfirmationDialogFragment"

        @JvmStatic
        fun newInstance(bundle: Bundle): DeleteConfirmationDialogFragment {
            val fragment = DeleteConfirmationDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}