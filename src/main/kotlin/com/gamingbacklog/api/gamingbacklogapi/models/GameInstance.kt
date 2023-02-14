package com.gamingbacklog.api.gamingbacklogapi.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("gameInstances")
@NoArgsConstructor
@Data
data class GameInstance(
  @Id
  val id: String = ObjectId.get().toString(),

  // igdb-specific data
  @Field
  val igdbId: String,
  val name: String,
  val platforms: List<String>,
  val genres: List<String>, // TODO: make these enums potentially
  val universes: List<String>,
  val companies: List<String>,
  val releaseDate: Date,
  val images: List<String>,

  // user-custom data
  var rating: Int? = null,
  var review: String? = null,
  var ranking: String? = null,
  val yearPlayed: Int? = null,
  var yearReceived: Int? = null,
  var notes: String? = null,
  var platformsOwnedOn: List<String>? = null
  )