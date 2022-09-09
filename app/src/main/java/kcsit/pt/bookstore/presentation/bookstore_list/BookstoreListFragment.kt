package kcsit.pt.bookstore.presentation.bookstore_list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.get
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class BookstoreListFragment : Fragment(R.layout.fragment_bookstore_list) {
    private lateinit var bookstoreListBinding: FragmentBookstoreListBinding
    private val bookstoreListViewModel: BookstoreListViewModel by viewModels()
    private lateinit var bookstoreListAdapter: BookstoreListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookstoreListBinding = FragmentBookstoreListBinding.bind(view)

        createAdapterAndItemClick()
        setBookstoreRecyclerView()
        collectObservables()
        createMenu()
        getBooks(requireContext().isNetworkAvailable(), bookstoreListViewModel.isFilterActive())
    }

    private fun getBooks(hasInternetConnection: Boolean, isFilterActive: Boolean) {
        bookstoreListViewModel.onEvent(BookListEvent.GetBooks(
            hasInternetConnection = hasInternetConnection,
            isFilterActive = isFilterActive))
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

    private fun createMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_list, menu)
                setFilterMenuIcon(menu[0])
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_list_filter_favorite -> {
                        if (bookstoreListViewModel.isFilterActive()) {
                            Timber.e("IF")
                            bookstoreListViewModel.onEvent(BookListEvent.SetFilter(isFilterActive = false))
                            getBooks(hasInternetConnection = requireContext().isNetworkAvailable(), bookstoreListViewModel.isFilterActive())
                        } else {
                            Timber.e("ELSE")
                            bookstoreListViewModel.onEvent(BookListEvent.SetFilter(isFilterActive = true))
                            getBooks(hasInternetConnection = false, bookstoreListViewModel.isFilterActive())
                        }
                        setFilterMenuIcon(menuItem)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setFilterMenuIcon(menuItemFavorite: MenuItem) {
        if (bookstoreListViewModel.isFilterActive()) {
            menuItemFavorite.icon = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_unfilter_list_24)
            menuItemFavorite.title = getString(R.string.unfilter_by_favorite)
        } else {
            menuItemFavorite.icon = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_filter_list_24)
            menuItemFavorite.title = getString(R.string.filter_by_favorite)
        }
    }

    private fun collectObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookstoreListViewModel.bookstoreItemsState
                    .collectLatest { bookListState ->
                        when (bookListState) {
                            is Resource.Error -> {
                                requireContext().makeToast(bookListState.message ?: "An unexpected error has occurred.")
                                bookstoreListBinding.pbLoading.visibility = View.GONE
                            }
                            is Resource.Loading -> bookstoreListBinding.pbLoading.visibility = View.VISIBLE
                            is Resource.Success -> {
                                bookstoreListBinding.pbLoading.visibility = View.GONE
                                bookListState.data?.let {
                                    bookstoreListAdapter.submitData(bookListState.data)
                                }
                            }
                        }
                    }
            }
        }
    }
}