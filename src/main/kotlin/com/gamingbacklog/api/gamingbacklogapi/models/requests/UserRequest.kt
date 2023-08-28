package com.gamingbacklog.api.gamingbacklogapi.models.requests

class UserRequest(
  var displayName: String,
  var password: String,
  var email: String
): Request()