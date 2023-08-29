package com.gamingbacklog.api.gamingbacklogapi.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("games")
data class Game(
  @Id
  val id: String = ObjectId.get().toString(),
  val igdbId: String,
  val name: String,
  val platforms: List<String>,
  val genres: List<String>,
  val universes: List<String>? = null,
  val companies: List<String>,
  val releaseDate: List<String>,
  val images: List<String>,
  val summary: String

  )