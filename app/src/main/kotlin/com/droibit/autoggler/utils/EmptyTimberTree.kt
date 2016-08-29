package com.droibit.autoggler.utils

import timber.log.Timber

object EmptyTimberTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
    }
}