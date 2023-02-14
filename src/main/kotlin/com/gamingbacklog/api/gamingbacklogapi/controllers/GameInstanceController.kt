package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.requests.GameInstanceRequest
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// TODO: will need to work out that we need some storage for Games. Games need to exist so we don't make a million calls to IGDB
// see OneNote

@RestController
@RequestMapping("/gameinstance")
class GameInstanceController(
  private val gameInstanceService: GameInstanceService,
  private val igdbClient: IGDBClient
) {
  /**
   * Returns all the existing game instances
   */
  @GetMapping("/")
  fun getGames(): ResponseEntity<List<GameInstance>> {
    val games = gameInstanceService.getAll()
    return ResponseEntity.ok(games)
  }

  /**
   * Returns a single game instance based on an id
   * @param id  A given game ID
   *
   */
  @GetMapping("/{id}")
  fun getSingleGame(
    @PathVariable("id") id: String
  ): ResponseEntity<GameInstance> {
    val game = gameInstanceService.getSingle(id)
    return ResponseEntity.ok(game)
  }

  /**
   * Creates a new game
   * @param gameRequest The game to be created
   *
   */
  @PostMapping("/")
  fun createGame(
    @RequestBody gameRequest: GameInstanceRequest
  ): ResponseEntity<GameInstance> {
    // TODO: may need to be massively changed
    val game = gameInstanceService.create(gameRequest)
    return ResponseEntity<GameInstance>(game, HttpStatus.CREATED)
  }
}