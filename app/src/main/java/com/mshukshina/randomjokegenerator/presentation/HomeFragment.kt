package com.mshukshina.randomjokegenerator.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mshukshina.randomjokegenerator.JokeGenerator
import com.mshukshina.randomjokegenerator.data.Joke
import com.mshukshina.randomjokegenerator.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private val jokeGenerator = JokeGenerator()

    private lateinit var binding: FragmentHomeBinding

    private var joke: Joke? = null
    private var jokeId: Long? = null

    private val viewModel: MyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.generatedJoke != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                joke = Joke(viewModel.insertedJokeId ?: 0, viewModel.generatedJoke!!)
                setUpHomeScreen(joke!!)
            }
        }

        binding.generateJokeButton.setOnClickListener {
            val question = binding.spinner.selectedItem.toString()
            generateJoke(question)
        }

        binding.generateCustomJokeButton.setOnClickListener {
            val question = binding.searchView.query.toString()
            generateJoke(question)
        }
    }

    private fun generateJoke(question: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            jokeGenerator.callAPI(question)
        }
        requireActivity().runOnUiThread {
            binding.progressIndicator.visibility = View.VISIBLE
            binding.cosmicCatImageView.visibility = View.GONE
            binding.jokeScrollView.visibility = View.GONE
            binding.favoriteUncheckedImageView.visibility = View.GONE
            binding.favoriteImageView.visibility = View.GONE
            binding.shareImageView.visibility = View.GONE
        }
        jokeGenerator.callBack = { result ->
            requireActivity().runOnUiThread {
                if (result != null) {

                    viewModel.setGeneratedJoke(result)

                    binding.progressIndicator.visibility = View.GONE
                    binding.cosmicCatImageView.visibility = View.GONE
                    binding.jokeTextView.text = result
                    binding.jokeScrollView.visibility = View.VISIBLE
                    binding.favoriteUncheckedImageView.visibility = View.VISIBLE
                    binding.shareImageView.visibility = View.VISIBLE

                    binding.favoriteUncheckedImageView.setOnClickListener {
                        joke = Joke(0, result)
                        insertJoke(joke!!)
                    }
                    binding.favoriteImageView.setOnClickListener {
                        deleteJoke(jokeId)
                    }
                    binding.shareImageView.setOnClickListener {
                        joke = Joke(0, result)
                        shareJoke(joke!!)
                    }

                } else {
                    Toast.makeText(requireContext(), "Failed to get response", Toast.LENGTH_SHORT)
                        .show()
                    binding.cosmicCatImageView.visibility = View.VISIBLE
                    binding.jokeScrollView.visibility = View.GONE
                    binding.progressIndicator.visibility = View.GONE
                    binding.favoriteUncheckedImageView.visibility = View.GONE
                    binding.favoriteImageView.visibility = View.GONE
                    binding.shareImageView.visibility = View.GONE
                }
            }
        }
    }

    private fun deleteJoke(jokeId: Long?) {
        binding.favoriteImageView.visibility = View.GONE
        binding.favoriteUncheckedImageView.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            if (jokeId != null) {
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

    private fun insertJoke(joke: Joke) {
        binding.favoriteUncheckedImageView.visibility = View.GONE
        binding.favoriteImageView.visibility = View.VISIBLE


        lifecycleScope.launch(Dispatchers.IO) {
            jokeId = async {
                viewModel.insertJoke(joke).join()
                viewModel.insertedJokeId
            }.await()

            Log.i("JokeAsync", jokeId.toString())

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Joke saved",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun shareJoke(joke: Joke) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, joke.joke)
        requireContext().startActivity(Intent.createChooser(shareIntent, "Share Joke"))
    }

    private fun setUpHomeScreen(joke: Joke) {

        binding.jokeTextView.text = joke.joke
        binding.jokeScrollView.visibility = View.VISIBLE
        binding.shareImageView.visibility = View.VISIBLE
        binding.cosmicCatImageView.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.Main) {

            viewModel.getAllJokes.observe(viewLifecycleOwner) { jokes ->
                val existingIds = jokes?.map { it.id }
                val isJokeIdExistent = existingIds?.contains(jokeId) == true
                viewModel.setIsJokeIdInRange(isJokeIdExistent)
            }

            viewModel.isJokeIdInRange.observe(viewLifecycleOwner) { jokeIdExist ->
                when (jokeIdExist) {
                    true -> {
                        binding.favoriteUncheckedImageView.visibility = View.GONE
                        binding.favoriteImageView.visibility = View.VISIBLE
                    }

                    false -> {
                        binding.favoriteUncheckedImageView.visibility = View.VISIBLE
                        binding.favoriteImageView.visibility = View.GONE
                    }
                    null -> {
                        binding.favoriteUncheckedImageView.visibility = View.GONE
                        binding.favoriteImageView.visibility = View.GONE
                    }
                }

                binding.favoriteUncheckedImageView.setOnClickListener {

                    lifecycleScope.launch(Dispatchers.IO) {
                        if (jokeIdExist == false) {
                            launch(Dispatchers.Main) {
                                insertJoke(joke)
                            }
                        }
                    }
                }

                binding.favoriteImageView.setOnClickListener {
                    lifecycleScope.launch {
                        if (jokeIdExist == true) {
                            deleteJoke(joke.id)
                        }
                    }
                }
            }
        }
        binding.shareImageView.setOnClickListener {
            shareJoke(joke)
        }
    }

}