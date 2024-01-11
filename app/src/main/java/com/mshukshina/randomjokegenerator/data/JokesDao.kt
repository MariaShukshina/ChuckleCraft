package com.mshukshina.randomjokegenerator.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JokesDao {
    @Query("SELECT * FROM jokes")
    fun getAllJokes(): LiveData<List<Joke>>

    @Query("DELETE FROM jokes WHERE id = :jokeId")
    suspend fun deleteJokeById(jokeId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoke(joke: Joke): Long
}
