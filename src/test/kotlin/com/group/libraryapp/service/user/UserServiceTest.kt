package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest
@Transactional
open class UserServiceTest @Autowired constructor(
    // constructor 에 @Autowired 를 붙이면 파라미터에 일일이 @Autowired 붙이는 걸 생략 가능!
    private val userRepository: UserRepository,
    private val userService: UserService,
) {

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
        assertThat(userB.isPresent)
        assertThat(userB.get().id).isEqualTo(savedUser.id)
        assertThat(userB.get().name).isEqualTo("B")
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
}
