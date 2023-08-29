package com.gamingbacklog.api.gamingbacklogapi.models.requests

class UserRequest(
  val displayName: String?,
  val password: String?,
  val email: String?
): Request()