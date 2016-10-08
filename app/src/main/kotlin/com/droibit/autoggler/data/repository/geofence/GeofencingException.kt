package com.droibit.autoggler.data.repository.geofence


class GeofencingException(val status: ErrorStatus) : Exception("Error: $status") {

    enum class ErrorStatus {
        PERMISSION_DENIED,
        FAILED_ADD,
        FAILED_REMOVE,
    }
}