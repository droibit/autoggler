package com.droibit.autoggler

import android.app.Application
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import timber.log.Timber

class AutogglerApplication : Application(), KodeinAware {

    override val kodein: Kodein by Kodein.lazy {
        import(applicationModule(this@AutogglerApplication, BuildConfig.DEBUG))
    }

    private val injector = KodeinInjector()

    private val timberTree: Timber.Tree by injector.instance()

    override fun onCreate() {
        super.onCreate()

        injector.inject(Kodein {
            extend(appKodein())
        })

        Timber.plant(timberTree)
    }
}