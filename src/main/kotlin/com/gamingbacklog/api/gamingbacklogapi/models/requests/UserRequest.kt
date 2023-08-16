package com.gamingbacklog.api.gamingbacklogapi.models.requests

class UserRequest(
  var username: String,
  var password: String,
  var email: String
): Request()