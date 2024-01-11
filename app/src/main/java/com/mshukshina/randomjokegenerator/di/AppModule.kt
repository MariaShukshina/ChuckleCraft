package com.mshukshina.randomjokegenerator.di

import androidx.room.Room
import com.mshukshina.randomjokegenerator.presentation.MyViewModel
import com.mshukshina.randomjokegenerator.data.JokesDatabase
import com.mshukshina.randomjokegenerator.data.JokesRepositoryImpl
import com.mshukshina.randomjokegenerator.domain.JokesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { MyViewModel(get()) }


    single<JokesRepository> {
        JokesRepositoryImpl(get())
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            JokesDatabase::class.java,
            JokesDatabase.DATABASE_NAME
        ).build()
    }

    single { get<JokesDatabase>().getJokesDao() }


}