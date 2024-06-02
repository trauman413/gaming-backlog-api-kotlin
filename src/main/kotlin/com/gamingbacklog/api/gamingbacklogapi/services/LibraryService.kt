package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.enums.LibraryStatus
import com.gamingbacklog.api.gamingbacklogapi.models.enums.MultiLibraryStatus
import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.Request
import com.gamingbacklog.api.gamingbacklogapi.models.responses.GameResponse
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
import com.gamingbacklog.api.gamingbacklogapi.models.results.LibraryResult
import com.gamingbacklog.api.gamingbacklogapi.models.results.MultiLibraryResult
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.util.ArrayList

// TODO: this class needs better error handling, will be part of task GB-55
@Service
class LibraryService (
  private val libraryRepository: LibraryRepository,
  private val gameService: GameService,
  private val gameInstanceService: GameInstanceService
) : IService<Library> {

  override fun getAll(): List<Library> {
    return libraryRepository.findAll()
  }

  override fun getSingle(id: String): Library? {
    return libraryRepository.findOneById(ObjectId(id))
  }

  override fun getSingleByName(name: String): Library? {
    return libraryRepository.findByName(name)

  }

  override fun create(request: Request): Library {
    val libraryRequest = request as LibraryRequest
    val games: ArrayList<String> = if (libraryRequest.games == null) ArrayList<String>() else libraryRequest.games!!
    val library = Library(
      name = libraryRequest.name,
      games = games
    )
    libraryRepository.save(library)
    return library
  }

  override fun delete(id: String) {
    libraryRepository.deleteById(id)
  }

  override fun update(model: Library) {
    libraryRepository.save(model)
  }

  fun addToLibrary(libraryId: String, gameId: String): LibraryResult {
    if (!isValidGameInstanceId(gameId)) {
      return LibraryResult(null, LibraryStatus.GAME_DOES_NOT_EXIST)
    }
    val library = getSingle(libraryId)
    if (library != null) {
      if (library.games.contains(gameId)) {
        return LibraryResult(library, LibraryStatus.GAME_DUPLICATE_FOUND)
      }
      library.games.add(gameId)
      update(library)
      return LibraryResult(library, LibraryStatus.SUCCESS)
    }
    return LibraryResult(null, LibraryStatus.LIBRARY_DOES_NOT_EXIST)
  }

  fun addToLibraries(libraryIds: List<String>?, gameId: String): MultiLibraryResult {
    val failedLibraries = mutableListOf<LibraryResult>()
    if (libraryIds.isNullOrEmpty()) {
      return MultiLibraryResult(emptyList(), MultiLibraryStatus.ALL_LIBRARIES_DO_NOT_EXIST)
    }
    if (!isValidGameInstanceId(gameId)) {
      return MultiLibraryResult(emptyList(), MultiLibraryStatus.GAME_DOES_NOT_EXIST)
    }
    libraryIds.forEach { libraryId ->
      val result = addToLibrary(libraryId, gameId)
      if (result.libraryStatus != LibraryStatus.SUCCESS) {
        failedLibraries.add(result)
      }
    }
    if (failedLibraries.isNotEmpty()) {
      return MultiLibraryResult(failedLibraries, MultiLibraryStatus.INDIVIDUAL_LIBRARY_ERROR)
    }
    return MultiLibraryResult(emptyList(), MultiLibraryStatus.SUCCESS)
  }

  fun updateName(id: String, name: String): LibraryResult {
    val library = libraryRepository.findOneById(ObjectId(id))
    val updatedLibrary = library?.let { Library(id, name, it.games) }
    return if (updatedLibrary == null) {
      LibraryResult(null, LibraryStatus.LIBRARY_DOES_NOT_EXIST)
    } else {
      libraryRepository.save(updatedLibrary)
      LibraryResult(updatedLibrary, LibraryStatus.SUCCESS)
    }
  }


  fun deleteGameFromLibrary(libraryId: String, gameId: String): LibraryResult {
    if (!isValidGameInstanceId(gameId)) {
      return LibraryResult(null, LibraryStatus.GAME_DOES_NOT_EXIST)
    }
    val library = getSingle(libraryId)
    if (library != null) {
      library.games.remove(gameId)
      update(library)
      return LibraryResult(library, LibraryStatus.SUCCESS)
    }
    return LibraryResult(null, LibraryStatus.LIBRARY_DOES_NOT_EXIST)
  }

  // TODO: error handling here
  fun getGameFromLibrary(libraryId: String, gameId: String): GameInstance? {
    val library = libraryRepository.findOneById(ObjectId(libraryId))
    if (library != null && !library.games.contains(gameId)) {
        return null
      }
    val game = gameService.getSingle(gameId)
    if (game == null) {
      val gameInstance = gameInstanceService.getSingle(gameId)
      return gameInstance?.name?.let { gameInstanceService.getSingleByName(it) }
    }
    return gameInstanceService.getSingleByName(game.name)
  }

  fun convertLibraryToResponse(library: Library): LibraryResponse {
    return if (library.games.size > 0) {
      val games: List<GameInstance> = library.games.map { gameId -> getGameFromLibrary(library.id, gameId)!! }
      val gameResponses = games.map { game -> GameResponse(game.id, game.name) }
      LibraryResponse(id = library.id, name = library.name, games = gameResponses)
    } else {
      LibraryResponse(id = library.id, name = library.name, games = emptyList());
    }
  }

  fun isValidGameInstanceId(gameId: String): Boolean {
    return gameInstanceService.getSingle(gameId) != null
  }

}
