package com.gamingbacklog.api.gamingbacklogapi.unit.controllers

import com.gamingbacklog.api.gamingbacklogapi.controllers.UserController
import com.gamingbacklog.api.gamingbacklogapi.models.User
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UserRequest
import com.gamingbacklog.api.gamingbacklogapi.services.UserService
import com.google.gson.Gson
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.*
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders


@SpringBootTest
@AutoConfigureMockMvc
@Import(UserController::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class UserControllerTests {
  lateinit var userService: UserService
  lateinit var requestBuilder: RequestBuilder
  var endpoint = "/users/"
  var id1 = "70b664a416135a6e967fadc6"

  @BeforeEach
  fun configureSystem() {
    userService = mock(UserService::class.java)
    val userController = UserController(userService)
    val mockMvc = MockMvcBuilders.standaloneSetup(userController)
      .build()
    requestBuilder = RequestBuilder(mockMvc)
    given(userService.convertUserToResponse(any())).willCallRealMethod()
  }

  @Nested
  @DisplayName("Tests for getUsers")
  inner class GetUsers {
    @Test
    fun shouldReturnAllUsers() {
      val user1 = User("id1", "displayName", "so secure", "test@test.com")
      val user2 = User("id2", "sauce1", "123456", "fun@fun.com")
      val users = listOf(user1, user2)
      given(userService.getAll()).willReturn(users)
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$[0].id", equalTo(user1.id)))
        .andExpect(jsonPath("$[0].displayName", equalTo(user1.displayName)))
        .andExpect(jsonPath("$[0].email", equalTo(user1.email)))
        .andExpect(jsonPath("$[1].id", equalTo(user2.id)))
        .andExpect(jsonPath("$[1].displayName", equalTo(user2.displayName)))
        .andExpect(jsonPath("$[1].email", equalTo(user2.email)))

    }
  }

  @Nested
  @DisplayName("Tests for getSingleUser")
  inner class GetSingleUser {
    @Test
    fun shouldReturnUserSuccessfully() {
      val user = User("id1", "displayName", "so secure", "test@test.com")
      given(userService.getSingle(any())).willReturn(user)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id", equalTo("id1")))
        .andExpect(jsonPath("$.displayName", equalTo(user.displayName)))
        .andExpect(jsonPath("$.email", equalTo(user.email)))
    }

    @Test
    fun shouldNotFindUser() {
      given(userService.getSingle(any())).willReturn(null)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isNotFound)
        .andExpect(jsonPath("$").doesNotExist())
    }
  }
  
  @Nested
  @DisplayName("Tests for createUser")
  inner class CreateUser {
    @Test
    fun shouldSuccessfullyCreateUser() {
      val user = User("id1", "epicsauceXD", "123", "dude@something.com")
      given(userService.create(any())).willReturn(user)
      val userRequest = UserRequest("epicsauceXD", "123", "dude@something.com")
      requestBuilder.runPostRequest(endpoint, requestToString(userRequest))
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.id", equalTo(user.id)))
        .andExpect(jsonPath("$.displayName", equalTo(user.displayName)))
        .andExpect(jsonPath("$.email", equalTo(user.email)))
    }
  }

  @Nested
  @DisplayName("Tests for updateUserInfo")
  inner class UpdateUser {
    @Test
    fun shouldUpdateAllFields() {
      val user = User("id1", "newCoolName", "betterPass", "email2@gmail.com")
      given(userService.updateUserFields(any(), any())).willReturn(user)
      val fieldsUpdated = mapOf("displayName" to "newCoolName", "password" to "betterPass", "email" to "email2@gmail.com")
      requestBuilder.runPatchRequest("$endpoint/$id1/", Gson().toJson(fieldsUpdated))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id", equalTo(user.id)))
        .andExpect(jsonPath("$.displayName", equalTo(user.displayName)))
        .andExpect(jsonPath("$.email", equalTo(user.email)))
    }

    @Test
    fun shouldUpdateOnlyOneFieldPassedIn() {
      val user = User("id1", "newCoolName", "ogPass", "email@gmail.com")
      given(userService.updateUserFields(any(), any())).willReturn(user)
      val fieldsUpdated = mapOf("displayName" to "newCoolName")
      requestBuilder.runPatchRequest("$endpoint/$id1/", Gson().toJson(fieldsUpdated))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.displayName", equalTo(user.displayName)))
        .andExpect(jsonPath("$.email", equalTo(user.email)))
    }

    @Test
    fun shouldUpdateNoFields() {
      val user = User("id1", "displayName", "ogPass", "email@gmail.com")
      given(userService.updateUserFields(any(), any())).willReturn(user)
      val fieldsUpdated = emptyMap<String, String>()
      requestBuilder.runPatchRequest("$endpoint/$id1/", Gson().toJson(fieldsUpdated))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id", equalTo(user.id)))
        .andExpect(jsonPath("$.displayName", equalTo(user.displayName)))
        .andExpect(jsonPath("$.email", equalTo(user.email)))
    }
  }

  @Nested
  @DisplayName("Tests for deleteUser")
  inner class DeleteUser {
    @Test
    fun shouldSuccessfullyDeleteUser() {
      val user1 = User("id1", "displayName", "so secure", "test@test.com")
      val users = arrayListOf(user1)
      given(userService.delete(any())).will {
        users.remove(user1)
      }
      requestBuilder.runDeleteRequest("$endpoint/$id1")
        .andExpect(status().isNoContent)
      Assertions.assertTrue(users.isEmpty())
    }
  }

  fun requestToString(request: UserRequest): String {
    return Gson().toJson(request)
  }
}