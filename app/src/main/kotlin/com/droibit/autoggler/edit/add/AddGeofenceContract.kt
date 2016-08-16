package com.droibit.autoggler.edit.add


interface AddGeofenceContract {

    interface View {

    }

    interface Navigator {

        fun navigationToUp()
    }

    interface Presenter {

        fun onUpNavigationButtonClicked()
    }
}
