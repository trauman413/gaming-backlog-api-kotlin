package com.gamingbacklog.api.gamingbacklogapi.requests

class LibraryRequest (
  var name: String,
  var games: ArrayList<String>?
) : Request() {
}