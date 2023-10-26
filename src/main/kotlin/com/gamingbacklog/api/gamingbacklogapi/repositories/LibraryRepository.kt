package com.gamingbacklog.api.gamingbacklogapi.repositories

import com.gamingbacklog.api.gamingbacklogapi.models.Library
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface LibraryRepository : MongoRepository<Library, String> {
  fun findOneById(id: ObjectId): Library?
  fun findByName(name: String): Library?
  fun findByIdIn(ids: List<ObjectId>): Array<Library>?
  override fun deleteAll()

}