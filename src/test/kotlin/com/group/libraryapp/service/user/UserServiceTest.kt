package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository
) {

    @AfterEach
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("유저 저장 테스트")
    fun saveUserTest() {
        // given
        val user = UserCreateRequest("크오옹", null)

        // when
        userService.saveUser(user)

        // then
        val results = userRepository.findAll()
        Assertions.assertThat(results).hasSize(1)
        Assertions.assertThat(results[0].name).isEqualTo("크오옹")
        Assertions.assertThat(results[0].age).isNull()
    }

    @Test
    @DisplayName("유저 조회 테스트")
    fun getUsersTest() {
        // given
        userRepository.saveAll(
            listOf(
                User("A", 20),
                User("B", null),
            )
        )

        // when
        val results = userService.getUsers()

        // then
        Assertions.assertThat(results).hasSize(2)
        Assertions.assertThat(results).extracting("name").containsExactly("A", "B")
        Assertions.assertThat(results).extracting("age").containsExactly(20, null)
    }

    @Test
    @DisplayName("유저 이름 수정 테스트")
    fun updateUserNameTest() {
        // given
        val savedUser = userRepository.save(User("A", null))
        val request = UserUpdateRequest(savedUser.id, "B")

        userService.updateUserName(request)

        // when
        val result = userRepository.findAll()[0]
        Assertions.assertThat(result.name).isEqualTo("B")
    }

    @Test
    @DisplayName("유저 나이 수정 테스트")
    fun deleteUserTest() {
        // given
        userRepository.save(User("A", null))

        // when
        userService.deleteUser("A")

        // then
        val results = userRepository.findAll()
        Assertions.assertThat(results).isEmpty()
    }

}