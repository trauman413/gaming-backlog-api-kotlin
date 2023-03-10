package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.GameResponse
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/libraries")
class LibraryController(private val libraryService: LibraryService) {
  @GetMapping("/all")
  fun getAllLibraries(): ResponseEntity<List<Library>> {
    val libraries = libraryService.getAll()
    return ResponseEntity.ok(libraries)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @GetMapping("/")
  fun getAllLibrariesWithGames(): ResponseEntity<List<LibraryResponse>> {
    val libraries = libraryService.getAll()
    // Needed for the frontend. Should refactor this and the one above + tests depending on what we need.
    val librariesWithGames: List<LibraryResponse> = libraries.map { library -> convertLibraryToResponse(library) }
    return ResponseEntity.ok(librariesWithGames)
  }

  private fun convertLibraryToResponse(library: Library): LibraryResponse {
    if (library.games.size > 0) {
      val games: List<Game> = library.games.map { gameId -> libraryService.getGameFromLibrary(library.id, gameId)!! }
      val gameResponses = games.map { game -> GameResponse(game.id, game.name) }
      return LibraryResponse(id = library.id, name = library.name, games = gameResponses)
    } else {
      // TODO: Do we want to allow empty libraries?
      return LibraryResponse(id = library.id, name = library.name, games = emptyList());
    }
  }

  @GetMapping("/{id}")
  fun getSingleLibrary(
    @PathVariable("id") id: String
  ): ResponseEntity<Library> {
    val library = libraryService.getSingle(id)
    return ResponseEntity.ok(library)
  }

  @PostMapping("/")
  fun createLibrary(
    @RequestBody libraryRequest: LibraryRequest
  ): ResponseEntity<Library> {
    val library = libraryService.create(libraryRequest)
    return ResponseEntity<Library>(library, HttpStatus.CREATED)
  }

  @PutMapping("/{libraryId}/addToLibrary")
  fun addToLibrary(
    @PathVariable("libraryId") libraryId: String,
    @RequestBody addGameToLibrary: Map<String, String>
  ): ResponseEntity<String> {
    val gameId = addGameToLibrary["gameId"] ?: throw Exception("gameId must be provided")
    libraryService.addToLibrary(libraryId, gameId)
    return ResponseEntity.ok("Successfully added game to library")
  }

  @GetMapping("/{id}/games/{gameId}")
  fun getGameFromLibrary(
    @PathVariable("id") libraryId: String,
    @PathVariable("gameId") gameId: String
  ): ResponseEntity<Game> {
    val game = libraryService.getGameFromLibrary(libraryId, gameId)
      ?: return ResponseEntity<Game>( null, HttpStatus.NOT_FOUND)
    return ResponseEntity<Game>(game, HttpStatus.OK)
  }

  @DeleteMapping("/{id}")
  fun deleteLibrary(
    @PathVariable("id") id: String
  ): ResponseEntity<String> {
    libraryService.delete(id)
    return ResponseEntity.ok("Successfully deleted library")
  }
}
