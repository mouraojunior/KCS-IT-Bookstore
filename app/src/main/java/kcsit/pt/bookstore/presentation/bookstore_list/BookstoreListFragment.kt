package kcsit.pt.bookstore.presentation.bookstore_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kcsit.pt.bookstore.R
import kcsit.pt.bookstore.databinding.FragmentBookstoreListBinding
import kcsit.pt.bookstore.presentation.adapters.BookstoreListAdapter
import kcsit.pt.bookstore.util.Extensions.isNetworkAvailable
import kcsit.pt.bookstore.util.Extensions.makeToast
import kcsit.pt.bookstore.util.Extensions.safeNavigate
import kcsit.pt.bookstore.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookstoreListFragment : Fragment(R.layout.fragment_bookstore_list) {
    private lateinit var bookstoreListBinding: FragmentBookstoreListBinding
    private val bookstoreListViewModel: BookstoreListViewModel by viewModels()
    private lateinit var bookstoreListAdapter: BookstoreListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookstoreListBinding = FragmentBookstoreListBinding.bind(view)

        createAdapterAndItemClick()
        getBooks()
        setBookstoreRecyclerView()
        collectObservables()
    }

    private fun getBooks() {
        bookstoreListViewModel.onEvent(BookListEvent.GetBooks(requireContext().isNetworkAvailable()))
    }

    private fun setBookstoreRecyclerView() {
        bookstoreListBinding.apply {
            rvBooks.apply {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = bookstoreListAdapter
            }
        }
    }

    private fun createAdapterAndItemClick() {
        bookstoreListAdapter = BookstoreListAdapter(
            onItemClick = { book ->
                findNavController().safeNavigate(
                    BookstoreListFragmentDirections.actionBookstoreListFragmentToBookDetailsFragment(
                        book.id,
                        book.isFavorite)
                )
            }
        )
    }

    private fun collectObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookstoreListViewModel.bookstoreItemsState
                    .collect { bookListState ->
                        when (bookListState) {
                            is Resource.Error -> {
                                requireContext().makeToast(bookListState.message ?: "An unexpected error has occurred.")
                                bookstoreListBinding.pbLoading.visibility = View.GONE
                            }
                            is Resource.Loading -> bookstoreListBinding.pbLoading.visibility = View.VISIBLE
                            is Resource.Success -> {
                                bookstoreListBinding.pbLoading.visibility = View.GONE
                                bookstoreListAdapter.submitList(bookListState.data ?: emptyList())
                            }
                        }
                    }
            }
        }
    }
}