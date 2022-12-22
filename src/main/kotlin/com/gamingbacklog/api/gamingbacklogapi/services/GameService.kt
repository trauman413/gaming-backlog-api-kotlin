package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.requests.GameRequest
import com.gamingbacklog.api.gamingbacklogapi.requests.Request
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class GameService(
  private val gameRepository: GameRepository
) : IService<Game> {
  override fun getAll(): List<Game> {
    return gameRepository.findAll()
  }

  override fun getSingle(id: String): Game {
    return gameRepository.findOneById(ObjectId(id))
  }

  override fun create(request: Request): Game {
    val gameRequest = request as GameRequest
    val platform: String = if (gameRequest.platform == null) "" else gameRequest.platform!!
    val genre: String = if (gameRequest.genre == null) "" else gameRequest.genre!!
    val universe: String = if (gameRequest.universe == null) "" else gameRequest.universe!!
    val company: String = if (gameRequest.company == null) "" else gameRequest.company!!
    val game = Game(
      name = gameRequest.name,
      platform = platform,
      genre = genre,
      universe = universe,
      company = company
    )
    gameRepository.save(game)
    return game
  }

  override fun delete(id: String) {
    gameRepository.deleteById(id)
  }

  override fun update(model: Game) {
    gameRepository.save(model)
  }
}