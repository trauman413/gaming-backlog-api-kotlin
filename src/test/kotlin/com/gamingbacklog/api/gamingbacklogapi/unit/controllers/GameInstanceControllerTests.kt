package com.gamingbacklog.api.gamingbacklogapi.unit.controllers

import com.gamingbacklog.api.gamingbacklogapi.controllers.GameInstanceController
import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.models.Library
import com.gamingbacklog.api.gamingbacklogapi.requests.GameInstanceRequest
import com.gamingbacklog.api.gamingbacklogapi.requests.LibraryRequest
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import com.google.gson.Gson
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.equalToObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@AutoConfigureMockMvc
@Import(GameInstanceController::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class GameInstanceControllerTests {
  lateinit var gameInstanceService: GameInstanceService
  lateinit var requestBuilder: RequestBuilder
  var endpoint = "/gameinstances/"
  var id1 = "70b664a416135a6e967fadc6"
  var id2 = "dd7f03b962f1f3416d08ee0f"

  @BeforeEach
  fun configureSystem() {
    gameInstanceService = Mockito.mock(GameInstanceService::class.java)
    val gameInstanceController = GameInstanceController(gameInstanceService)
    val mockMvc = MockMvcBuilders.standaloneSetup(gameInstanceController)
      .build()
    requestBuilder = RequestBuilder(mockMvc)
  }

  @Nested
  @DisplayName("Tests for getGameInstances")
  inner class GetGameInstances {
    @Test
    fun shouldSuccessfullyGetAllGameInstances() {
      val game1 = GameInstance("id1", "igdb1", "Persona 5 Royal",
        arrayListOf("PS4"), arrayListOf("RPG"), arrayListOf("Persona"), arrayListOf("Atlus"), arrayListOf("2020"), arrayListOf(""))
      val game2 = GameInstance("id2", "igdb2", "Persona 4 Golden",
        arrayListOf("PSVita"), arrayListOf("RPG"), arrayListOf("Persona"), arrayListOf("Atlus"), arrayListOf("2012"), arrayListOf(""))
      val games = ArrayList<GameInstance>()
      games.add(game1)
      games.add(game2)
      given(gameInstanceService.getAll()).willReturn(games)
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$[0].name", equalTo("Persona 5 Royal")))
        .andExpect(jsonPath("$[0].platforms[0]", equalTo("PS4")))
        .andExpect(jsonPath("$[0].genres[0]", equalTo("RPG")))
        .andExpect(jsonPath("$[0].universes[0]", equalTo("Persona")))
        .andExpect(jsonPath("$[0].companies[0]", equalTo("Atlus")))
        .andExpect(jsonPath("$[0].releaseDate[0]", equalTo("2020")))
        .andExpect(jsonPath("$[0].id", equalTo("id1")))
        .andExpect(jsonPath("$[1].name", equalTo("Persona 4 Golden")))
        .andExpect(jsonPath("$[1].platforms[0]", equalTo("PSVita")))
        .andExpect(jsonPath("$[1].genres[0]", equalTo("RPG")))
        .andExpect(jsonPath("$[1].universes[0]", equalTo("Persona")))
        .andExpect(jsonPath("$[1].companies[0]", equalTo("Atlus")))
        .andExpect(jsonPath("$[1].releaseDate[0]", equalTo("2012")))
        .andExpect(jsonPath("$[1].id", equalTo("id2")))
    }
  }

  @Nested
  @DisplayName("Tests for getSingleGameInstance")
  inner class GetSingleGameInstance {

    @Test
    fun shouldSuccessfullyReturnGame() {
      val game = GameInstance(id1, "igdb1", "Live a Live",
        arrayListOf("Nintendo Switch"), arrayListOf("RPG"), arrayListOf("Live a Live"), arrayListOf("Square Enix"),
        arrayListOf("2022"), arrayListOf("url"))
      given(gameInstanceService.getSingle(any())).willReturn(game)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.name", equalTo("Live a Live")))
        .andExpect(jsonPath("$.igdbId", equalTo("igdb1")))
        .andExpect(jsonPath("$.platforms[0]", equalTo("Nintendo Switch")))
        .andExpect(jsonPath("$.genres[0]", equalTo("RPG")))
        .andExpect(jsonPath("$.universes[0]", equalTo("Live a Live")))
        .andExpect(jsonPath("$.companies[0]", equalTo("Square Enix")))
        .andExpect(jsonPath("$.releaseDate[0]", equalTo("2022")))
        .andExpect(jsonPath("$.images[0]", equalTo("url")))
        .andExpect(jsonPath("$.id", equalTo(id1)))
    }

    @Test
    fun shouldReturnNoGameInstance() {
      given(gameInstanceService.getSingle(any())).willReturn(null)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(status().isOk)
        .andExpect(jsonPath("$").doesNotExist())
    }
  }

  @Nested
  @DisplayName("Tests for createGameInstance")
  inner class CreateGameInstance {
    @Test
    fun shouldSuccessfullyCreateGame() {
      val newGame = GameInstance(id1, "igdb1", "Celeste",
        arrayListOf("Nintendo Switch", "PC", "PS4", "Xbox One"),
        arrayListOf("Platformer"), arrayListOf("Celeste"), arrayListOf("Extremely OK Games"),
        arrayListOf("2018"), arrayListOf("so mountain"))
      given(gameInstanceService.create(any())).willReturn(newGame)
      val gameInstanceRequest = GameInstanceRequest("igdb1",
        null, null, null, null, null, null, null)
      requestBuilder.runPostRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.name", equalTo(newGame.name)))
        .andExpect(jsonPath("$.id", equalTo(newGame.id)))
        .andExpect(jsonPath("$.igdbId", equalTo(newGame.igdbId)))
        .andExpect(jsonPath("$.platforms[0]", equalTo(newGame.platforms[0])))
        .andExpect(jsonPath("$.platforms[1]", equalTo(newGame.platforms[1])))
        .andExpect(jsonPath("$.platforms[2]", equalTo(newGame.platforms[2])))
        .andExpect(jsonPath("$.platforms[3]", equalTo(newGame.platforms[3])))
        .andExpect(jsonPath("$.genres[0]", equalTo(newGame.genres[0])))
        .andExpect(jsonPath("$.universes[0]", equalTo(newGame.universes[0])))
        .andExpect(jsonPath("$.companies[0]", equalTo(newGame.companies[0])))
        .andExpect(jsonPath("$.releaseDate[0]", equalTo(newGame.releaseDate[0])))
        .andExpect(jsonPath("$.images[0]", equalTo(newGame.images[0])))
    }
  }

  @Nested
  @DisplayName("Tests for updateGameInstance")
  inner class UpdateGameInstance {
    fun setValues(gameInstanceRequest: GameInstanceRequest, game: GameInstance) {
      if (gameInstanceRequest.rating != null) game.rating = gameInstanceRequest.rating
      if (gameInstanceRequest.review != null) game.review = gameInstanceRequest.review
      if (gameInstanceRequest.ranking != null) game.ranking = gameInstanceRequest.ranking
      if (gameInstanceRequest.yearPlayed != null) game.yearPlayed = gameInstanceRequest.yearPlayed
      if (gameInstanceRequest.yearReceived != null) game.yearReceived = gameInstanceRequest.yearReceived
      if (gameInstanceRequest.notes != null) game.notes = gameInstanceRequest.notes
      if (gameInstanceRequest.platformsOwnedOn != null) game.platformsOwnedOn = gameInstanceRequest.platformsOwnedOn
    }
    @Test
    fun successfullyUpdateAllFields() {
      val gameInstanceRequest = GameInstanceRequest(
        null,
        8,
        "Great entry into a great series, Estelle is Bestelle",
        "24",
        2020,
        2020,
        "Own on GOG from sale",
        arrayListOf("PC")
      )
      val game = GameInstance(id1, "igdb1", "Trails in the Sky",
        arrayListOf("PC", "PSP"),
        arrayListOf("RPG"), arrayListOf("Trails", "Legend of Heroes"), arrayListOf("Falcom"),
        arrayListOf("2004"), arrayListOf("sky"))
      given(gameInstanceService.updateWithCustomFields(any(), any())).will {
        setValues(gameInstanceRequest, game)
      }
      endpoint += "$id1/"
      requestBuilder.runPutRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(status().isOk)
      assertEquals(game.rating, gameInstanceRequest.rating)
      assertEquals(game.review, gameInstanceRequest.review)
      assertEquals(game.ranking, gameInstanceRequest.ranking)
      assertEquals(game.yearPlayed, gameInstanceRequest.yearPlayed)
      assertEquals(game.yearReceived, gameInstanceRequest.yearReceived)
      assertEquals(game.notes, gameInstanceRequest.notes)
      assertEquals(game.platformsOwnedOn, gameInstanceRequest.platformsOwnedOn)
    }

    @Test
    fun successfullyUpdatedSomeFields() {
      val gameInstanceRequest = GameInstanceRequest(
        null,
        10,
        null,
        null,
        null,
        null,
        "why do i own this game on 4 platforms? this is factual not just making stuff up for test cases",
        arrayListOf("PC", "Switch", "PS3", "PS2")
      )
      val game = GameInstance(id1, "igdb1", "Final Fantasy X",
        arrayListOf("a lot"),
        arrayListOf("RPG"), arrayListOf("Final Fantasy"), arrayListOf("Square Enix"),
        arrayListOf("2001"), arrayListOf("zanarkand"))

      given(gameInstanceService.updateWithCustomFields(any(), any())).will {
        setValues(gameInstanceRequest, game)
      }
      endpoint += "$id1/"
      requestBuilder.runPutRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(status().isOk)
      assertEquals(game.rating, gameInstanceRequest.rating)
      assertEquals(game.review, gameInstanceRequest.review)
      assertEquals(game.ranking, gameInstanceRequest.ranking)
      assertEquals(game.yearPlayed, gameInstanceRequest.yearPlayed)
      assertEquals(game.yearReceived, gameInstanceRequest.yearReceived)
      assertEquals(game.notes, gameInstanceRequest.notes)
      assertEquals(game.platformsOwnedOn, gameInstanceRequest.platformsOwnedOn)
    }


  }

  @Nested
  @DisplayName("Tests for deleteGameInstance")
  inner class DeleteGameInstances {
    @Test
    fun shouldSuccessfullyDeleteGameInstance() {
      val newGame = GameInstance(id1, "igdb1", "Paper Mario: The Thousand Year Door",
        arrayListOf("GameCube"),
        arrayListOf("RPG"), arrayListOf("Paper Mario"), arrayListOf("Nintendo", "Intelligent Systems"),
        arrayListOf("2004"), arrayListOf("url"))
      val games = ArrayList<GameInstance>()
      games.add(newGame)
      BDDMockito.given(gameInstanceService.delete(any())).will {
        games.remove(newGame)
      }
      requestBuilder.runDeleteRequest("$endpoint/$id1")
        .andExpect(status().isOk)
        .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Successfully deleted game instance")))
      Assertions.assertTrue(games.isEmpty())
    }
  }

  fun requestToString(request: GameInstanceRequest): String {
    return Gson().toJson(request)
  }


}