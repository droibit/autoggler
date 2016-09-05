package com.droibit.autoggler.edit

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
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
import com.droibit.autoggler.data.provider.rx.RxBus
import com.droibit.autoggler.data.repository.geofence.Geofence
import com.droibit.autoggler.edit.EditGeofenceContract.EditGeofenceEvent
import com.droibit.autoggler.edit.EditGeofenceContract.ToggleItem.Companion.INDEX_VIBRATION
import com.droibit.autoggler.edit.EditGeofenceContract.ToggleItem.Companion.INDEX_WIFI
import com.github.droibit.chopstick.bindIntArray
import com.github.droibit.chopstick.bindView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import com.linearlistview.LinearListView
import timber.log.Timber

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
            return EditGeofenceDialogFragment().apply {
                arguments = Bundle(2).apply {
                    putSerializable(ARG_GEOFENCE, srcGeofence)
                    putBoolean(ARG_DELETABLE, deletable)
                }
            }
        }
    }

    internal class EditGeofenceContentView : LinearLayout {

        val geofenceName: String
            get() = "${geofenceNameView.text}"

        val geofenceRadius: Double
            get() = geofenceRadiusList[geofenceRadiusView.selectedItemPosition].toDouble()

        val shouldToggleWifi: Boolean
            get() = toggleListAdapter.shouldToggle(INDEX_WIFI)

        val shouldToggleVibration: Boolean
            get() = toggleListAdapter.shouldToggle(INDEX_VIBRATION)

        val hasGeofenceName: Boolean
            get() = !geofenceNameView.text.isNullOrEmpty()

        private val geofenceNameView: EditText by bindView(R.id.geofence_name)

        private val geofenceRadiusView: Spinner by bindView(R.id.geofence_radius)

        private val toggleListView: LinearListView by bindView(R.id.toggle_list)

        private val geofenceRadiusList: IntArray by bindIntArray(R.array.edit_geofence_circle_radius_items)

        private val toggleListAdapter: ToggleAdapter
            get() = toggleListView.adapter as ToggleAdapter

        @JvmOverloads
        constructor(context: Context,
                    attrs: AttributeSet? = null,
                    defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
            View.inflate(context, R.layout.view_edit_geofence, this)

            geofenceRadiusView.adapter = ArrayAdapter.createFromResource(
                    context,
                    R.array.edit_geofence_circle_radius_labels,
                    R.layout.list_item_geofence_radius
            )
        }

        fun init(srcGeofence: Geofence) {
            geofenceNameView.setText(srcGeofence.name)
            toggleListView.adapter = ToggleAdapter(context, srcGeofence)

            val radiusIndex = geofenceRadiusList.indexOfFirst { it == srcGeofence.radius.toInt() }
            geofenceRadiusView.setSelection(if (radiusIndex != -1) radiusIndex else 0)
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
    }

    private val injector = KodeinInjector()

    private val rxBus: RxBus by injector.instance()

    private val positiveButton: Button by lazy { (dialog as AlertDialog).getButton(BUTTON_POSITIVE) }

    private lateinit var contentView: EditGeofenceContentView

    private lateinit var geofence: Geofence

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        geofence = if (savedInstanceState != null)
            savedInstanceState.getSerializable(ARG_GEOFENCE) as Geofence
        else
            arguments.getSerializable(ARG_GEOFENCE) as Geofence
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        injector.inject(Kodein {
            val parentKodein = context as? KodeinAware
                    ?: throw IllegalStateException("KodeinAware is not implemented.")
            extend(parentKodein.kodein)
        })
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

        positiveButton.isEnabled = contentView.hasGeofenceName
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        updateGeofence().apply { outState.putSerializable(ARG_GEOFENCE, this) }
    }

    fun show(manager: FragmentManager) {
        show(manager, FRAGMENT_TAG)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            BUTTON_POSITIVE -> updateGeofence().run { rxBus.call(EditGeofenceEvent.OnUpdate(geofence = this)) }
        }
    }

    private fun updateGeofence(): Geofence {
        return geofence.apply {
            name = contentView.geofenceName
            circle.radius = contentView.geofenceRadius
            toggle.wifi = contentView.shouldToggleWifi
            toggle.vibration = contentView.shouldToggleVibration

            Timber.d("Updated: $this")
        }
    }
}