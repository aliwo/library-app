package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @Transactional
    fun saveBook(request: BookRequest) {
        val book = Book.fixture(request.name)
        bookRepository.save(book)
    }

    @Transactional
    fun loanBook(request: BookLoanRequest) {
        val book = bookRepository.findByName(request.bookName) ?: throw IllegalArgumentException()
        if (userLoanHistoryRepository.findByBookNameAndStatus(request.bookName, UserLoanStatus.LOANED) != null) {
            throw java.lang.IllegalArgumentException("진작 대출되어 있는 책입니다")
        }
        val user = userRepository.findByName(request.userName) ?: throw IllegalArgumentException()
        user.loanBook(book)
    }

    @Transactional
    fun returnBook(request: BookReturnRequest) {
        val user = userRepository.findByName(request.userName) ?: throw IllegalArgumentException()
        user.returnBook(request.bookName)
    }

    @Transactional(readOnly = true)
    fun countLoanedBook(): Int {
        return userLoanHistoryRepository.findAllByStatus(UserLoanStatus.LOANED).size
    }

    @Transactional(readOnly = true)
    fun getBookStatistics(): List<BookStatResponse> {
        return bookRepository.getStats()
    }
}