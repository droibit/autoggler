package com.droibit.autoggler.data.repository.source

import io.realm.Realm
import io.realm.RealmConfiguration

class RealmProvider(private val config: RealmConfiguration) {

    companion object {
        const val FILE_NAME = "autoggler.realm"
    }

    fun get(): Realm = Realm.getInstance(config)
}