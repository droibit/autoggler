package com.droibit.autoggler

import android.app.Application
import com.droibit.autoggler.data.repository.repositoryModule
import com.droibit.autoggler.stetho.Stetho
import com.droibit.autoggler.stetho.stethoModule
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import timber.log.Timber

class AutogglerApplication : Application(), KodeinAware {

    override val kodein: Kodein by Kodein.lazy {
        import(applicationModule(this@AutogglerApplication, BuildConfig.DEBUG))
        import(repositoryModule())
        import(stethoModule())
    }

    private val injector = KodeinInjector()

    private val timberTree: Timber.Tree by injector.instance()

    private val stetho: Stetho by injector.instance()

    override fun onCreate() {
        super.onCreate()

        injector.inject(Kodein {
            extend(appKodein())
        })

        Timber.plant(timberTree)
        stetho.initialize()
    }
}