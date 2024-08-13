package com.gamingbacklog.api.gamingbacklogapi.integration

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.controllers.LibraryController
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game10
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game5
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game7
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game8
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game9
import com.gamingbacklog.api.gamingbacklogapi.integration.utils.RequestBuilder
import com.gamingbacklog.api.gamingbacklogapi.integration.utils.TestUtils.requestToString
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UpdateLibraryGamesRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.GameResponse
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameInstanceRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import com.gamingbacklog.api.gamingbacklogapi.services.GameService
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import com.google.gson.Gson
import org.bson.types.ObjectId
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.hasSize
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
@Import(LibraryController::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class LibraryIT {
  private val libraryRepository = Mockito.mock(LibraryRepository::class.java)
  private val gameRepository = Mockito.mock(GameRepository::class.java)
  private val gameInstanceRepository = Mockito.mock(GameInstanceRepository::class.java)
  private val igdbClient = Mockito.mock(IGDBClient::class.java)
  private val gameService = GameService(gameRepository, igdbClient)
  private val libraryService = LibraryService(libraryRepository, gameService, GameInstanceService(gameInstanceRepository, gameService))
  lateinit var requestBuilder: RequestBuilder
  var endpoint = "/libraries/"
  val id1 = "70b664a416135a6e967fadc6"
  val id2 = "dd7f03b962f1f3416d08ee0f"
  val id3 = "5e11b8c6fbb706c75e058337"
  val id4 = "8e9f5b65026d638f7cd75166"

  @BeforeEach
  fun configureSystem() {
    val libraryController = LibraryController(libraryService)
    val mockMvc = MockMvcBuilders.standaloneSetup(libraryController)
      .build()
    requestBuilder = RequestBuilder(mockMvc)
  }

  @Nested
  @DisplayName("Tests for getSingleLibrary")
  inner class GetSingleLibrary {
    @Test
    fun shouldReturnLibrarySuccessfully() {
      val library = Library(id1, "Owned Games", ArrayList())
      library.games.add("gameId1")
      library.games.add("gameId2")
      given(libraryRepository.findOneById(any())).willReturn(library)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(jsonPath("$.name", equalTo("Owned Games")))
        .andExpect(jsonPath("$.games[0]", equalTo("gameId1")))
        .andExpect(jsonPath("$.games[1]", equalTo("gameId2")))
        .andExpect(jsonPath("$.id", equalTo(id1)))
    }

    @Test
    fun shouldReturnNoLibrary() {
      given(libraryRepository.findOneById(any())).willReturn(null)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$").doesNotExist())
    }
  }

  @Nested
  @DisplayName("Tests for createLibrary")
  inner class CreateLibrary {
    @Test
    fun shouldSuccessfullyCreateLibraryWithNoGamesPassedIn() {
      val library = Library(id2, "Backlog", ArrayList())
      val libraryRequest = LibraryRequest("Backlog", null)
      requestBuilder.runPostRequest(endpoint, requestToString(libraryRequest))
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.name", equalTo(library.name)))
        .andExpect(jsonPath("$.games", equalTo(library.games)))
    }

    @Test
    fun shouldSuccessfullyCreateLibraryWithGamesPassedIn() {
      val library = Library(id2, "Backlog", ArrayList())
      library.games.add(id1)
      val libraryRequest = LibraryRequest("Backlog", arrayListOf(id1))
      requestBuilder.runPostRequest(endpoint, requestToString(libraryRequest))
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.name", equalTo(library.name)))
        .andExpect(jsonPath("$.games[0]", equalTo(id1)))
    }
  }

  @Nested
  @DisplayName("Tests for addToLibrary")
  inner class AddToLibrary {

    @Test
    fun shouldSuccessfullyAddToLibrary() {
      val library = Library(id2, "Backlog", ArrayList())
      given(gameInstanceRepository.findOneById(any())).willReturn(game5)
      given(gameInstanceRepository.findByName(any())).willReturn(game5)
      given(libraryRepository.findOneById(any())).willReturn(library)
      val request = UpdateLibraryGamesRequest(id1)
      requestBuilder.runPostRequest("$endpoint$id2/games", Gson().toJson(request))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.name", equalTo(library.name)))
        .andExpect(jsonPath("$.games[0].id", equalTo(id1)))
        .andExpect(jsonPath("$.games[0].name", equalTo("Trails in the Sky")))
    }

    @Test
    fun shouldSuccessfullyAddToLibraryMultipleLibraries() {
      val library = Library(id2, "Backlog", ArrayList())
      val library2 = Library(id3, "Wishlist", ArrayList())
      given(gameInstanceRepository.findOneById(any())).willReturn(game5)
      given(gameInstanceRepository.findByName(any())).willReturn(game5)
      given(libraryRepository.findOneById(any())).willReturn(library).willReturn(library2)
      val request = UpdateLibraryGamesRequest(id1, listOf(id2, id3))
      requestBuilder.runPostRequest("$endpoint/games", Gson().toJson(request))
        .andExpect(status().isOk)
    }
  }

  @Nested
  @DisplayName("Tests for deleteLibrary")
  inner class DeleteLibrary {
    @Test
    fun shouldSuccessfullyDeleteLibrary() {
      val library1 = Library("id1", "Backlog", ArrayList())
      val libraries = ArrayList<Library>()
      libraries.add(library1)
      given(libraryRepository.deleteById(any())).willAnswer {
        libraries.remove(library1)
      }
      requestBuilder.runDeleteRequest("$endpoint/$id1")
        .andExpect(status().isOk)
        .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Successfully deleted library")))
      Assertions.assertTrue(libraries.isEmpty())
    }
  }

  @Nested
  @DisplayName("Tests for getGameFromLibrary")
  inner class GetGameFromLibrary {
    @Test
    fun shouldGetGameFromLibrarySuccessfully() {
      val library = Library("id1", "Backlog", ArrayList())
      library.games.add(id2)
      given(libraryRepository.findOneById(any())).willReturn(library)
      given(gameInstanceRepository.findOneById(any())).willReturn(game7)
      given(gameInstanceRepository.findByName(any())).willReturn(game7)
      endpoint += "$id1/games/$id2"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.id", equalTo(id2)))
        .andExpect(jsonPath("$.name", equalTo("Fire Emblem: Engage")))
        .andExpect(jsonPath("$.igdbId", equalTo("19")))
        .andExpect(jsonPath("$.platforms[0]", equalTo("Nintendo Switch")))
        .andExpect(jsonPath("$.genres[0]", equalTo("RPG")))
        .andExpect(jsonPath("$.universes[0]", equalTo("Fire Emblem")))
        .andExpect(jsonPath("$.companies[0]", equalTo("Nintendo")))
        .andExpect(jsonPath("$.companies[1]", equalTo("Intelligent Systems")))
        .andExpect(jsonPath("$.releaseDate[0]", equalTo("January 20 2023")))
        .andExpect(jsonPath("$.images[0]", equalTo("")))
    }

    @Test
    fun shouldGetNotFoundForGameInLibrary() {
      val library = Library("id1", "Backlog", ArrayList())
      given(libraryRepository.findOneById(any())).willReturn(library)
      given(gameInstanceRepository.findOneById(any())).willReturn(null)
      endpoint += "$id1/games/${id2}"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isNotFound)
        .andExpect(jsonPath("$").doesNotExist())
    }
  }

  @Nested
  @DisplayName("Tests for deleteGameFromLibrary")
  inner class DeleteGameFromLibraryTests {
    @Test
    fun shouldSuccessfullyDeleteGameFromLibrary() {
      val library = Library(id1, "Backlog", arrayListOf(id2, id3, id4))
      given(libraryRepository.findOneById(any())).willReturn(library)
      given(gameInstanceRepository.findOneById(ObjectId(id2))).willReturn(game8)
      given(gameInstanceRepository.findOneById(ObjectId(id3))).willReturn(game9)
      given(gameInstanceRepository.findOneById(ObjectId(id4))).willReturn(game10)
      given(gameInstanceRepository.findByName("Super Mario 64")).willReturn(game8)
      given(gameInstanceRepository.findByName("Super Mario Sunshine")).willReturn(game9)
      given(gameInstanceRepository.findByName("Super Mario Galaxy")).willReturn(game10)

      val request = UpdateLibraryGamesRequest(id3)
      requestBuilder.runDeleteRequest("$endpoint$id1/games", Gson().toJson(request))
        .andExpect(status().isOk)
        .andExpect(jsonPath<List<GameResponse>>("$.games", hasSize(2)))
        .andExpect(jsonPath("$.games[0].id", equalTo(id2)))
        .andExpect(jsonPath("$.games[0].name", equalTo("Super Mario 64")))
        .andExpect(jsonPath("$.games[1].id", equalTo(id4)))
        .andExpect(jsonPath("$.games[1].name", equalTo("Super Mario Galaxy")))
    }
  }
}