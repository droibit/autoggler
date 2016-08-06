package com.droibit.autoggler.data.repository.source

import io.realm.Realm
import io.realm.RealmConfiguration

class RealmProvider(config: RealmConfiguration) {

    companion object {
        const val FILE_NAME = "autoggler.realm"
    }

    private val realm = Realm.getInstance(config)

    fun get(): Realm = realm
}