package com.gamingbacklog.api.gamingbacklogapi.repositories

import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface GameInstanceRepository: MongoRepository<GameInstance, String> {
  fun findOneById(id: ObjectId): GameInstance?

  fun findByName(name: String): GameInstance?

  override fun deleteAll()
}