package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.enums.LibraryStatus
import com.gamingbacklog.api.gamingbacklogapi.models.enums.MultiLibraryStatus
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.UpdateLibraryGamesRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
import com.gamingbacklog.api.gamingbacklogapi.models.results.LibraryResult
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/libraries")
class LibraryController(private val libraryService: LibraryService) {

  @GetMapping("/withGames")
  fun getAllLibraries(): ResponseEntity<List<Library>> {
    val libraries = libraryService.getAll()
    return ResponseEntity.ok(libraries)
  }

  @GetMapping("/{id}")
  fun getSingleLibrary(
    @PathVariable("id") id: String
  ): ResponseEntity<Library> {
    val library = libraryService.getSingle(id)
    return ResponseEntity.ok(library)
  }

  @GetMapping("/{id}/withGames")
  fun getSingleLibraryWithGames(
    @PathVariable("id") id: String
  ): ResponseEntity<LibraryResponse> {
    val library = libraryService.getSingle(id) ?: return ResponseEntity<LibraryResponse>( null, HttpStatus.NOT_FOUND)
    val libraryResponse = libraryService.convertLibraryToResponse(library)
    return ResponseEntity.ok(libraryResponse)
  }

  @PostMapping("/")
  fun createLibrary(
    @RequestBody libraryRequest: LibraryRequest
  ): ResponseEntity<Library> {
    val library = libraryService.create(libraryRequest)
    return ResponseEntity<Library>(library, HttpStatus.CREATED)
  }

  @PostMapping("/{libraryId}/games")
  fun addToLibrary(
    @PathVariable("libraryId") libraryId: String,
    @RequestBody addGameToLibrary: UpdateLibraryGamesRequest
  ): ResponseEntity<LibraryResponse> {
    val libraryResult = libraryService.addToLibrary(libraryId, addGameToLibrary.gameId)
    return when (libraryResult.libraryStatus) {
      LibraryStatus.SUCCESS -> ResponseEntity.ok(libraryResult.library?.let { libraryService.convertLibraryToResponse(it) })
      LibraryStatus.LIBRARY_DOES_NOT_EXIST, LibraryStatus.GAME_DOES_NOT_EXIST -> ResponseEntity<LibraryResponse>(HttpStatus.BAD_REQUEST)
      LibraryStatus.GAME_DUPLICATE_FOUND -> ResponseEntity<LibraryResponse>(HttpStatus.BAD_REQUEST)
    }
  }

  @PostMapping("/games")
  fun addToLibraries(
    @RequestBody addGameToLibrary: UpdateLibraryGamesRequest,
  ): ResponseEntity<List<LibraryResult>> {
    val libraryResult = libraryService.addToLibraries(addGameToLibrary.libraryIds, addGameToLibrary.gameId)
    return when (libraryResult.libraryStatus) {
      MultiLibraryStatus.SUCCESS -> ResponseEntity.ok(null)
      MultiLibraryStatus.EMPTY_LIBRARIES, MultiLibraryStatus.GAME_DOES_NOT_EXIST -> ResponseEntity<List<LibraryResult>>(HttpStatus.NOT_FOUND)
      MultiLibraryStatus.INDIVIDUAL_LIBRARY_ERROR -> ResponseEntity(libraryResult.libraries, HttpStatus.ACCEPTED)
    }
  }

  @GetMapping("/{id}/games/{gameId}")
  fun getGameFromLibrary(
    @PathVariable("id") libraryId: String,
    @PathVariable("gameId") gameId: String
  ): ResponseEntity<GameInstance> {
    val game = libraryService.getGameFromLibrary(libraryId, gameId)
      ?: return ResponseEntity<GameInstance>( null, HttpStatus.NOT_FOUND)
    return ResponseEntity<GameInstance>(game, HttpStatus.OK)
  }

  @DeleteMapping("/{id}/games")
  fun deleteGameFromLibrary(
    @PathVariable("id") libraryId: String,
    @RequestBody removeGameFromLibrary: UpdateLibraryGamesRequest
  ): ResponseEntity<LibraryResponse> {
    val libraryResult = libraryService.deleteGameFromLibrary(libraryId, removeGameFromLibrary.gameId)
    return when (libraryResult.libraryStatus) {
      LibraryStatus.SUCCESS -> ResponseEntity.ok(libraryResult.library?.let { libraryService.convertLibraryToResponse(it) })
      LibraryStatus.LIBRARY_DOES_NOT_EXIST, LibraryStatus.GAME_DOES_NOT_EXIST -> ResponseEntity<LibraryResponse>(HttpStatus.NOT_FOUND)
      else -> ResponseEntity<LibraryResponse>(HttpStatus.INTERNAL_SERVER_ERROR) // should never reach this branch
    }
  }

  @PatchMapping("/{id}")
  fun updateLibraryName(
          @PathVariable("id") id: String,
          @RequestBody libraryRequest: LibraryRequest
  ): ResponseEntity<LibraryResponse> {
    val libraryResult = libraryService.updateName(id, libraryRequest.name)
    return when (libraryResult.libraryStatus) {
      LibraryStatus.SUCCESS -> ResponseEntity.ok(libraryResult.library?.let { libraryService.convertLibraryToResponse(it) })
      LibraryStatus.LIBRARY_DOES_NOT_EXIST -> ResponseEntity<LibraryResponse>(HttpStatus.NOT_FOUND)
      else -> ResponseEntity<LibraryResponse>(HttpStatus.INTERNAL_SERVER_ERROR) // should never reach this branch
    }
  }

  @DeleteMapping("/{id}")
  fun deleteLibrary(
    @PathVariable("id") id: String
  ): ResponseEntity<String> {
    libraryService.delete(id)
    return ResponseEntity.ok("Successfully deleted library")
  }
}
