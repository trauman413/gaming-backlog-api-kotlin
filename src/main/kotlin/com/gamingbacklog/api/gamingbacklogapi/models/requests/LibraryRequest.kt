package com.gamingbacklog.api.gamingbacklogapi.models.requests

class LibraryRequest (
  var name: String,
  var games: ArrayList<String>?
) : Request() {
}
