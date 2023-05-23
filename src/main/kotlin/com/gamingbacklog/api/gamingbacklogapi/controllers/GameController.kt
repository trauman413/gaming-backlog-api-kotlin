package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.Credentials
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.IGDBGame
import com.gamingbacklog.api.gamingbacklogapi.services.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/games")
class GameController(
  private val gameService: GameService,
  private val igdbClient: IGDBClient
) {

  /**
   * Returns all the existing games
   * // TODO: temporary testing, will remove this in long term
   */
  @GetMapping("/")
  fun getGames(): ResponseEntity<List<Game>> {
    val games = gameService.getAll()
    return ResponseEntity.ok(games)
  }

  /**
   * Returns a single game based on an id
   * @param id  A given game ID
   *
   */
  @GetMapping("/{id}")
  fun getSingleGame(
    @PathVariable("id") id: String
  ): ResponseEntity<Game> {
    val game = gameService.getSingle(id)
    return ResponseEntity.ok(game)
  }

  @GetMapping("/authenticate")
  fun authenticateIGDB(): ResponseEntity<Credentials> {
    val creds = igdbClient.authenticate()
    return ResponseEntity.ok(creds)
  }

  @GetMapping("/igdb/{id}")
  fun getFromIGDB(
    @PathVariable("id") id: String
  ): ResponseEntity<Array<IGDBGame>> {
    val credentials = igdbClient.authenticate()
    return ResponseEntity.ok(igdbClient.gamesRequest(credentials.access_token, id))
  }
}
