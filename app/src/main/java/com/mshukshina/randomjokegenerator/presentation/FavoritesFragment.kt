package com.mshukshina.randomjokegenerator.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mshukshina.randomjokegenerator.databinding.FragmentFavoritesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: MyViewModel by viewModel()

    private lateinit var binding: FragmentFavoritesBinding

    private val jokesAdapter = JokesAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.jokeRv.adapter = jokesAdapter
        binding.jokeRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getAllJokes.observe(viewLifecycleOwner) { jokes ->
            jokesAdapter.setData(jokes.orEmpty())
        }

        jokesAdapter.onItemDeleteClickListener = { jokeId ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    viewModel.deleteJoke(jokeId)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Joke deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Error deleting joke",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

}