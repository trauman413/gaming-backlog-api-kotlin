package com.gamingbacklog.api.gamingbacklogapi.unit.controllers

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*


class RequestBuilder(private val mockMvc: MockMvc) {
    fun runGetRequest(url: String): ResultActions {
        return mockMvc.perform(get(url))
    }

  fun runPostRequest(url: String, request: String): ResultActions {
    return mockMvc.perform(post(url)
      .contentType(MediaType.APPLICATION_JSON)
      .content(request)
    )
  }

  fun runPutRequest(url: String, request: String): ResultActions {
    return mockMvc.perform(put(url)
      .contentType(MediaType.APPLICATION_JSON)
      .content(request)
    )
  }

  fun runDeleteRequest(url: String): ResultActions {
    return mockMvc.perform(delete(url))
  }


}