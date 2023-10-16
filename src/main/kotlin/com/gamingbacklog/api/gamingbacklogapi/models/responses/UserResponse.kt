package com.gamingbacklog.api.gamingbacklogapi.models.responses

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
  val id: String,
  val displayName: String,
  val password: String?,
  val email: String,
  val libraries: List<LibraryResponse>?
) : Response() {

  constructor(id: String, displayName: String, email: String, libraries: List<LibraryResponse>) : this(
    id = id,
    displayName = displayName,
    password = null,
    email = email,
    libraries = libraries
  )
}