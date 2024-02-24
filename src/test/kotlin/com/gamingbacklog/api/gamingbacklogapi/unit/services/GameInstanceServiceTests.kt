package com.gamingbacklog.api.gamingbacklogapi.unit.services

import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameInstanceRepository
import com.gamingbacklog.api.gamingbacklogapi.requests.GameInstanceRequest
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import com.gamingbacklog.api.gamingbacklogapi.services.GameService
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.mockito.kotlin.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import java.util.*

@SpringBootTest
@Import(GameInstanceService::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class GameInstanceServiceTests {
  final var gameInstanceRepository: GameInstanceRepository = Mockito.mock(GameInstanceRepository::class.java)
  final var gameService = Mockito.mock(GameService::class.java)
  var gameInstanceService: GameInstanceService = GameInstanceService(gameInstanceRepository, gameService)

  @BeforeEach
  fun setup() {

  }
  @Nested
  @DisplayName("Create Game Instance Tests")
  inner class Create {
    // id exists in game repo --> create from there, no igdb
    private val foundGame = Game("id", "301", "Pokemon Emerald", arrayListOf("GBA"), arrayListOf("RPG"),
       arrayListOf("Pokemon"), arrayListOf("Game Freak"), arrayListOf("2004"), arrayListOf("url"), "7/10 too much water")
    private val foundGameInstance = GameInstance("id", "301", "Pokemon Emerald", arrayListOf("GBA"), arrayListOf("RPG"),
      arrayListOf("Pokemon"), arrayListOf("Game Freak"), arrayListOf("2004"), arrayListOf("url"), "7/10 too much water")

    private val request = GameInstanceRequest("301", null, null,
      null, null, null, null, null)

    @Test
    fun shouldCreateInstanceFromIdAlreadyInGameRepo() {
      given(gameService.getByIGDBId(any())).willReturn(foundGame)
      val result = gameInstanceService.create(request)
      Assertions.assertEquals(foundGame.igdbId, result?.igdbId)
      Assertions.assertEquals(foundGame.name, result?.name)
      Assertions.assertEquals(foundGame.platforms, result?.platforms)
      Assertions.assertEquals(foundGame.genres, result?.genres)
      Assertions.assertEquals(foundGame.universes, result?.universes)
      Assertions.assertEquals(foundGame.companies, result?.companies)
      Assertions.assertEquals(foundGame.releaseDate, result?.releaseDate)
      Assertions.assertEquals(foundGame.images, result?.images)
      Assertions.assertEquals(foundGame.summary, result?.summary)
    }
    // id does not exist --> call igdb, create
    @Test
    fun shouldCreateGameInstanceAndCallIGDB() {
      given(gameService.getByIGDBId(any())).willReturn(null)
      given(gameService.create(any())).willReturn(foundGame)
      val result = gameInstanceService.create(request)
      Assertions.assertEquals(foundGame.igdbId, result?.igdbId)
      Assertions.assertEquals(foundGame.name, result?.name)
      Assertions.assertEquals(foundGame.platforms, result?.platforms)
      Assertions.assertEquals(foundGame.genres, result?.genres)
      Assertions.assertEquals(foundGame.universes, result?.universes)
      Assertions.assertEquals(foundGame.companies, result?.companies)
      Assertions.assertEquals(foundGame.releaseDate, result?.releaseDate)
      Assertions.assertEquals(foundGame.images, result?.images)
    }
  }

  @Nested
  @DisplayName("Update With Custom Fields Tests")
  inner class UpdateWithCustomFields {

  @Test
  fun shouldUpdateSomeFields() {
      val gameInstanceRequest = GameInstanceRequest(
        null,
        9,
        null,
        null,
        null,
        null,
        "wow space yay so fun",
        arrayListOf("PS3")
      )
      val game = GameInstance(
        "id", "igdb1", "Mass Effect 2",
        arrayListOf("a lot"),
        arrayListOf("RPG"), arrayListOf("Mass Effect"), arrayListOf("BioWare"),
        arrayListOf("2007"), arrayListOf("normandy"), "Create a space crew and stop the Reapers!"
      )
      given(gameInstanceRepository.findOneById(any())).willReturn(game)
      gameInstanceService.updateWithCustomFields("dd7f03b962f1f3416d08ee0f", gameInstanceRequest)
      Assertions.assertEquals(game.rating, gameInstanceRequest.rating)
      Assertions.assertEquals(game.review, gameInstanceRequest.review)
      Assertions.assertEquals(game.ranking, gameInstanceRequest.ranking)
      Assertions.assertEquals(game.yearPlayed, gameInstanceRequest.yearPlayed)
      Assertions.assertEquals(game.yearReceived, gameInstanceRequest.yearReceived)
      Assertions.assertEquals(game.notes, gameInstanceRequest.notes)
      Assertions.assertEquals(game.platformsOwnedOn, gameInstanceRequest.platformsOwnedOn)
    }

  @Test
  fun shouldUpdateAllFields() {
    val gameInstanceRequest = GameInstanceRequest(
      null,
      8,
      "nostalgia is cool but idk if this game is good anymore, oh well love it anyway",
      "30",
      2005,
      2005,
      "nostalgia games, need to replay before disney trip",
      arrayListOf("PS2", "PS3", "PS4")
    )
    val game = GameInstance("id", "igdb1", "Kingdom Hearts",
      arrayListOf("a lot"),
      arrayListOf("RPG"), arrayListOf("Kingdom Hearts"), arrayListOf("Square Enix"),
      arrayListOf("2002"), arrayListOf("heart in the light or something"), "Disney meets FF but mostly disney")
    given(gameInstanceRepository.findOneById(any())).willReturn(game)
    gameInstanceService.updateWithCustomFields("dd7f03b962f1f3416d08ee0f", gameInstanceRequest)
    Assertions.assertEquals(game.rating, gameInstanceRequest.rating)
    Assertions.assertEquals(game.review, gameInstanceRequest.review)
    Assertions.assertEquals(game.ranking, gameInstanceRequest.ranking)
    Assertions.assertEquals(game.yearPlayed, gameInstanceRequest.yearPlayed)
    Assertions.assertEquals(game.yearReceived, gameInstanceRequest.yearReceived)
    Assertions.assertEquals(game.notes, gameInstanceRequest.notes)
    Assertions.assertEquals(game.platformsOwnedOn, gameInstanceRequest.platformsOwnedOn)
  }
  }
}