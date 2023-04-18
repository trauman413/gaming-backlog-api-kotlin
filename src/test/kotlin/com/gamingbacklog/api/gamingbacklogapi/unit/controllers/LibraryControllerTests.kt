package com.gamingbacklog.api.gamingbacklogapi.unit.controllers

import com.gamingbacklog.api.gamingbacklogapi.controllers.LibraryController
import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import com.google.gson.Gson
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@AutoConfigureMockMvc
@Import(LibraryController::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class LibraryControllerTests {
  lateinit var libraryService: LibraryService
  lateinit var requestBuilder: RequestBuilder
  var endpoint = "/libraries/"
  var id1 = "70b664a416135a6e967fadc6"
  var id2 = "dd7f03b962f1f3416d08ee0f"

  @BeforeEach
  fun configureSystem() {
    libraryService = mock(LibraryService::class.java)
    val libraryController = LibraryController(libraryService)
    val mockMvc = MockMvcBuilders.standaloneSetup(libraryController)
      .build()
    requestBuilder = RequestBuilder(mockMvc)
  }

  @Nested
  @DisplayName("Tests for getLibraries")
  inner class GetLibraries {
    @Test
    fun shouldReturnAllLibraries() {
      val library1 = Library("id1", "Backlog", ArrayList())
      val library2 = Library("id2", "Owned Games", ArrayList())
      library2.games.add("gameId1")
      library2.games.add("gameId2")
      val libraries = ArrayList<Library>()
      libraries.add(library1)
      libraries.add(library2)
      given(libraryService.getAll()).willReturn(libraries)
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$[0].name", equalTo("Backlog")))
        .andExpect(jsonPath("$[0].games", equalToObject(ArrayList<String>())))
        .andExpect(jsonPath("$[0].id", equalTo("id1")))
        .andExpect(jsonPath("$[1].name", equalTo("Owned Games")))
        .andExpect(jsonPath("$[1].games[0]", equalTo("gameId1")))
        .andExpect(jsonPath("$[1].games[1]", equalTo("gameId2")))
        .andExpect(jsonPath("$[1].id", equalTo("id2")))
    }
  }

  @Nested
  @DisplayName("Tests for getSingleLibrary")
  inner class GetSingleLibrary {
    @Test
    fun shouldReturnLibrarySuccessfully() {
      val library = Library(id1, "Owned Games", ArrayList())
      library.games.add("gameId1")
      library.games.add("gameId2")
      given(libraryService.getSingle(any())).willReturn(library)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.name", equalTo("Owned Games")))
        .andExpect(jsonPath("$.games[0]", equalTo("gameId1")))
        .andExpect(jsonPath("$.games[1]", equalTo("gameId2")))
        .andExpect(jsonPath("$.id", equalTo(id1)))
    }

    @Test
    fun shouldReturnNoLibrary() {
      given(libraryService.getSingle(any())).willReturn(null)
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
      given(libraryService.create(any())).willReturn(library)
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
      given(libraryService.create(any())).willReturn(library)
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
      given(libraryService.addToLibrary(any(), any())).will {
        library.games.add(id1)
      }
      val map = HashMap<String, String>()
      map["gameId"] = id1
      requestBuilder.runPutRequest("$endpoint/$id2/addToLibrary", Gson().toJson(map))
        .andExpect(status().isOk)
      Assertions.assertTrue(library.games.contains(id1))
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
      given(libraryService.delete(any())).will {
        libraries.remove(library1)
      }
      requestBuilder.runDeleteRequest("$endpoint/$id1")
        .andExpect(status().isOk)
        .andExpect(content().string(containsString("Successfully deleted library")))
      Assertions.assertTrue(libraries.isEmpty())
    }
  }

  @Nested
  @DisplayName("Tests for getGameFromLibrary")
  inner class GetGameFromLibrary {
    @Test
    fun shouldGetGameFromLibrarySuccessfully() {
      val library = Library("id1", "Backlog", ArrayList())
      library.games.add("gameId1")
      val game = GameInstance("gameId1", "19", "Fire Emblem: Engage",
        arrayListOf("Nintendo Switch"), arrayListOf("RPG"), arrayListOf("Fire Emblem"), arrayListOf("Nintendo", "Intelligent Systems"),
        arrayListOf("January 20 2023"), arrayListOf(""), "Shine on, Emblem of Beginnings!")
      given(libraryService.getGameFromLibrary(any(), any())).willReturn(game)
      endpoint += "$id1/games/${"gameId1"}"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.name", equalTo("Fire Emblem: Engage")))
        .andExpect(jsonPath("$.igdbId", equalTo("19")))
        .andExpect(jsonPath("$.platforms[0]", equalTo("Nintendo Switch")))
        .andExpect(jsonPath("$.genres[0]", equalTo("RPG")))
        .andExpect(jsonPath("$.universes[0]", equalTo("Fire Emblem")))
        .andExpect(jsonPath("$.companies[0]", equalTo("Nintendo")))
        .andExpect(jsonPath("$.companies[1]", equalTo("Intelligent Systems")))
        .andExpect(jsonPath("$.releaseDate[0]", equalTo("January 20 2023")))
        .andExpect(jsonPath("$.images[0]", equalTo("")))
        .andExpect(jsonPath("$.id", equalTo("gameId1")))
    }

    @Test
    fun shouldGetNotFoundForGameInLibrary() {
      val library = Library("id1", "Backlog", ArrayList())
      given(libraryService.getGameFromLibrary(any(), any())).willReturn(null)
      endpoint += "$id1/games/${"gameId1"}"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isNotFound)
        .andExpect(jsonPath("$").doesNotExist())
    }
  }

  fun requestToString(request: LibraryRequest): String {
    return Gson().toJson(request)
  }


}