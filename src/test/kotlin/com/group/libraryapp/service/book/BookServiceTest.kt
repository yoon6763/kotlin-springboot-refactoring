package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.Assertions
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
        val request = BookRequest("이상한 나라의 앨리스", "소설")

        // when
        bookService.saveBook(request)

        // then
        val books = bookRepository.findAll()
        Assertions.assertThat(books).hasSize(1)
        Assertions.assertThat(books[0].name).isEqualTo("이상한 나라의 앨리스")
        Assertions.assertThat(books[0].type).isEqualTo("소설")
    }

    @Test
    @DisplayName("책 대출 테스트")
    fun loanBookTest() {
        // given
        bookRepository.save(Book.fixture())
        userRepository.save(User("크오옹", null))
        val request = BookLoanRequest("크오옹", "이상한 나라의 앨리스")

        // when
        bookService.loanBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        Assertions.assertThat(results).hasSize(1)
        Assertions.assertThat(results[0].user.name).isEqualTo("크오옹")
        Assertions.assertThat(results[0].bookName).isEqualTo("이상한 나라의 앨리스")
        Assertions.assertThat(results[0].isReturn).isFalse
    }

    @Test
    @DisplayName("책이 이미 대출되어 있다면 신규 대출 실패")
    fun loadBookFailTest() {
        // given
        bookRepository.save(Book.fixture())
        val savedUser = userRepository.save(User("크오옹", null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "이상한 나라의 앨리스", false))
        val request = BookLoanRequest("크오옹", "이상한 나라의 앨리스")

        // when & then
        val message = Assertions.catchThrowable { bookService.loanBook(request) }.message
        Assertions.assertThat(message).isEqualTo("이미 대출된 책입니다.")
    }

    @Test
    @DisplayName("책 반납 테스트")
    fun returnBookTest() {
        // given
        bookRepository.save(Book.fixture())
        val savedUser = userRepository.save(User("크오옹", null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "이상한 나라의 앨리스", false))
        val request = BookReturnRequest("크오옹", "이상한 나라의 앨리스")

        // when
        bookService.returnBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        Assertions.assertThat(results).hasSize(1)
        Assertions.assertThat(results[0].isReturn).isTrue
    }

}