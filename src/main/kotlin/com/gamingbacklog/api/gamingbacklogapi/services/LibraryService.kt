package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import com.gamingbacklog.api.gamingbacklogapi.models.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.Request
import com.gamingbacklog.api.gamingbacklogapi.models.responses.GameResponse
import com.gamingbacklog.api.gamingbacklogapi.models.responses.LibraryResponse
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

  fun addToLibrary(libraryId: String, gameId: String) {
    val library = getSingle(libraryId)
    if (library != null) {
      library.games.add(gameId)
      update(library)
    }
  }

  // TODO: error handling here
  fun getGameFromLibrary(libraryId: String, gameId: String): GameInstance? {
    val library = libraryRepository.findOneById(ObjectId(libraryId))
    if (library != null && !library.games.contains(gameId)) {
        return null
      }
    val game = gameService.getSingle(gameId)
    return gameInstanceService.getSingleByName(game!!.name)
  }

  fun convertLibraryToResponse(library: Library): LibraryResponse {
    if (library.games.size > 0) {
      val games: List<GameInstance> = library.games.map { gameId -> getGameFromLibrary(library.id, gameId)!! }
      val gameResponses = games.map { game -> GameResponse(game.id, game.name) }
      return LibraryResponse(id = library.id, name = library.name, games = gameResponses)
    } else {
      return LibraryResponse(id = library.id, name = library.name, games = emptyList());
    }
  }

}
