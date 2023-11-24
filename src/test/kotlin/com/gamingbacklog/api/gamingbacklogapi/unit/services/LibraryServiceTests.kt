package com.gamingbacklog.api.gamingbacklogapi.unit.services

import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import com.gamingbacklog.api.gamingbacklogapi.services.GameService
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@Import(LibraryService::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class LibraryServiceTests {

  private val libraryRepository = Mockito.mock(LibraryRepository::class.java)
  private val gameService = Mockito.mock(GameService::class.java)
  private val gameInstanceService = Mockito.mock(GameInstanceService::class.java)
  private val libraryService = LibraryService(libraryRepository, gameService, gameInstanceService)

  private val mockLibraryDb = HashMap<String, Library>()

  private val libraryId = "70b664a416135a6e967fadc6"
  private val gameId1 = "81c775b527246b7f078gbde7"


  fun mockSave(library: Library) {
    given(libraryRepository.save(library)).willAnswer {
      mockLibraryDb[library.id] = library
      library
    }
  }

  fun mockGetOne(libraryId: String) {
    given(libraryRepository.findOneById(ObjectId(libraryId))).willReturn(mockLibraryDb[libraryId])
  }

  @AfterEach
  fun teardown() {
    mockLibraryDb.clear()
  }

  @Nested
  @DisplayName("Tests for createLibrary")
  inner class CreateLibrary {
    @Test
    fun shouldCreateLibraryWithNoGames() {
      val library = Library(libraryId, "Backlog", arrayListOf())
      mockSave(library)
      val request = LibraryRequest("Backlog", arrayListOf())
      val result = libraryService.create(request)
      assertEquals(library.name, result.name)
      assertEquals(library.games, result.games)
    }

    @Test
    fun shouldCreateLibraryWithGames() {
      val library = Library(libraryId, "Backlog", arrayListOf(gameId1))
      mockSave(library)
      val request = LibraryRequest("Backlog", arrayListOf(gameId1))
      val result = libraryService.create(request)
      assertEquals(library.name, result.name)
      assertEquals(library.games, result.games)
    }
  }

  @Nested
  @DisplayName("Tests for addToLibrary")
  inner class AddToLibrary {
    @Test
    fun shouldAddToLibrary() {
      mockLibraryDb[libraryId] = Library(libraryId, "Backlog", arrayListOf())
      mockGetOne(libraryId)
      mockLibraryDb[libraryId]?.games?.add(gameId1)
      mockLibraryDb[libraryId]?.let { mockSave(it) }
      val result = libraryService.addToLibrary(libraryId, gameId1)
      assertEquals(mockLibraryDb[libraryId], result)
    }

    @Test
    fun shouldAddToLibraryNoIDFound() {
      val result = libraryService.addToLibrary(libraryId, gameId1)
      assertEquals(null, result)
    }
  }

  @Nested
  @DisplayName("Tests for deleteGameFromLibrary")
  inner class DeleteGameFromLibrary {
    @Test
    fun shouldDeleteGameFromLibrary() {
      mockLibraryDb[libraryId] = Library(libraryId, "Backlog", arrayListOf(gameId1))
      mockGetOne(libraryId)
      mockLibraryDb[libraryId]?.games?.remove(gameId1)
      mockLibraryDb[libraryId]?.let { mockSave(it) }
      val result = libraryService.deleteGameFromLibrary(libraryId, gameId1)
      assertEquals(mockLibraryDb[libraryId], result)
    }

    @Test
    fun shouldNotDeleteIfNoIDIsFound() {
      val result = libraryService.deleteGameFromLibrary(libraryId, gameId1)
      assertEquals(null, result)
    }

  }
}