package com.droibit.autoggler.edit

import android.content.Context
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.droibit.autoggler.R

class DragActionMode(private val context: Context, private val callback: Callback) : ActionMode.Callback {

    interface Callback {

        fun onPrepareMarkerMove()

        fun onMarkerMoved()
    }

    var isShown: Boolean = false

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        isShown = true
        callback.onPrepareMarkerMove()
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        mode.title = context.getString(R.string.edit_geofence_move_action_mode_title)
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        isShown = false
        callback.onMarkerMoved()
    }
}