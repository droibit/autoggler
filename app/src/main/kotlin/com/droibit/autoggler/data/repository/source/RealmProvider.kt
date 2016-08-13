package com.droibit.autoggler.data.repository.source

import io.realm.Realm


interface RealmProvider {

    companion object {
        const val FILE_NAME = "autoggler.realm"
    }

    fun <T> use(block: (Realm)->T): T
}