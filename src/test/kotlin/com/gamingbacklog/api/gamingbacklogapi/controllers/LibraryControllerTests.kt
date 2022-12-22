package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import com.gamingbacklog.api.gamingbacklogapi.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import com.google.gson.Gson
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.equalToObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@AutoConfigureMockMvc
@Import(LibraryController::class)
class LibraryControllerTests() {
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
        /** Tests returning a 200 response for returning all libraries */
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
        /** Tests returning 204 for getting all libraries */
        @Test
        fun shouldReturnNoValues() {
            given(libraryService.getAll()).willReturn(emptyList())
            requestBuilder.runGetRequest(endpoint)
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$", equalTo(emptyList<Library>())))
        }
    }

    @Nested
    @DisplayName("Tests for getSingleLibrary")
    inner class GetSingleLibrary {
      // returns 200
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
      // returns 404
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
      // creates library successfully
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
    // TODO: write these tests when master library functionality is complete
    inner class AddToLibrary {
      // adds to library successfully
      // adds duplicate
    }

    @Nested
    @DisplayName("Tests for deleteLibrary")
    inner class DeleteLibrary {
      // successfully deletes
    }

  @Nested
  @DisplayName("Tests for getGameFromLibrary")
  inner class GetGameFromLibrary {
    // successfully fetches game
    // no game found
  }

  fun requestToString(request: LibraryRequest): String {
    println(request.toString())
    return Gson().toJson(request)
  }


}