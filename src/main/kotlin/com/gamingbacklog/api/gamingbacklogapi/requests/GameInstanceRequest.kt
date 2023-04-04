package com.gamingbacklog.api.gamingbacklogapi.requests

import java.util.*

class GameInstanceRequest(
  var igdbId: String?,

  var rating: Int?,
  var review: String?,
  var ranking: String?,
  var yearPlayed: Int?,
  var yearReceived: Int?,
  var notes: String?,
  var platformsOwnedOn: List<String>?
) : Request() {
}