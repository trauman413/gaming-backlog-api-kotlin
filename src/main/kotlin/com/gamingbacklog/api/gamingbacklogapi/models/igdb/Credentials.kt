package com.gamingbacklog.api.gamingbacklogapi.models.igdb

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

@NoArgsConstructor
@AllArgsConstructor
data class Credentials(
  val access_token: String,
  val expires_in: String,
  val token_type: String
) {

}