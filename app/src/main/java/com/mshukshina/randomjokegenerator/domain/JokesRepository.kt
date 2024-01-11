package com.mshukshina.randomjokegenerator.domain

import androidx.lifecycle.LiveData
import com.mshukshina.randomjokegenerator.data.Joke

interface JokesRepository {

    fun getAllJokes(): LiveData<List<Joke>>

    suspend fun deleteJokeById(jokeId: Long)

    suspend fun insertJoke(joke: Joke): Long
}
