package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameInstanceRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.requests.GameInstanceRequest
import com.gamingbacklog.api.gamingbacklogapi.requests.Request
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class GameInstanceService(
  private val gameInstanceRepository: GameInstanceRepository,
  private val gameRepository: GameRepository
) : IService<GameInstance> {
  override fun getAll(): List<GameInstance> {
    return gameInstanceRepository.findAll()
  }

  override fun getSingle(id: String): GameInstance {
    return gameInstanceRepository.findOneById(ObjectId(id))
  }

  override fun create(request: Request): GameInstance {
    /**
     * 1) Get igdbID
     * 2) Search GameDB for igdbID
     * 3) If it is there, get info from there
     * 4) If it is not there, call igdb API and get info and add to GameDB
     * 5) Use info to populate new GameInstance
     */
//    val gameInstanceRequest = request as GameInstanceRequest
//    val igdbId = gameInstanceRequest.igdbId
//    val game = gameRepository.findByIGDBId(igdbId)
//    return GameInstance("")
    TODO("Not yet implemented")


  }

  override fun delete(id: String) {
    gameInstanceRepository.deleteById(id)
  }

  override fun update(model: GameInstance) {
    gameInstanceRepository.save(model)
  }
}