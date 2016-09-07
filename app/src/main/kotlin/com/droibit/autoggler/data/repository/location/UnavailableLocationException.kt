package com.droibit.autoggler.data.repository.location

class UnavailableLocationException(
        val status: ErrorStatus,
        val option: AvailableStatus? = null) : Exception() {

    enum class ErrorStatus {
        PERMISSION_DENIED,
        RESOLUTION_REQUIRED,
        ERROR,
    }
}