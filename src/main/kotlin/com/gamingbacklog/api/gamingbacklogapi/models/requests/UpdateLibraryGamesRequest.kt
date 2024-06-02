package com.gamingbacklog.api.gamingbacklogapi.models.requests

class UpdateLibraryGamesRequest(
  var gameId: String,
  var libraryIds: List<String>? = null
) : Request() {
}