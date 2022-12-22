package com.gamingbacklog.api.gamingbacklogapi.models.igdb

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@NoArgsConstructor
@AllArgsConstructor
data class FieldInfo (
  val id: Long,
  val name: String
) {}


@AllArgsConstructor
@NoArgsConstructor
data class CompanyFieldInfo (
  val id: Long,
  val company: FieldInfo
)

@AllArgsConstructor
@NoArgsConstructor
data class ArtworkInfo (
  val id: Long,
  val url: String
)

@AllArgsConstructor
@NoArgsConstructor
data class ReleaseDate (
  val id: Long,
  val human: String
)