package com.gamingbacklog.api.gamingbacklogapi.clients

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class ExternalAPICall {
  fun postExternalCall(
    uri: String,
    body: String? = null,
    headers: HashMap<String, String>? = null,
  ): HttpResponse<String> {
    val requestBody = if (body != null)  HttpRequest.BodyPublishers.ofString(body) else HttpRequest.BodyPublishers.noBody()
    val requestBuilder: HttpRequest.Builder? = HttpRequest.newBuilder()
      .uri(URI.create(uri))
      .POST(requestBody)
    if (headers != null) {
      for ((key, value) in headers) {
        requestBuilder?.header(key, value)
      }
    }
    val request = requestBuilder?.build()
    return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
  }
}