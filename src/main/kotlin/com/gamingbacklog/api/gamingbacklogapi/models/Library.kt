package com.gamingbacklog.api.gamingbacklogapi.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("libraries")
data class Library (
  @Id
  val id: String = ObjectId.get().toString(),
  val name: String,
  val games: ArrayList<String>
)