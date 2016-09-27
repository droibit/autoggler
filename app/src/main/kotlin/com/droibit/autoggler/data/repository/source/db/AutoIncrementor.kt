package com.droibit.autoggler.data.repository.source.db

import io.realm.Realm
import io.realm.RealmModel

class AutoIncrementor(val columnName: String) {

    inline fun <reified T : RealmModel> newId(realm: Realm): Long {
        return realm.where(T::class.java).max(columnName)?.let { it.toLong() + 1L } ?: 1L
    }
}
