package kcsit.pt.bookstore.presentation.book_details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import kcsit.pt.bookstore.R
import kcsit.pt.bookstore.databinding.FragmentBookDetailsBinding

@AndroidEntryPoint
class BookDetailsFragment : Fragment(R.layout.fragment_book_details) {
    private lateinit var bookDetailsBinding: FragmentBookDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookDetailsBinding = FragmentBookDetailsBinding.bind(view)

    }
}