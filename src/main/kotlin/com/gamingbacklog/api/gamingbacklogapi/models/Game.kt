package com.gamingbacklog.api.gamingbacklogapi.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("games")
@NoArgsConstructor
@Data
@AllArgsConstructor
data class Game (
  @Id
  val id: String = ObjectId.get().toString(),
  val name: String,
  val platform: String,
  val genre: String,
  val universe: String,
  val company: String

)