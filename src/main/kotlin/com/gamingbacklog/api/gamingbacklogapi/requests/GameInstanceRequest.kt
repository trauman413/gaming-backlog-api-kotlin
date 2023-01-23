package com.gamingbacklog.api.gamingbacklogapi.requests

import java.util.*

class GameInstanceRequest(
  var igdbId: Long,
  var name: String,
  var platforms: List<String>,
  var genres: List<String>,
  var universes: List<String>,
  var companies: List<String>,
  var releaseDate: Date,
  var images: List<String>,

  var rating: Int?,
  var review: String?,
  var ranking: String?,
  var yearPlayed: Int?,
  var yearReceived: Int?,
  var notes: String?,
  var platformsOwnedOn: List<String>?
) : Request() {
}