package com.droibit.autoggler.stetho

import android.content.Context
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import com.facebook.stetho.Stetho as StethoOrigin

class Stetho(private val context: Context) {

    fun initialize() {
        val initializer = buildInitializer()
        StethoOrigin.initialize(initializer)
    }

    private fun buildInitializer(): StethoOrigin.Initializer {
        val inspector = RealmInspectorModulesProvider.builder(context)
                .build()
        return StethoOrigin.newInitializerBuilder(context)
                .enableDumpapp(StethoOrigin.defaultDumperPluginsProvider(context))
                .enableWebKitInspector(inspector)
                .build()
    }
}
