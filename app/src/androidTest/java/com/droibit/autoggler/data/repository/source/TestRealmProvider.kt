package com.droibit.autoggler.data.repository.source

import com.droibit.autoggler.data.repository.source.db.RealmProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.Closeable

class TestRealmProvider : RealmProvider, Closeable {

    private val config: RealmConfiguration

    private val realm: Realm

    constructor() {
        config = RealmConfiguration.Builder()
                .name("test.realm")
                .build()
        realm = Realm.getInstance(config)
    }

    override fun <T> use(block: (Realm) -> T): T {
        return block(realm)
    }

    override fun close() {
        realm.close()
        Realm.deleteRealm(config)
    }
}