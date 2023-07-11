package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
import com.gamingbacklog.api.gamingbacklogapi.services.LibraryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/libraries")
class LibraryController(private val libraryService: LibraryService) {
  @GetMapping("/")
  fun getAllLibraries(): ResponseEntity<List<Library>> {
    val libraries = libraryService.getAll()
    return ResponseEntity.ok(libraries)
  }

  @CrossOrigin(origins = ["http://localhost:3000"])
  @GetMapping("/withGames")
  fun getAllLibrariesWithGames(): ResponseEntity<List<LibraryResponse>> {
    val libraries = libraryService.getAll()
    // Needed for the frontend. Should refactor this and the one above + tests depending on what we need.
    val librariesWithGames: List<LibraryResponse> = libraries.map { library -> libraryService.convertLibraryToResponse(library) }
    return ResponseEntity.ok(librariesWithGames)
  }

  @CrossOrigin(origins = ["http://localhost:3000", "http://localhost:3000/libraries"])
  @GetMapping("/{id}")
  fun getSingleLibrary(
    @PathVariable("id") id: String
  ): ResponseEntity<Library> {
    val library = libraryService.getSingle(id)
    return ResponseEntity.ok(library)
  }

  @CrossOrigin(origins = ["http://localhost:3000", "http://localhost:3000/libraries"])
  @GetMapping("/{id}/withGames")
  fun getSingleLibraryWithGames(
    @PathVariable("id") id: String
  ): ResponseEntity<LibraryResponse> {
    val library = libraryService.getSingle(id) ?: return ResponseEntity<LibraryResponse>( null, HttpStatus.NOT_FOUND)
    val libraryResponse = libraryService.convertLibraryToResponse(library)
    return ResponseEntity.ok(libraryResponse)
  }

  @CrossOrigin(origins = ["http://localhost:3000", "http://localhost:3000/libraries"])
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
  ): ResponseEntity<GameInstance> {
    val game = libraryService.getGameFromLibrary(libraryId, gameId)
      ?: return ResponseEntity<GameInstance>( null, HttpStatus.NOT_FOUND)
    return ResponseEntity<GameInstance>(game, HttpStatus.OK)
  }

  @DeleteMapping("/{id}")
  fun deleteLibrary(
    @PathVariable("id") id: String
  ): ResponseEntity<String> {
    libraryService.delete(id)
    return ResponseEntity.ok("Successfully deleted library")
  }
}
