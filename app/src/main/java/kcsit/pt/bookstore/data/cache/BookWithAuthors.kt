package kcsit.pt.bookstore.data.cache

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import kcsit.pt.bookstore.data.cache.entity.AuthorEntity
import kcsit.pt.bookstore.data.cache.entity.BookAuthorCrossRefEntity
import kcsit.pt.bookstore.data.cache.entity.BookEntity
import kcsit.pt.bookstore.domain.model.*

data class BookWithAuthors(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "authorNameId",
        associateBy = Junction(BookAuthorCrossRefEntity::class)
    )
    val authors: List<AuthorEntity>,
) {
    fun toBook() = Book(
        id = book.bookId,
        isFavorite = book.isFavorite,
        saleInfo = SaleInfo(book.buyLink,
            ListPrice(book.amount, book.currencyCode)),
        VolumeInfo(authors = authors.map { it.authorNameId },
            description = book.description,
            imageLinks = ImageLinks(thumbnail = book.thumbnail),
            subtitle = book.subtitle,
            title = book.title)
    )
}