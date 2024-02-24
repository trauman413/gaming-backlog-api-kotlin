package com.gamingbacklog.api.gamingbacklogapi.unit.services

import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.enums.LibraryStatus
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import com.gamingbacklog.api.gamingbacklogapi.services.GameService
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.given
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

  private val testGameInstance = GameInstance(
    id = gameId1,
    igdbId = "",
    name = "",
    companies = listOf(),
    platforms = listOf(),
    genres = listOf(),
    releaseDate = listOf(),
    summary = "",
    images = listOf()
  )


  fun mockSave(library: Library) {
    given(libraryRepository.save(library)).willAnswer {
      mockLibraryDb[library.id] = library
      library
    }
  }

  private fun mockGetOne(libraryId: String) {
    given(libraryRepository.findOneById(ObjectId(libraryId))).willReturn(mockLibraryDb[libraryId])
  }

  @AfterEach
  fun teardown() {
    mockLibraryDb.clear()
  }

  fun saveMockLibraryDB(libraryId: String, libraryName: String, gameIds: ArrayList<String>) {
    mockLibraryDb[libraryId] = Library(libraryId, libraryName, gameIds)
    mockGetOne(libraryId)
    mockLibraryDb[libraryId]?.let { mockSave(it) }
  }

  @Nested
  @DisplayName("Tests for createLibrary")
  inner class CreateLibrary {

    private fun testCreateLibraryUtil(libraryId: String, libraryName: String, gameIds: ArrayList<String>) {
      val library = Library(libraryId, libraryName, gameIds)
      val request = LibraryRequest(libraryName, gameIds)
      mockSave(library)
      val result = libraryService.create(request)
      assertEquals(library.name, result.name)
      assertEquals(library.games, result.games)

    }
    @Test
    fun shouldCreateLibraryWithNoGames() {
      testCreateLibraryUtil(libraryId, "Backlog", arrayListOf())
    }

    @Test
    fun shouldCreateLibraryWithGames() {
      testCreateLibraryUtil(libraryId, "Backlog", arrayListOf(gameId1))
    }
  }

  @Nested
  @DisplayName("Tests for addToLibrary")
  inner class AddToLibrary {
    @Test
    fun shouldAddToLibrary() {
      saveMockLibraryDB(libraryId, "Backlog", arrayListOf())
      given(gameInstanceService.getSingle(gameId1)).willReturn(testGameInstance)
      val result = libraryService.addToLibrary(libraryId, gameId1)
      assertEquals(mockLibraryDb[libraryId], result.library)
      assertEquals(LibraryStatus.SUCCESS, result.libraryStatus)
    }

    @Test
    fun shouldNotAddToLibraryIfNoLibraryIDFound() {
      given(gameInstanceService.getSingle(gameId1)).willReturn(testGameInstance)
      val result = libraryService.addToLibrary(libraryId, gameId1)
      assertNull(result.library)
      assertEquals(LibraryStatus.LIBRARY_DOES_NOT_EXIST, result.libraryStatus)
    }

    @Test
    fun shouldNotAddDuplicateID() {
      given(gameInstanceService.getSingle(gameId1)).willReturn(testGameInstance)
      saveMockLibraryDB(libraryId, "Backlog", arrayListOf())
      libraryService.addToLibrary(libraryId, gameId1)
      // run duplicate
      val result = libraryService.addToLibrary(libraryId, gameId1)
      assertEquals(mockLibraryDb[libraryId], result.library)
      assertEquals(LibraryStatus.GAME_DUPLICATE_FOUND, result.libraryStatus)
    }

    @Test
    fun shouldHaveGameDoesNotExistError() {
      given(gameInstanceService.getSingle(gameId1)).willReturn(null)
      val result = libraryService.addToLibrary(libraryId, gameId1)
      assertNull(result.library)
      assertEquals(LibraryStatus.GAME_DOES_NOT_EXIST, result.libraryStatus)
    }
  }

  @Nested
  @DisplayName("Tests for deleteGameFromLibrary")
  inner class DeleteGameFromLibrary {
    @Test
    fun shouldDeleteGameFromLibrary() {
      given(gameInstanceService.getSingle(gameId1)).willReturn(testGameInstance)
      saveMockLibraryDB(libraryId, "Backlog", arrayListOf(gameId1))
      val result = libraryService.deleteGameFromLibrary(libraryId, gameId1)
      assertEquals(mockLibraryDb[libraryId], result.library)
      assertEquals(LibraryStatus.SUCCESS, result.libraryStatus)
    }

    @Test
    fun shouldNotDeleteIfNoLibraryIDIsFound() {
      given(gameInstanceService.getSingle(gameId1)).willReturn(testGameInstance)
      val result = libraryService.deleteGameFromLibrary(libraryId, gameId1)
      assertNull(result.library)
      assertEquals(LibraryStatus.LIBRARY_DOES_NOT_EXIST, result.libraryStatus)
    }

    @Test
    fun shouldNotDeleteIfNoGameIDIsFound() {
      given(gameInstanceService.getSingle(gameId1)).willReturn(null)
      val result = libraryService.deleteGameFromLibrary(libraryId, gameId1)
      assertNull(result.library)
      assertEquals(LibraryStatus.GAME_DOES_NOT_EXIST, result.libraryStatus)
    }
  }
}