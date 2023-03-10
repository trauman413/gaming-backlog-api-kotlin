package com.gamingbacklog.api.gamingbacklogapi.models.requests

/**
 * TODO: this will be heavily modified/refactored
 */
class GameRequest(
  var name: String,
  var platform: String?,
  var genre: String?,
  var universe: String?,
  var company: String?
) : Request() {
}
