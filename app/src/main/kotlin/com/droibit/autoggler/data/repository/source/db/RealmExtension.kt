package com.droibit.autoggler.data.repository.source.db

import io.realm.Realm
import io.realm.RealmModel

inline fun <reified T : RealmModel> Realm.where() = where(T::class.java)

inline fun <reified T : RealmModel> Realm.createObject(primaryKey: Long) = createObject(T::class.java, primaryKey)

inline fun <reified T : RealmModel> Realm.delete() = delete(T::class.java)

inline fun <reified T : RealmModel> Realm.runTransaction(execute: () -> T): T {
    beginTransaction()
    try {
        val managedObject = execute()
        commitTransaction()
        return managedObject
    } catch (e: Throwable) {
        if (isInTransaction) {
            cancelTransaction()
        }
        throw e
    }
}