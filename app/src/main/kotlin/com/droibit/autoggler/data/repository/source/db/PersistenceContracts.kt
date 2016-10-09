package com.droibit.autoggler.data.repository.source.db

import com.droibit.autoggler.data.repository.geofence.Geofence

object GeofencePersistenceContract {

    @JvmStatic
    val COLUMN_ID = Geofence::id.name

    @JvmStatic
    val CREATED_AT = Geofence::createdAt.name
}