package com.gamingbacklog.api.gamingbacklogapi.repositories

import com.gamingbacklog.api.gamingbacklogapi.models.Game
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface GameRepository: MongoRepository<Game, String> {
  fun findOneById(id: ObjectId): Game?
  fun findByName(name: String): Game?
  fun findByigdbId(igdbId: String): Game?
  override fun deleteAll()
}