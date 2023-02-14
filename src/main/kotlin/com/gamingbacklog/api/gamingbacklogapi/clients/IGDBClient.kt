package com.gamingbacklog.api.gamingbacklogapi.clients

import com.gamingbacklog.api.gamingbacklogapi.models.igdb.Credentials
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.IGDBGame
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class IGDBClient {

  @Value("\${igdb.client.id}")
  val clientId: String = ""

  @Value("\${igdb.client.secret}")
  val clientSecret: String = ""

  val authUri = "https://id.twitch.tv/oauth2/token"
  val baseUri = "https://api.igdb.com/v4/"

  /**
   * Generates a token
   */
  fun authenticate(): Credentials {
    val request: HttpRequest = HttpRequest.newBuilder()
      .uri(URI.create("$authUri?client_id=$clientId&client_secret=$clientSecret&grant_type=client_credentials"))
      .POST(HttpRequest.BodyPublishers.noBody())
      .build()
    try {
      val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
      return Gson().fromJson(response.body(), Credentials::class.java)
    } catch (ex: Exception) {
      ex.printStackTrace()
    }
    return Credentials("", "", "")
  }

  /**
   * Retrieves information about a given game and creates an associated Game
   *
   * @param accessToken   The authentication token
   * @param gameID        The associated gameID
   */
  fun gamesRequest(
    accessToken: String,
    gameID: String
  ): Array<IGDBGame> {
    val body = "fields name, platforms.name, genres.name, release_dates.human, summary, franchises.name, involved_companies.company.name, artworks.url; where id =$gameID;"
    val request: HttpRequest = HttpRequest.newBuilder()
      .uri(URI.create("${baseUri}games/"))
      .POST(HttpRequest.BodyPublishers.ofString(body))
      .header("Client-ID", clientId)
      .header("Authorization", "Bearer $accessToken")
      .build()
    try {
      val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
      println("Request: ${request.uri()} Body: ${request.bodyPublisher()}")
      println("Response: ${response.body()}")
      // todo: need better error handling here for 404
      return Gson().fromJson(response.body(), Array<IGDBGame>::class.java)
    } catch (ex: Exception) {
      ex.printStackTrace()
    }
    val blank = IGDBGame(0, emptyList(), emptyList(), emptyList(), emptyList(), "", emptyList(), emptyList(), "")
    return arrayOf(blank)
  }






}