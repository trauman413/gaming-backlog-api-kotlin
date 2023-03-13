package com.gamingbacklog.api.gamingbacklogapi.controllers

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.requests.GameInstanceRequest
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/gameinstances")
class GameInstanceController(
  private val gameInstanceService: GameInstanceService,
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
  fun getSingleGameInstance(
    @PathVariable("id") id: String
  ): ResponseEntity<GameInstance> {
    val game = gameInstanceService.getSingle(id)
    return ResponseEntity.ok(game)
  }

  /**
   * Creates a new game instance
   * @param gameRequest The game instance to be created
   */
  @PostMapping("/")
  fun createGameInstance(
    @RequestBody gameRequest: GameInstanceRequest
  ): ResponseEntity<GameInstance> {
    val game = gameInstanceService.create(gameRequest)
    return ResponseEntity<GameInstance>(game, HttpStatus.CREATED)
  }

  /**
   * Updates a game with user-specified fields
   * @param id  A given game ID
   * @param gameRequest   The request with fields to be updated
   */
  @PutMapping("/{id}")
  fun updateGameInstance(
    @PathVariable("id") id: String,
    @RequestBody gameRequest: GameInstanceRequest
  ): ResponseEntity<String> {
    gameInstanceService.updateWithCustomFields(id, gameRequest)
    return ResponseEntity.ok("Successfully added custom fields")
  }

  /**
   * Deletes a game instance
   * @param id A given game instance ID
   */
  @DeleteMapping("/{id}")
  fun deleteGameInstance(
    @PathVariable("id") id: String
  ): ResponseEntity<String> {
    gameInstanceService.delete(id)
    return ResponseEntity.ok("Successfully deleted game instance $id")
  }
}