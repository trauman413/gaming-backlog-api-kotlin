package com.gamingbacklog.api.gamingbacklogapi.models.igdb

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

/**
 * Temporary class to model response from IGDB
 */
@NoArgsConstructor
@AllArgsConstructor
data class IGDBGame(
  val id: Long,
  val artworks: List<ArtworkInfo>,
  val franchises: List<FieldInfo>,
  val genres: List<FieldInfo>,
  val involved_companies: List<CompanyFieldInfo>,
  val name: String,
  val platforms: List<FieldInfo>,
  val release_dates: List<ReleaseDate>,
  val summary: String
) {
}