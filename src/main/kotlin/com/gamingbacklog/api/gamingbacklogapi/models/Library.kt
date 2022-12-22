package com.gamingbacklog.api.gamingbacklogapi.models

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("libraries")
@NoArgsConstructor
@Data
@AllArgsConstructor
data class Library (
  @Id
  val id: String = ObjectId.get().toString(),
  val name: String,
  val games: ArrayList<String>
)