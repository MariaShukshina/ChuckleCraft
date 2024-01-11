package com.mshukshina.randomjokegenerator.app

import android.app.Application
import com.mshukshina.randomjokegenerator.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RandomJokeGeneratorApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RandomJokeGeneratorApplication)
            modules(appModule)
        }
    }
}