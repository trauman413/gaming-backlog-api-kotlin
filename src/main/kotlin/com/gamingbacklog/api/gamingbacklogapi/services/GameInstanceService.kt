package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameInstanceRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.requests.GameInstanceRequest
import com.gamingbacklog.api.gamingbacklogapi.requests.GameRequest
import com.gamingbacklog.api.gamingbacklogapi.requests.Request
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class GameInstanceService(
  private val gameInstanceRepository: GameInstanceRepository,
  private val gameService: GameService
) : IService<GameInstance> {
  override fun getAll(): List<GameInstance> {
    return gameInstanceRepository.findAll()
  }

  override fun getSingle(id: String): GameInstance? {
    return gameInstanceRepository.findOneById(ObjectId(id))
  }

  override fun create(request: Request): GameInstance? {
    val gameInstanceRequest = request as GameInstanceRequest
    val igdbId = gameInstanceRequest.igdbId!! // TODO: GB-55 error handling
    var game = gameService.getByIGDBId(igdbId)
    if (game != null) {
      val gameInstance = GameInstance(
        igdbId = igdbId,
        name = game.name,
        platforms = game.platforms,
        genres = game.genres,
        universes = game.universes,
        companies = game.companies,
        releaseDate = game.releaseDate,
        images = game.images
      )
      gameInstanceRepository.save(gameInstance)
      return gameInstance
    }
    // if no game is found, call IGDB Client
    game = gameService.create(GameRequest(igdbId))
    if (game == null) {
      return null
    }
    val gameInstance = GameInstance(
      igdbId = igdbId,
      name = game.name,
      platforms = game.platforms,
      genres = game.genres,
      universes = game.universes,
      companies = game.companies,
      releaseDate = game.releaseDate,
      images = game.images
    )
    gameInstanceRepository.save(gameInstance)
    return gameInstance
  }

  override fun delete(id: String) {
    gameInstanceRepository.deleteById(id)
  }

  override fun update(model: GameInstance) {
    gameInstanceRepository.save(model)
  }

  fun updateWithCustomFields(gameId: String, request: GameInstanceRequest) {
    val gameInstance = gameInstanceRepository.findOneById(ObjectId(gameId))
    if (request.rating != null) gameInstance?.rating = request.rating
    if (request.review != null) gameInstance?.review = request.review
    if (request.ranking != null) gameInstance?.ranking = request.ranking
    if (request.yearPlayed != null) gameInstance?.yearPlayed = request.yearPlayed
    if (request.yearReceived != null) gameInstance?.yearReceived = request.yearReceived
    if (request.notes != null) gameInstance?.notes = request.notes
    if (request.platformsOwnedOn != null) gameInstance?.platformsOwnedOn = request.platformsOwnedOn
    gameInstance?.dateAdded = LocalDate.now()
    update(gameInstance!!)
  }


}