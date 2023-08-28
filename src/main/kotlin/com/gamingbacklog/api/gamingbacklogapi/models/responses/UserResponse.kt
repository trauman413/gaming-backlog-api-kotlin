package com.gamingbacklog.api.gamingbacklogapi.models.responses

data class UserResponse(
  val id: String,
  val displayName: String,
  val email: String
) : Response() {
}