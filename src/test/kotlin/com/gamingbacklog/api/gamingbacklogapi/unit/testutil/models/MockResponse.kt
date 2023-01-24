package com.gamingbacklog.api.gamingbacklogapi.unit.testutil.models

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import javax.net.ssl.SSLSession

class MockResponse(
  mockBody: String,
  mockStatus: Int
) : HttpResponse<String> {
  private val body = mockBody
  private val status = mockStatus
  override fun statusCode(): Int {
    return status
  }

  override fun request(): HttpRequest? {
    return null
  }

  override fun previousResponse(): Optional<HttpResponse<String>> {
    return Optional.empty()
  }

  override fun headers(): HttpHeaders? {
    return null
  }

  override fun body(): String {
    return body
  }

  override fun sslSession(): Optional<SSLSession> {
    return Optional.empty()
  }

  override fun uri(): URI? {
    return null
  }

  override fun version(): HttpClient.Version? {
    return null
  }
}