package com.gamingbacklog.api.gamingbacklogapi.testutil.models

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

  override fun request(): HttpRequest {
    TODO("Not yet implemented")
  }

  override fun previousResponse(): Optional<HttpResponse<String>> {
    TODO("Not yet implemented")
  }

  override fun headers(): HttpHeaders {
    TODO("Not yet implemented")
  }

  override fun body(): String {
    return body
  }

  override fun sslSession(): Optional<SSLSession> {
    TODO("Not yet implemented")
  }

  override fun uri(): URI {
    TODO("Not yet implemented")
  }

  override fun version(): HttpClient.Version {
    TODO("Not yet implemented")
  }
}