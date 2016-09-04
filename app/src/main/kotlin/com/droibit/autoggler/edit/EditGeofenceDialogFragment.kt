package com.droibit.autoggler.edit

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.droibit.autoggler.R
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.github.droibit.chopstick.bindView
import com.linearlistview.LinearListView

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


    internal class EditGeofenceContentView : LinearLayout {

        private val geofenceNameView: EditText by bindView(R.id.geofence_name)

        private val geofenceRadiusView: Spinner by bindView(R.id.geofence_radius)

        private val toggleListView: LinearListView by bindView(R.id.toggle_list)

        @JvmOverloads
        constructor(context: Context,
                    attrs: AttributeSet? = null,
                    defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
            View.inflate(context, R.layout.view_edit_geofence, this)

            geofenceRadiusView.adapter = ArrayAdapter.createFromResource(
                    context, R.array.edit_geofence_circle_radius_labels, R.layout.list_item_geofence_radius)
        }

        fun init(srcGeofence: Geofence) {
            toggleListView.adapter = ToggleAdapter(context, srcGeofence)
        }

        inline fun onTextChanged(crossinline callback: (CharSequence?) -> Unit) {
            geofenceNameView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    callback(s)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }

        fun hasGeofenceName() = !geofenceNameView.text.isNullOrEmpty()
    }

    private val positiveButton: Button by lazy { (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE) }

    private lateinit var contentView: EditGeofenceContentView

    private lateinit var geofence: Geofence


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        geofence = if (savedInstanceState != null)
            savedInstanceState.getSerializable(ARG_GEOFENCE) as Geofence
        else
            arguments.getSerializable(ARG_GEOFENCE) as Geofence
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context).run {
            val self = this@EditGeofenceDialogFragment
            setTitle(R.string.edit_geofence_dialog_title)
            setPositiveButton(R.string.update, self)
            setNegativeButton(android.R.string.cancel, self)

            contentView = EditGeofenceContentView(context).apply {
                init(geofence)
                onTextChanged { positiveButton.isEnabled = !it.isNullOrEmpty() }
            }
            setView(contentView)

            if (arguments.getBoolean(ARG_DELETABLE, false)) {
                setNeutralButton(R.string.delete, self)
            }
            create()
        }

        return dialog
    }

    override fun onResume() {
        super.onResume()

        positiveButton.isEnabled = contentView.hasGeofenceName()
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