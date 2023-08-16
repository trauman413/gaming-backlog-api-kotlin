package com.gamingbacklog.api.gamingbacklogapi.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(
  @Id
  val id: String = ObjectId.get().toString(),
  var username: String,
  var password: String,
  var email: String
)
