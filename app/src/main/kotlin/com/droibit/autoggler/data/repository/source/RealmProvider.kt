package com.droibit.autoggler.data.repository.source

import io.realm.Realm
import io.realm.RealmConfiguration

class RealmProvider(val config: RealmConfiguration) {

    companion object {
        const val FILE_NAME = "geofences.realm"
    }

    fun getRealm(): Realm = Realm.getInstance(config)
}