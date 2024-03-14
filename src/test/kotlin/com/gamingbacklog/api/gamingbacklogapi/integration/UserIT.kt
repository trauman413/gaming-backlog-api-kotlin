package com.gamingbacklog.api.gamingbacklogapi.integration

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.controllers.UserController
import com.gamingbacklog.api.gamingbacklogapi.integration.utils.RequestBuilder
import com.gamingbacklog.api.gamingbacklogapi.integration.utils.TestUtils.requestToString
import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.User
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UserRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.GameResponse
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameInstanceRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.UserRepository
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import com.gamingbacklog.api.gamingbacklogapi.services.GameService
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import com.gamingbacklog.api.gamingbacklogapi.services.UserService
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@AutoConfigureMockMvc
@Import(UserController::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class UserIT {
  private val libraryRepository = Mockito.mock(LibraryRepository::class.java)
  private val gameRepository = Mockito.mock(GameRepository::class.java)
  private val gameInstanceRepository = Mockito.mock(GameInstanceRepository::class.java)
  private val igdbClient = Mockito.mock(IGDBClient::class.java)
  private val gameService = GameService(gameRepository, igdbClient)
  private val libraryService = LibraryService(libraryRepository, gameService, GameInstanceService(gameInstanceRepository, gameService))
  private val userRepository = Mockito.mock(UserRepository::class.java)
  val userService = UserService(userRepository, libraryService)

  lateinit var requestBuilder: RequestBuilder
  var endpoint = "/users/"
  var id1 = "70b664a416135a6e967fadc6"
  val id2 = "dd7f03b962f1f3416d08ee0f"
  val id3 = "5e11b8c6fbb706c75e058337"
  val id4 = "8e9f5b65026d638f7cd75166"

  @BeforeEach
  fun configureSystem() {
    val userController = UserController(userService)
    val mockMvc = MockMvcBuilders.standaloneSetup(userController)
      .build()
    requestBuilder = RequestBuilder(mockMvc)
  }

  @Nested
  @DisplayName("Tests for getUsers")
  inner class GetUsers {
    @Test
    fun shouldReturnAllUsers() {
      val user1 = User("id1", "displayName", "so secure", "test@test.com")
      val user2 = User("id2", "sauce1", "123456", "fun@fun.com")
      val users = listOf(user1, user2)
      given(userRepository.findAll()).willReturn(users)
      requestBuilder.runGetRequest(endpoint)
        .andExpect(MockMvcResultMatchers.status().isOk)
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
      given(userRepository.findOneById(any())).willReturn(user)
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
      given(userRepository.findOneById(any())).willReturn(null)
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
      val user = User(id1, "epicsauceXD", "123", "dude@something.com")
      val userRequest = UserRequest("epicsauceXD", "123", "dude@something.com")
      given(libraryRepository.findOneById(any())).willReturn(Library(id2, "All Games", ArrayList()))
      requestBuilder.runPostRequest(endpoint, requestToString(userRequest))
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.displayName", equalTo(user.displayName)))
        .andExpect(jsonPath("$.email", equalTo(user.email)))
        .andExpect(jsonPath("$.libraries[0].id", equalTo(id2)))
        .andExpect(jsonPath("$.libraries[0].name", equalTo("All Games")))
    }
  }

  @Nested
  @DisplayName("Tests for authenticateUser")
  inner class AuthenticateUser {
    @Test
    fun shouldReturnUserWhenAuthenticationSuccessful() {
      val userRequest = UserRequest("displayName", "so secure", "test@test.com")
      val user = User("id1", "displayName", "so secure", "test@test.com")
      given(userRepository.findByEmail(any())).willReturn(user)
      endpoint += "login/"
      requestBuilder.runPostRequest(endpoint, requestToString(userRequest))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id", equalTo("id1")))
        .andExpect(jsonPath("$.displayName", equalTo(user.displayName)))
        .andExpect(jsonPath("$.email", equalTo(user.email)))
    }

    @Test
    fun shouldFailAuthentication() {
      given(userRepository.findByEmail(any())).willReturn(null)
      endpoint += "login/"
      requestBuilder.runPostRequest(endpoint, requestToString(UserRequest(null, null, null)))
        .andExpect(status().isBadRequest)
        .andExpect(jsonPath("$").doesNotExist())
    }
  }

  @Nested
  @DisplayName("Tests for updateUserInfo")
  inner class UpdateUser {
    @Test
    fun shouldUpdateAllFields() {
      val user = User(id1, "coolName", "pass", "email@gmail.com")
      given(userRepository.findOneById(any())).willReturn(user)
      val fieldsUpdated = UserRequest("newCoolName", "betterPass", "email2@gmail.com")
      requestBuilder.runPatchRequest("$endpoint/$id1/", requestToString(fieldsUpdated))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id", equalTo(id1)))
        .andExpect(jsonPath("$.displayName", equalTo(fieldsUpdated.displayName)))
        .andExpect(jsonPath("$.email", equalTo(fieldsUpdated.email)))
        .andExpect(jsonPath("$.libraries", equalTo(ArrayList<String>())))
    }

    @Test
    fun shouldUpdateOnlyOneFieldPassedIn() {
      val user = User("id1", "ogCoolName", "ogPass", "email@gmail.com")
      given(userRepository.findOneById(any())).willReturn(user)
      val fieldsUpdated = UserRequest("newCoolName", null, null)
      requestBuilder.runPatchRequest("$endpoint/$id1/", requestToString(fieldsUpdated))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.displayName", equalTo(fieldsUpdated.displayName)))
        .andExpect(jsonPath("$.email", equalTo(user.email)))
        .andExpect(jsonPath("$.libraries", equalTo(ArrayList<String>())))
    }

    @Test
    fun shouldUpdateNoFields() {
      val user = User("id1", "displayName", "ogPass", "email@gmail.com")
      given(userRepository.findOneById(any())).willReturn(user)
      val fieldsUpdated = UserRequest(null, null, null)
      requestBuilder.runPatchRequest("$endpoint/$id1/", requestToString(fieldsUpdated))
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
      given(userRepository.deleteById(any())).willAnswer {
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
      given(userRepository.findOneById(any())).willReturn(user)
      requestBuilder.runGetRequest("$endpoint/$id1/libraries")
        .andExpect(status().isOk)
        .andExpect(jsonPath("$", equalTo(ArrayList<LibraryResponse>())))
    }

    @Test
    fun shouldReturnAllLibraries() {
      val user = User("id1", "displayName", "so secure", "test@test.com")
      val library1 = LibraryResponse(id2, "Backlog", listOf(GameResponse(id3, "Sea of Stars")))
      val game = GameInstance(id3, "", "Sea of Stars", listOf(),
        listOf(), listOf(), listOf(), listOf(), listOf(), "")
      user.libraries.add(library1.id)
      given(userRepository.findOneById(any())).willReturn(user)
      given(libraryRepository.findOneById(any())).willReturn(Library(id2, "Backlog", arrayListOf(id3)))
      given(gameInstanceRepository.findOneById(any())).willReturn(game)
      given(gameInstanceRepository.findByName(any())).willReturn(game)
      requestBuilder.runGetRequest("$endpoint/$id1/libraries")
        .andExpect(status().isOk)
        .andExpect(jsonPath("$[0].id", equalTo(library1.id)))
        .andExpect(jsonPath("$[0].name", equalTo(library1.name)))
        .andExpect(jsonPath("$[0].games[0].id", equalTo(library1.games[0].id)))
        .andExpect(jsonPath("$[0].games[0].name", equalTo(library1.games[0].name)))
    }

    @Test
    fun shouldReturnNull() {
      given(userRepository.findOneById(any())).willReturn(null)
      requestBuilder.runGetRequest("$endpoint/$id1/libraries")
        .andExpect(status().isNotFound)
    }
  }
}