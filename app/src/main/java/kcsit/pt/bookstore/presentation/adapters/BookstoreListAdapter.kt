package kcsit.pt.bookstore.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kcsit.pt.bookstore.databinding.BookListItemBinding
import kcsit.pt.bookstore.domain.model.Book

class BookstoreListAdapter(
    private val onItemClick: (Book) -> Unit,
) :
    PagingDataAdapter<Book, BookstoreListAdapter.BookstoreListViewHolder>(BookstoreComparator()) {

    inner class BookstoreListViewHolder(private val binding: BookListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindBook(book: Book) {
            binding.imgvBook.load(book.volumeInfo.imageLinks.thumbnail
                .replace("http", "https")){
                crossfade(true)
            }

            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    getItem(adapterPosition)?.let {
                        onItemClick(it)
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookstoreListViewHolder {
        val binding =
            BookListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookstoreListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookstoreListViewHolder, position: Int) {
        getItem(position)?.let { holder.bindBook(it) }
    }

    class BookstoreComparator : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Book, newItem: Book) =
            oldItem == newItem
    }
}