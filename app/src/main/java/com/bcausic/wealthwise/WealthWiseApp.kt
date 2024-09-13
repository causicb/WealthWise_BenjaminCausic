package com.bcausic.wealthwise

import android.app.Application
import com.bcausic.wealthwise.di.repositoryModule
import com.bcausic.wealthwise.di.roomDatabaseModule
import com.bcausic.wealthwise.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WealthWiseApp : Application() {

    companion object {
        lateinit var application : WealthWiseApp
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        startKoin {
            androidContext(this@WealthWiseApp)
            modules(listOf(
                roomDatabaseModule,
                repositoryModule,
                viewModelModule
            ))
        }
    }

}