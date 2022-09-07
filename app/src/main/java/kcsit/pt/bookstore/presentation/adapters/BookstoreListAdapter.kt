package kcsit.pt.bookstore.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kcsit.pt.bookstore.databinding.BookListItemBinding
import kcsit.pt.bookstore.domain.model.Book

class BookstoreListAdapter :
    ListAdapter<Book, BookstoreListAdapter.BookstoreListViewHolder>(BookstoreComparator()) {

    class BookstoreListViewHolder(private val binding: BookListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindBook(book: Book) {
            binding.apply {
                tvBookTitle.text = book.volumeInfo.title
                imgvBook.load(book.volumeInfo.imageLinks.thumbnail.replace("http", "https"))
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