package com.droibit.autoggler.data.repository.source

import io.realm.Realm
import io.realm.RealmModel

inline fun <reified T: RealmModel> Realm.where() = where(T::class.java)