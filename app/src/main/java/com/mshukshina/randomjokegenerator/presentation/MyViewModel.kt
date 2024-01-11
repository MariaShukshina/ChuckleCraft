package com.mshukshina.randomjokegenerator.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshukshina.randomjokegenerator.data.Joke
import com.mshukshina.randomjokegenerator.domain.JokesRepository
import kotlinx.coroutines.launch

class MyViewModel(private val repository: JokesRepository) : ViewModel() {

    var insertedJokeId: Long? = null
        private set

    var generatedJoke: String? = null
        private set

    private val _isJokeIdInRange = MutableLiveData<Boolean>(null)

    val isJokeIdInRange: LiveData<Boolean>
        get() = _isJokeIdInRange

    fun setGeneratedJoke(joke: String?) {
        generatedJoke = joke
    }

    fun setIsJokeIdInRange(isInRange: Boolean) {
        _isJokeIdInRange.value = isInRange
    }

    val getAllJokes = repository.getAllJokes()

    fun deleteJoke(id: Long) = viewModelScope.launch {
        repository.deleteJokeById(id)
        setIsJokeIdInRange(false)
    }

    fun insertJoke(joke: Joke) = viewModelScope.launch {
        insertedJokeId = repository.insertJoke(joke)
    }

}
