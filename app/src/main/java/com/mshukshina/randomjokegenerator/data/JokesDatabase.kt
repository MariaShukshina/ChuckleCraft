package com.mshukshina.randomjokegenerator.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Joke::class], version = 1)
abstract class JokesDatabase: RoomDatabase() {

    abstract fun getJokesDao(): JokesDao
    companion object {
        const val DATABASE_NAME = "Jokes.db"
    }
}