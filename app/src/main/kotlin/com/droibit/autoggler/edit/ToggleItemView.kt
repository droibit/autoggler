package com.droibit.autoggler.edit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.droibit.autoggler.R
import com.github.droibit.chopstick.bindView

@SuppressWarnings("PrivateResource")
class ToggleItemView : RelativeLayout {

    val iconView: ImageView by bindView(R.id.icon)

    val textView: TextView by bindView(R.id.text)

    val switch: Switch by bindView(R.id.toggle)

    @JvmOverloads
    constructor(context: Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
    }

    inline fun bind(item: EditGeofenceContract.ToggleItem, crossinline checkedCallback:(Boolean)->Unit) {
        switch.isChecked = item.enabled
        switch.setOnCheckedChangeListener { v, isChecked -> checkedCallback(isChecked) }
        iconView.setImageResource(item.iconRes)
        textView.setText(item.textRes)
    }
}
