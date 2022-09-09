package kcsit.pt.bookstore.di

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kcsit.pt.bookstore.data.cache.BookstoreDatabase
import kcsit.pt.bookstore.data.remote.BookstoreApi
import kcsit.pt.bookstore.data.repository.BookstoreRepositoryImpl
import kcsit.pt.bookstore.domain.repository.BookstoreRepository
import kcsit.pt.bookstore.util.Constants.BASE_URL_BOOKSTORE
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookstoreModule {
    @Singleton
    @Provides
    fun provideBookstoreApi(
    ): BookstoreApi {
        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        return Retrofit.Builder().baseUrl(BASE_URL_BOOKSTORE).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(BookstoreApi::class.java)
    }

    @Singleton
    @Provides
    @ExperimentalPagingApi
    fun provideBookstoreRepository(bookstoreApi: BookstoreApi, db: BookstoreDatabase): BookstoreRepository =
        BookstoreRepositoryImpl(
            bookstoreApi = bookstoreApi,
            bookstoreDAO = db.bookstoreDao,
            db = db)

    @Singleton
    @Provides
    fun provideBookstoreDatabase(
        app: Application,
    ): BookstoreDatabase =
        Room.databaseBuilder(app, BookstoreDatabase::class.java, "bookstoreDb").build()
}