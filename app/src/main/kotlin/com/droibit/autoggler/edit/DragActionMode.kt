package com.droibit.autoggler.edit

import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.droibit.autoggler.R

class DragActionMode(private val callback: Callback) : ActionMode.Callback {

    interface Callback {

        fun onPrepareMarkerMove()

        fun onMarkerMoved()
    }

    var isShown: Boolean = false

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        isShown = true
        mode.setTitle(R.string.edit_geofence_move_action_mode_title)
        callback.onPrepareMarkerMove()
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
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