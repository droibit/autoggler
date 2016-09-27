package com.droibit.autoggler.data.repository.source.db

import io.realm.Realm
import io.realm.RealmConfiguration

class RealmProviderImpl(private val config: RealmConfiguration) : RealmProvider {

    override fun <T> use(block: (Realm) -> T): T = Realm.getInstance(config).use(block)
}