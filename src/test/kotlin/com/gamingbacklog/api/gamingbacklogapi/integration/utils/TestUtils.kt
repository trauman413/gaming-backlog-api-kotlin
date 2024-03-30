package com.gamingbacklog.api.gamingbacklogapi.integration.utils

import com.gamingbacklog.api.gamingbacklogapi.models.requests.Request
import com.gamingbacklog.api.gamingbacklogapi.requests.GameInstanceRequest
import com.google.gson.Gson

object TestUtils {
  fun requestToString(request: Request): String {
    return Gson().toJson(request)
  }
}