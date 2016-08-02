package com.droibit.autoggler.data.repository.source

import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.Closeable

class RealmProvider(config: RealmConfiguration) : Closeable {

    companion object {
        const val FILE_NAME = "autoggler.realm"
    }

    val realm = Realm.getInstance(config)

    fun get(): Realm = realm

    override fun close() = realm.close()
}