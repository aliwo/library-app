package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    // constructor 에 @Autowired 를 붙이면 파라미터에 일일이 @Autowired 붙이는 걸 생략 가능!
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @BeforeEach
    fun wipeOut() {
        userRepository.deleteAll()
        userLoanHistoryRepository.deleteAll()
    }

    @Test
    fun saveUserTest() {
        // Given
        val userCreateRequest = UserCreateRequest("정승원", null)

        // When
        userService.saveUser(userCreateRequest)

        // Then
        val results = userRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("정승원")
        assertThat(results[0].age).isNull()
    }

    @Test
    fun getUsersTest() {
        // Given
        val users = listOf(
            User("A", 20),
            User("B", null),
        )
        userRepository.saveAll(users);

        // When
        val results = userService.getUsers()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B")
    }

    @Test
    fun updateUserNameTest() {
        // Given
        val savedUser = userRepository.save(User("A", 20))

        // When
        userService.updateUserName(
            UserUpdateRequest(savedUser.id!!, "B")
        )

        // Then
        val userB = userRepository.findByName("B")
        assertThat(userB).isNotNull
        assertThat(userB!!.id).isEqualTo(savedUser.id)
        assertThat(userB!!.name).isEqualTo("B")
    }

    @Test
    fun deleteUserTest() {
        // Given
        userRepository.save(User("A", 20))

        // When
        userService.deleteUser("A")

        // Then
        assertThat(userRepository.findAll()).isEmpty()
    }

    @Test
    fun getUserLoanHistoriesTest1() {
        // Given
        userRepository.save(User("A", null))

        // When
        val results = userService.getUserLoanHistories()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).isEmpty()
    }

    @Test
    fun getUserLoanHistoriesTest2() {
        // Given
        val savedUser = userRepository.save(User("A", null))
        userLoanHistoryRepository.saveAll(listOf(
            UserLoanHistory.fixture(savedUser,"책1", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUser,"책2", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUser,"책3", UserLoanStatus.RETURNED),
        ))

        // When
        val results = userService.getUserLoanHistories()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).hasSize(3)
        assertThat(results[0].books).extracting("name")
            .containsExactlyInAnyOrder("책1", "책2", "책3")
        assertThat(results[0].books).extracting("status")
            .containsExactlyInAnyOrder(UserLoanStatus.LOANED, UserLoanStatus.LOANED, UserLoanStatus.RETURNED)
    }

}
