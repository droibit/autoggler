package com.droibit.autoggler.edit.add

class AddGeofencePresenter(
        private val view: AddGeofenceContract.View,
        private val navigator: AddGeofenceContract.Navigator) : AddGeofenceContract.Presenter {

    override fun onUpNavigationButtonClicked() = navigator.navigationToUp()
}
