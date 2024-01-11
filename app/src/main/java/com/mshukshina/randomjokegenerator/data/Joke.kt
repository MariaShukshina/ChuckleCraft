package com.mshukshina.randomjokegenerator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jokes")
data class Joke(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val joke: String
)
