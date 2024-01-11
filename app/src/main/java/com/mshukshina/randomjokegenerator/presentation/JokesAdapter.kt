package com.mshukshina.randomjokegenerator.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mshukshina.randomjokegenerator.data.Joke
import com.mshukshina.randomjokegenerator.databinding.JokeItemBinding

class JokesAdapter : RecyclerView.Adapter<JokesAdapter.JokesViewHolder>() {

    private var jokesList = emptyList<Joke>()

    var onItemDeleteClickListener: ((Long) -> Unit) = { }

    fun setData(list: List<Joke>) {
        jokesList = list
        notifyDataSetChanged()
    }

    class JokesViewHolder(binding: JokeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val jokeTextView = binding.jokeTV
        val deleteIv = binding.deleteIV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokesViewHolder {
        val binding = JokeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JokesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return jokesList.size
    }

    override fun onBindViewHolder(holder: JokesViewHolder, position: Int) {
        val joke = jokesList[position]
        holder.jokeTextView.text = joke.joke
        holder.deleteIv.setOnClickListener {
            onItemDeleteClickListener(joke.id)
        }
    }
}