package kcsit.pt.bookstore.presentation.book_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kcsit.pt.bookstore.R
import kcsit.pt.bookstore.databinding.FragmentBookDetailsBinding
import kcsit.pt.bookstore.domain.model.Book
import kcsit.pt.bookstore.util.Extensions.isNetworkAvailable
import kcsit.pt.bookstore.util.Extensions.makeToast
import kcsit.pt.bookstore.util.Extensions.toVerticalString
import kcsit.pt.bookstore.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookDetailsFragment : Fragment(R.layout.fragment_book_details) {
    private lateinit var bookDetailsBinding: FragmentBookDetailsBinding
    private val bookDetailsViewModel: BookDetailsViewModel by viewModels()
    private val bookIdArgs: BookDetailsFragmentArgs by navArgs()
    private lateinit var bookId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookDetailsBinding = FragmentBookDetailsBinding.bind(view)

        getArgs()
        getBookById()
        collectObservables()
    }

    private fun bindBookDetails(book: Book?) {
        book?.let {
            bookDetailsBinding.apply {
                imgBookImage.load(it.volumeInfo.imageLinks.thumbnail
                    .replace("http://", "https://"))
                txTitle.text = it.volumeInfo.title
                if (it.volumeInfo.description.isEmpty()) {
                    txDescription.text = "Description not available"
                    txDescription.gravity = Gravity.CENTER
                } else txDescription.text = it.volumeInfo.description
                txAuthors.text = it.volumeInfo.authors.toVerticalString()

                val bookCurrencyCode = it.saleInfo.listPrice.currencyCode
                val bookAmount = it.saleInfo.listPrice.amount
                val bookBuyLink = it.saleInfo.buyLink

                if (bookCurrencyCode.isEmpty()
                    || bookAmount <= 0.0
                    || bookBuyLink.isEmpty()
                ) {
                    btnBuyNow.apply {
                        text = "Book Unavailable"
                        alpha = 0.5f
                        isClickable = false
                    }
                } else {
                    btnBuyNow.apply {
                        setOnClickListener {
                            bindBuyNowClick(bookBuyLink)
                        }
                        text = "Buy Now: $bookCurrencyCode $bookAmount"
                        alpha = 1f
                        isClickable = true
                    }
                }
            }
        }
    }

    private fun bindBuyNowClick(bookUrl: String) {
        val defaultBrowser = Intent(Intent.ACTION_VIEW)
        defaultBrowser.data = Uri.parse(bookUrl.replace("http://", "https://"))
        startActivity(defaultBrowser)
    }

    private fun getBookById() {
        bookDetailsViewModel.onEvent(BookDetailsEvent.GetBookById(
            hasInternetConnection = requireContext().isNetworkAvailable(),
            bookId = bookId))
    }

    private fun getArgs() {
        bookId = bookIdArgs.bookId
    }

    private fun collectObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookDetailsViewModel.bookDetailState
                    .collectLatest { bookState ->
                        when (bookState) {
                            is Resource.Error -> {
                                requireContext().makeToast(bookState.message ?: "An unexpected error has occurred.")
                                bookDetailsBinding.pbLoadingImage.visibility = GONE
                            }
                            is Resource.Loading -> bookDetailsBinding.pbLoadingImage.visibility = VISIBLE
                            is Resource.Success -> {
                                bookDetailsBinding.pbLoadingImage.visibility = GONE
                                bindBookDetails(bookState.data)
                            }
                        }
                    }
            }
        }
    }
}