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
import com.group.libraryapp.dto.book.response.BookStatResponse
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("책 저장 테스트")
    fun saveBookTest() {
        // given
        val request = BookRequest("이상한 나라의 앨리스", BookType.COMPUTER)

        // when
        bookService.saveBook(request)

        // then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("이상한 나라의 앨리스")
        assertThat(books[0].type).isEqualTo(BookType.COMPUTER)
    }

    @Test
    @DisplayName("책 대출 테스트")
    fun loanBookTest() {
        // given
        bookRepository.save(Book.fixture(name = "이상한 나라의 앨리스"))
        userRepository.save(User("크오옹", null))
        val request = BookLoanRequest("크오옹", "이상한 나라의 앨리스")

        // when
        bookService.loanBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].user.name).isEqualTo("크오옹")
        assertThat(results[0].bookName).isEqualTo("이상한 나라의 앨리스")
        assertThat(results[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    @DisplayName("책이 이미 대출되어 있다면 신규 대출 실패")
    fun loadBookFailTest() {
        // given
        bookRepository.save(Book.fixture(name = "이상한 나라의 앨리스"))
        val savedUser = userRepository.save(User("크오옹", null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "이상한 나라의 앨리스"))
        val request = BookLoanRequest("크오옹", "이상한 나라의 앨리스")

        // when & then
        val message = Assertions.catchThrowable { bookService.loanBook(request) }.message
        assertThat(message).isEqualTo("이미 대출된 책입니다.")
    }

    @Test
    @DisplayName("책 반납 테스트")
    fun returnBookTest() {
        // given
        bookRepository.save(Book.fixture())
        val savedUser = userRepository.save(User("크오옹", null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "이상한 나라의 앨리스"))
        val request = BookReturnRequest("크오옹", "이상한 나라의 앨리스")

        // when
        bookService.returnBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("책 대여 권수를 정상 확인한다")
    fun countLoanedBookTest() {
        // given
        val savedUser = userRepository.save(User("크오옹", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "A"),
                UserLoanHistory.fixture(savedUser, "B", status = UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(savedUser, "C", status = UserLoanStatus.RETURNED),
            )
        )

        // when
        val count = bookService.countLoanedBook()

        // then
        assertThat(count).isEqualTo(1)
    }

    @Test
    @DisplayName("분야별 책 권수를 정상 확인한다")
    fun getBookStatisticsTest() {
        // given
        bookRepository.saveAll(
            listOf(
                Book.fixture(type = BookType.COMPUTER),
                Book.fixture(type = BookType.COMPUTER),
                Book.fixture(type = BookType.SCIENCE),
            )
        )

        // when
        val results = bookService.getBookStatistics()

        // then
        assertThat(results).hasSize(2)
        assertCount(results, BookType.COMPUTER, 2)
        assertCount(results, BookType.SCIENCE, 1)
    }

    private fun assertCount(results: List<BookStatResponse>, type: BookType, count: Int) {
        assertThat(results.first { it.type == type }.count).isEqualTo(count)
    }

}