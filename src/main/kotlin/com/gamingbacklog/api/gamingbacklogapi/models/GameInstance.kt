package com.gamingbacklog.api.gamingbacklogapi.models

import lombok.Data
import lombok.NoArgsConstructor
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
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
  val genres: List<String>,
  val universes: List<String>? = null,
  val companies: List<String>,
  val releaseDate: List<String>,
  val images: List<String>,
  val summary: String,

  // user-custom data
  var rating: Int? = null,
  var review: String? = null,
  var ranking: String? = null,
  var yearPlayed: Int? = null,
  var yearReceived: Int? = null,
  var notes: String? = null,
  var platformsOwnedOn: List<String>? = null,
  var dateAdded: LocalDate? = null
  )