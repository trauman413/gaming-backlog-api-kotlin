package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.LibraryRepository
import com.gamingbacklog.api.gamingbacklogapi.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.requests.Request
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.util.ArrayList

@Service
class LibraryService (
  private val libraryRepository: LibraryRepository,
  private val gameRepository: GameRepository
) : IService<Library> {

  override fun getAll(): List<Library> {
    return libraryRepository.findAll()
  }

  override fun getSingle(id: String): Library {
    return libraryRepository.findOneById(ObjectId(id))
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
    // todo: add to master library and possibly create new game instance
    val library = getSingle(libraryId)
    library.games.add(gameId)
    update(library)
  }

  fun getGameFromLibrary(libraryId: String, gameId: String): Game? {
    val library = libraryRepository.findOneById(ObjectId(libraryId))
    if (!library.games.contains(gameId)) {
      return null
    }
    return gameRepository.findOneById(ObjectId(gameId))
  }

}