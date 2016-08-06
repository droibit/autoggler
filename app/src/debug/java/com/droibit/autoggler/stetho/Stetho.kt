package com.droibit.autoggler.stetho

import android.content.Context
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import java.util.regex.Pattern
import com.facebook.stetho.Stetho as RawStetho

class Stetho(private val context: Context) {

    fun initialize() {
        val initializer = buildInitializer()
        RawStetho.initialize(initializer)
    }

    private fun buildInitializer(): RawStetho.Initializer {
        val inspector = RealmInspectorModulesProvider.builder(context)
                .databaseNamePattern(Pattern.compile(".+\\.realm"))
                .build()
        return RawStetho.newInitializerBuilder(context)
                .enableWebKitInspector(inspector)
                .build()
    }
}
