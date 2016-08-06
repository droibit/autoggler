package com.droibit.autoggler.data.repository.source

import io.realm.Realm
import io.realm.RealmModel

inline fun <reified T : RealmModel> Realm.where() = where(T::class.java)

inline fun <reified T : RealmModel> Realm.createObject() = createObject(T::class.java)

inline fun <reified T : RealmModel> Realm.delete() = delete(T::class.java)

inline fun <reified T : RealmModel> Realm.useTransaction(execute: ()->T): T {
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