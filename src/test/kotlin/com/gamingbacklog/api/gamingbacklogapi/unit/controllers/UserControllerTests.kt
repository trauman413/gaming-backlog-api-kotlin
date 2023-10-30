package com.gamingbacklog.api.gamingbacklogapi.unit.controllers

import com.gamingbacklog.api.gamingbacklogapi.controllers.UserController
import com.gamingbacklog.api.gamingbacklogapi.models.User
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UserRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.UserResponse
import com.gamingbacklog.api.gamingbacklogapi.models.responses.GameResponse
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
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
  lateinit var libraryService: LibraryService
  lateinit var requestBuilder: RequestBuilder
  var endpoint = "/users/"
  var id1 = "70b664a416135a6e967fadc6"

  @BeforeEach
  fun configureSystem() {
    userService = mock(UserService::class.java)
    libraryService = mock(LibraryService::class.java)
    val userController = UserController(userService)
    val mockMvc = MockMvcBuilders.standaloneSetup(userController)
      .build()
    requestBuilder = RequestBuilder(mockMvc)
    given(userService.convertUserToResponse(any(), any())).willCallRealMethod()
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
        .andExpect(jsonPath("$[0].libraries", equalTo(ArrayList<String>())))
        .andExpect(jsonPath("$[1].id", equalTo(user2.id)))
        .andExpect(jsonPath("$[1].displayName", equalTo(user2.displayName)))
        .andExpect(jsonPath("$[1].email", equalTo(user2.email)))
        .andExpect(jsonPath("$[1].libraries", equalTo(ArrayList<String>())))
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
        .andExpect(jsonPath("$.libraries", equalTo(ArrayList<String>())))
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
        .andExpect(jsonPath("$.libraries", equalTo(ArrayList<String>())))
    }
  }

  @Nested
  @DisplayName("Tests for authenticateUser")
  inner class AuthenticateUser {
    @Test
    fun shouldReturnUserWhenAuthenticationSuccessful() {
      val userRequest = UserRequest("displayName", "so secure", "test@test.com")
      val user = User("id1", "displayName", "so secure", "test@test.com")
      given(userService.getSingleByEmail(any())).willReturn(user)
      given(userService.authenticateUser(any(), any())).willReturn(user)
      endpoint += "login/"
      requestBuilder.runPostRequest(endpoint, requestToString(userRequest))
              .andExpect(status().isOk)
              .andExpect(jsonPath("$.id", equalTo("id1")))
              .andExpect(jsonPath("$.displayName", equalTo(user.displayName)))
              .andExpect(jsonPath("$.email", equalTo(user.email)))
    }

    @Test
    fun shouldFailAuthentication() {
      given(userService.getSingleByEmail(any())).willReturn(null)
      endpoint += "login/"
      requestBuilder.runGetRequest(endpoint)
              .andExpect(status().isNotFound)
              .andExpect(jsonPath("$").doesNotExist())
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
        .andExpect(jsonPath("$.libraries", equalTo(ArrayList<String>())))
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
        .andExpect(jsonPath("$.libraries", equalTo(ArrayList<String>())))
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
        .andExpect(jsonPath("$.libraries", equalTo(ArrayList<String>())))
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

  @Nested
  @DisplayName("Tests for getAllUserLibraries")
  inner class GetAllUserLibraryTests {
    @Test
    fun shouldReturnEmptyList() {
      val user = User("id1", "displayName", "so secure", "test@test.com")
      given(userService.getSingle(any())).willReturn(user)
      requestBuilder.runGetRequest("$endpoint/$id1/libraries")
        .andExpect(status().isOk)
        .andExpect(jsonPath("$", equalTo(ArrayList<LibraryResponse>())))
    }

    @Test
    fun shouldReturnAllLibraries() {
      val user = User("id1", "displayName", "so secure", "test@test.com")
      val library1 = LibraryResponse("libid1", "Backlog", listOf(GameResponse("gameid1", "Sea of Stars")))
      user.libraries.add(library1.id)
      given(userService.getSingle(any())).willReturn(user)
      given(userService.convertUserToResponse(any(), any())).willReturn(UserResponse(user.id, user.displayName, user.email, arrayListOf(library1)))
      requestBuilder.runGetRequest("$endpoint/$id1/libraries")
        .andExpect(status().isOk)
        .andExpect(jsonPath("$[0].id", equalTo(library1.id)))
        .andExpect(jsonPath("$[0].name", equalTo(library1.name)))
        .andExpect(jsonPath("$[0].games[0].id", equalTo(library1.games[0].id)))
        .andExpect(jsonPath("$[0].games[0].name", equalTo(library1.games[0].name)))
    }

    @Test
    fun shouldReturnNull() {
      given(userService.getSingle(any())).willReturn(null)
      requestBuilder.runGetRequest("$endpoint/$id1/libraries")
        .andExpect(status().isNotFound)
    }
  }

  fun requestToString(request: UserRequest): String {
    return Gson().toJson(request)
  }
}