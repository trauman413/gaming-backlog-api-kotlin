package com.gamingbacklog.api.gamingbacklogapi.controllers

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post


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

}