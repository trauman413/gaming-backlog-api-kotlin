package com.gamingbacklog.api.gamingbacklogapi.requests

import com.gamingbacklog.api.gamingbacklogapi.models.requests.Request

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