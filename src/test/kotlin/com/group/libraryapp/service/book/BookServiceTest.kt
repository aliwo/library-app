package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
open class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
){
    @AfterEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
        userLoanHistoryRepository.deleteAll()
    }

    @Test
    fun saveBookTest() {
        // GIven
        val request = BookRequest("이상한 나라의 엘리스", BookType.COMPUTER)

        // When
        bookService.saveBook(request)

        // Then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("이상한 나라의 엘리스")
        assertThat(books[0].type).isEqualTo(BookType.COMPUTER)
    }

    @Test
    @DisplayName("책 대출이 정상 동작한다")
    fun loanBookTest() {
        // Given
        bookRepository.save(Book.fixture("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("정승원", null))
        val request = BookLoanRequest("정승원", "이상한 나라의 엘리스")

        // When
        bookService.loanBook(request)

        // Then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].bookName).isEqualTo("이상한 나라의 엘리스")
        assertThat(results[0].user.id).isEqualTo(savedUser.id)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    fun loanBookFailTest() {
        // Given
        bookRepository.save(Book.fixture("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("정승원", null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(user=savedUser))
        val request = BookLoanRequest("정승원", "이상한 나라의 엘리스")

        // When & Then
        val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(request)
        }.message

        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    // TODO: 테스트에 @Transactional 을 쓰면 이 테스트만 터짐... returnBook() 을 할 떄 user 의 userLoanHistories 가 없음 왜 그럴까? fetch join 을 하면 해결될까?
    @Test
    fun returnBookTest() {
        // Given
        bookRepository.save(Book.fixture("이상한 나라의 엘리스"))
        val savedUser = userRepository.save(User("정승원", null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(user=savedUser))
        val request = BookReturnRequest("정승원", "이상한 나라의 엘리스")

        // When
        bookService.returnBook(request)

        // Then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    fun countLoanedBookTest() {
        // Given
        val user = userRepository.save(User("test user1", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(user, "A"),
                UserLoanHistory.fixture(user, "B", UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(user, "B", UserLoanStatus.RETURNED),
            )
        )

        // When
        val result = bookService.countLoanedBook()

        // Then
        assertThat(result).isEqualTo(1)
    }
    
    @Test
    fun getBookStatisticsTest() {
        // Given
        bookRepository.saveAll(listOf(
            Book.fixture("A", BookType.COMPUTER),
            Book.fixture("B", BookType.COMPUTER),
            Book.fixture("C", BookType.SCIENCE),
        ))
        
        // When
        val bookStatistics = bookService.getBookStatistics()

        // Then
        assertThat(bookStatistics).hasSize(2)
        assertThat(bookStatistics.first { stat -> stat.type == BookType.COMPUTER }.count)
            .isEqualTo(2L)
        assertThat(bookStatistics.first { stat -> stat.type == BookType.SCIENCE }.count)
            .isEqualTo(1L)
    }
}