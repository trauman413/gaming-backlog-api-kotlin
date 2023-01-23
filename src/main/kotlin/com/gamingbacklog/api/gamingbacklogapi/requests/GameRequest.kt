package com.gamingbacklog.api.gamingbacklogapi.requests

/**
 * TODO: this will be heavily modified/refactored
 */
class GameRequest(
  var name: String,
  var platform: List<String>?,
  var genre: List<String>?,
  var universe: List<String>?,
  var company: List<String>?
) : Request() {
}