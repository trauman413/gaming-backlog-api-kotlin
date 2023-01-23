package com.gamingbacklog.api.gamingbacklogapi.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("gameInstances")
@NoArgsConstructor
@Data
@AllArgsConstructor
data class GameInstance(
  @Id
  val id: String = ObjectId.get().toString(),

  // igdb-specific data
  val igdbId: Long,
  val name: String,
  val platforms: List<String>,
  val genres: List<String>, // TODO: make these enums potentially
  val universes: List<String>,
  val companies: List<String>,
  val releaseDate: Date,
  val images: List<String>,

  // user-custom data
  val rating: Int?,
  val review: String?,
  val ranking: String?,
  val yearPlayed: Int?,
  val yearReceived: Int?,
  val notes: String?,
  val platformsOwnedOn: List<String>?
  )