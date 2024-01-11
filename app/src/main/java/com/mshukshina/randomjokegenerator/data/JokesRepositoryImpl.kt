package com.mshukshina.randomjokegenerator.data

import androidx.lifecycle.LiveData
import com.mshukshina.randomjokegenerator.domain.JokesRepository

class JokesRepositoryImpl(private val dao: JokesDao) : JokesRepository {
    override fun getAllJokes(): LiveData<List<Joke>> {
        return dao.getAllJokes()
    }

    override suspend fun deleteJokeById(jokeId: Long) {
        dao.deleteJokeById(jokeId)
    }

    override suspend fun insertJoke(joke: Joke): Long {
        return dao.insertJoke(joke)
    }

}
