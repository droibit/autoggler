package com.droibit.autoggler

import android.app.Application
import com.droibit.autoggler.data.checker.checkerModule
import com.droibit.autoggler.data.provider.providerModule
import com.droibit.autoggler.data.repository.repositoryModule
import com.droibit.autoggler.stetho.Stetho
import com.droibit.autoggler.stetho.stethoModule
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.appKodein
import timber.log.Timber

class AutogglerApplication : Application(), KodeinAware {

    override val kodein: Kodein by Kodein.lazy {
        val context = this@AutogglerApplication
        import(applicationModule(context, BuildConfig.DEBUG))
        import(repositoryModule())
        import(checkerModule())
        import(providerModule())
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