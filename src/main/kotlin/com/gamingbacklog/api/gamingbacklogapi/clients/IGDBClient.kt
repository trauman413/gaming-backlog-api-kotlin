package com.gamingbacklog.api.gamingbacklogapi.clients

import com.gamingbacklog.api.gamingbacklogapi.clients.util.ExternalAPIClient
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.Credentials
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.IGDBGame
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class IGDBClient(private val externalAPIClient: ExternalAPIClient) {

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
    val response = externalAPIClient.postExternalCall(
      "$authUri?client_id=$clientId&client_secret=$clientSecret&grant_type=client_credentials"
    )
    return if (response.statusCode() == 200) {
      Gson().fromJson(response.body(), Credentials::class.java)
    } else {
      println("Status Code: ${response.statusCode()}, Error: ${response.body()}")
      Credentials("", "", "")
    }
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
    val body =
      "fields name, platforms.name, genres.name, release_dates.human, summary, franchises.name, involved_companies.company.name, artworks.url; where id =$gameID;"
    val headers = HashMap<String, String>()
    headers["Client-ID"] = clientId
    headers["Authorization"] = "Bearer $accessToken"
    val response = externalAPIClient.postExternalCall("${baseUri}games/", body, headers)
    return if (response.statusCode() == 200 && response.body() != "[]") {
      Gson().fromJson(response.body(), Array<IGDBGame>::class.java)
    } else {
      println(
        "Status Code: ${if (response.statusCode() == 200) 404 else response.statusCode()}, " +
          "Error: ${if (response.body() == "[]") "Not found" else response.body()}"
      )
      val blank = IGDBGame(0, emptyList(), emptyList(), emptyList(), emptyList(), "", emptyList(), emptyList(), "")
      arrayOf(blank)
    }
  }






}