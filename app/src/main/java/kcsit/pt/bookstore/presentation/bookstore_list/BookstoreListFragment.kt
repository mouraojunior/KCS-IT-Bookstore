package kcsit.pt.bookstore.presentation.bookstore_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kcsit.pt.bookstore.R
import kcsit.pt.bookstore.databinding.FragmentBookstoreListBinding
import kcsit.pt.bookstore.presentation.adapters.BookstoreListAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class BookstoreListFragment : Fragment(R.layout.fragment_bookstore_list) {
    private lateinit var bookstoreListBinding: FragmentBookstoreListBinding
    private val bookstoreListViewModel: BookstoreListViewModel by viewModels()
    private val bookstoreListAdapter = BookstoreListAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookstoreListBinding = FragmentBookstoreListBinding.bind(view)

        setBookstoreRecyclerView()
        collectObservables()
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

    private fun collectObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookstoreListViewModel.bookstoreItemsState
                    .collectLatest {
                        Timber.e("DATA RECEIVED: ${it.data}")
                        bookstoreListAdapter.submitList(it.data ?: emptyList())
                    }
            }
        }
    }
}