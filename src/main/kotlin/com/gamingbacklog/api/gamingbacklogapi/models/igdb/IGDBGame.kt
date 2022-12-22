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
  val artworks: Array<ArtworkInfo>,
  val franchises: Array<FieldInfo>,
  val genres: Array<FieldInfo>,
  val involved_companies: Array<CompanyFieldInfo>,
  val name: String,
  val platforms: Array<FieldInfo>,
  val release_dates: Array<ReleaseDate>,
  val summary: String
) {
}