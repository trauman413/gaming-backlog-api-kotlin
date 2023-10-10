package com.gamingbacklog.api.gamingbacklogapi.repositories

import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.models.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, String> {

  fun findOneById(id: ObjectId): User?

  fun findByDisplayName(displayName: String): User?

  fun findByEmail(email: String): User?

  override fun deleteAll()
}