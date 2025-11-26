package com.and_rey.mvi_skeleton_example

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.and_rey.mvi_skeleton_example.databinding.FragmentBookDetailsBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [BookDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BookDetailsFragment : Fragment() {
    private lateinit var _binding: FragmentBookDetailsBinding
    val binding get() = _binding

    private val viewModel: BookDetailsViewModel by viewModels()

    private val args: BookDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailsBinding.inflate(layoutInflater)
        viewModel.userIntent.trySend(BookDetailsViewModel.Wish.LoadBookDetails(args.bookId))

        with(binding) {
            //before view created: setup your initial animation and for example text with data from args
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            //view is created! setup your click-listeners, RV's adapters etc here
        }

        //here is the magic -
        //you are listening for StateFlow variable 'state' from vm
        //state reflects current state(thanks, Cap) of model
        viewModel.state
            .onEach {
                setState(it)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}