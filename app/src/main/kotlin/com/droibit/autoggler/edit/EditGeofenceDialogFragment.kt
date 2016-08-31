package com.droibit.autoggler.edit

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.widget.Button
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Geofence

class EditGeofenceDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    companion object {

        @JvmStatic
        private val ARG_GEOFENCE = "ARG_GEOFENCE"

        @JvmStatic
        private val ARG_DELETABLE = "ARG_DELETABLE"

        @JvmStatic
        private val FRAGMENT_TAG = EditGeofenceDialogFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(srcGeofence: Geofence, deletable: Boolean = false): EditGeofenceDialogFragment {
            val fragment = EditGeofenceDialogFragment().apply {
                arguments = Bundle(2).apply {
                    putSerializable(ARG_GEOFENCE, srcGeofence)
                    putBoolean(ARG_DELETABLE, deletable)
                }
            }
            return fragment
        }
    }

    private lateinit var contentView: EditGeofenceContentView

    private lateinit var geofence: Geofence

    private lateinit var positiveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        geofence = if (savedInstanceState != null)
            savedInstanceState.getSerializable(ARG_GEOFENCE) as Geofence
        else
            arguments.getSerializable(ARG_GEOFENCE) as Geofence
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context).run {
            setTitle(R.string.edit_geofence_dialog_title)
            setPositiveButton(R.string.update, this@EditGeofenceDialogFragment)
            setNegativeButton(android.R.string.cancel, this@EditGeofenceDialogFragment)

            contentView = EditGeofenceContentView(context).apply { init(geofence) }
            setView(contentView)

            if (arguments.getBoolean(ARG_DELETABLE, false)) {
                setNeutralButton(R.string.delete, this@EditGeofenceDialogFragment)
            }
            create()
        }

        return dialog
    }

    override fun onResume() {
        super.onResume()

        positiveButton = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // TODO:
        outState.putSerializable(ARG_GEOFENCE, geofence)
    }

    fun show(manager: FragmentManager) {
        show(manager, FRAGMENT_TAG)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

    }
}