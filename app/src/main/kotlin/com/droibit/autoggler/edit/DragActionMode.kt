package com.droibit.autoggler.edit

import android.app.Activity
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.droibit.autoggler.R
import timber.log.Timber

class DragActionMode(
        private val activity: Activity,
        private val callback: Callback) : ActionMode.Callback {

    interface Callback {

        fun onPrepareDragMode()

        fun onFinishedDragMode()
    }

    var isShown: Boolean = false

    @ColorInt
    private var statusBarColor = activity.window.statusBarColor

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        Timber.d("onPrepareActionMode")

        isShown = true
        mode.setTitle(R.string.edit_geofence_move_action_mode_title)
        activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.colorGrey_800)

        callback.onPrepareDragMode()
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        Timber.d("onDestroyActionMode")

        isShown = false
        activity.window.statusBarColor = statusBarColor

        callback.onFinishedDragMode()
    }
}