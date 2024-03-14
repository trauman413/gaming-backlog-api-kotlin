package com.gamingbacklog.api.gamingbacklogapi.integration

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.controllers.GameInstanceController
import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameInstanceRepository
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.requests.GameInstanceRequest
import com.gamingbacklog.api.gamingbacklogapi.services.GameInstanceService
import com.gamingbacklog.api.gamingbacklogapi.services.GameService
import com.gamingbacklog.api.gamingbacklogapi.integration.utils.RequestBuilder
import com.gamingbacklog.api.gamingbacklogapi.integration.utils.TestUtils.requestToString
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest
@AutoConfigureMockMvc
@Import(GameInstanceController::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class GameInstanceIT {
  private val gameInstanceRepository = Mockito.mock(GameInstanceRepository::class.java)
  private val gameRepository = Mockito.mock(GameRepository::class.java)
  private val igdbClient = Mockito.mock(IGDBClient::class.java)
  val gameInstanceService = GameInstanceService(gameInstanceRepository, GameService(gameRepository, igdbClient))
  lateinit var requestBuilder: RequestBuilder
  private var endpoint = "/gameinstances/"
  val id1 = "70b664a416135a6e967fadc6"
  val id2 = "dd7f03b962f1f3416d08ee0f"

  @BeforeEach
  fun configureSystem() {
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
      val game1 = GameInstance(
        "id1",
        "igdb1",
        "Persona 5 Royal",
        arrayListOf("PS4"),
        arrayListOf("RPG"),
        arrayListOf("Persona"),
        arrayListOf("Atlus"),
        arrayListOf("2020"),
        arrayListOf(""),
        "Steal their heart!"
      )
      val game2 = GameInstance(
        "id2",
        "igdb2",
        "Persona 4 Golden",
        arrayListOf("PSVita"),
        arrayListOf("RPG"),
        arrayListOf("Persona"),
        arrayListOf("Atlus"),
        arrayListOf("2012"),
        arrayListOf(""),
        "Everyday is great at your Junes!"
      )
      val games = ArrayList<GameInstance>()
      games.add(game1)
      games.add(game2)
      given(gameInstanceRepository.findAll()).willReturn(games)
      requestBuilder.runGetRequest(endpoint)
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", CoreMatchers.equalTo("Persona 5 Royal")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].platforms[0]", CoreMatchers.equalTo("PS4")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].genres[0]", CoreMatchers.equalTo("RPG")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].universes[0]", CoreMatchers.equalTo("Persona")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].companies[0]", CoreMatchers.equalTo("Atlus")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].releaseDate[0]", CoreMatchers.equalTo("2020")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].summary", CoreMatchers.equalTo(game1.summary)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.equalTo("id1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", CoreMatchers.equalTo("Persona 4 Golden")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].platforms[0]", CoreMatchers.equalTo("PSVita")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].genres[0]", CoreMatchers.equalTo("RPG")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].universes[0]", CoreMatchers.equalTo("Persona")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].companies[0]", CoreMatchers.equalTo("Atlus")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].releaseDate[0]", CoreMatchers.equalTo("2012")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].summary", CoreMatchers.equalTo(game2.summary)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", CoreMatchers.equalTo("id2")))
    }
  }

  @Nested
  @DisplayName("Tests for getSingleGameInstance")
  inner class GetSingleGameInstance {
    @Test
    fun shouldSuccessfullyReturnGame() {
      val game = GameInstance(
        id1, "igdb1", "Live a Live",
        arrayListOf("Nintendo Switch"), arrayListOf("RPG"), arrayListOf("Live a Live"), arrayListOf("Square Enix"),
        arrayListOf("2022"), arrayListOf("url"), "so many stories"
      )
      given(gameInstanceRepository.findOneById(any())).willReturn(game)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.equalTo("Live a Live")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.igdbId", CoreMatchers.equalTo("igdb1")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[0]", CoreMatchers.equalTo("Nintendo Switch")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.genres[0]", CoreMatchers.equalTo("RPG")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.universes[0]", CoreMatchers.equalTo("Live a Live")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.companies[0]", CoreMatchers.equalTo("Square Enix")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate[0]", CoreMatchers.equalTo("2022")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.images[0]", CoreMatchers.equalTo("url")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.summary", CoreMatchers.equalTo("so many stories")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.equalTo(id1)))
    }

    @Test
    fun shouldReturnNoGameInstance() {
      given(gameInstanceRepository.findOneById(any())).willReturn(null)
      endpoint += "$id1/"
      requestBuilder.runGetRequest(endpoint)
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist())
    }
  }

  @Nested
  @DisplayName("Tests for createGameInstance")
  inner class CreateGameInstance {
    @Test
    fun shouldSuccessfullyCreateGame() {
      val newGame = GameInstance(
        id = id1,
        igdbId = "igdb1",
        name = "Celeste",
        platforms = arrayListOf("Nintendo Switch", "PC", "PS4", "Xbox One"),
        genres = arrayListOf("Platformer"),
        companies = arrayListOf("Extremely OK Games"),
        releaseDate = arrayListOf("2018"),
        images = arrayListOf("so mountain"),
        summary = "Climb up the mountain in this hard platformer"
      )
      given(gameRepository.findByigdbId(any())).willReturn(
        Game(
          id = "",
          igdbId = "igdb1",
          name = newGame.name,
          platforms = newGame.platforms,
          genres = newGame.genres,
          companies = newGame.companies,
          releaseDate = newGame.releaseDate,
          images = newGame.images,
          summary = newGame.summary
        )
      )
      val gameInstanceRequest = GameInstanceRequest(
        "igdb1",
        null, null, null, null, null, null, null
      )
      requestBuilder.runPostRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(MockMvcResultMatchers.status().isCreated)
        .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.equalTo(newGame.name)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.igdbId", CoreMatchers.equalTo(newGame.igdbId)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[0]", CoreMatchers.equalTo(newGame.platforms[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[1]", CoreMatchers.equalTo(newGame.platforms[1])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[2]", CoreMatchers.equalTo(newGame.platforms[2])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[3]", CoreMatchers.equalTo(newGame.platforms[3])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.genres[0]", CoreMatchers.equalTo(newGame.genres[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.universes", CoreMatchers.equalTo(null)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.companies[0]", CoreMatchers.equalTo(newGame.companies[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate[0]", CoreMatchers.equalTo(newGame.releaseDate[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.images[0]", CoreMatchers.equalTo(newGame.images[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.summary", CoreMatchers.equalTo(newGame.summary)))
    }
  }

  @Nested
  @DisplayName("Tests for updateGameInstance")
  inner class UpdateGameInstance {
    private fun setValues(gameInstanceRequest: GameInstanceRequest, game: GameInstance) {
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
      val game = GameInstance(
        id1, "igdb1", "Trails in the Sky",
        arrayListOf("PC", "PSP"),
        arrayListOf("RPG"), arrayListOf("Trails", "Legend of Heroes"), arrayListOf("Falcom"),
        arrayListOf("2004"), arrayListOf("sky"), "wholesome"
      )
      endpoint += "$id1/"
      given(gameInstanceRepository.findOneById(any())).willReturn(game)
      requestBuilder.runPatchRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(MockMvcResultMatchers.status().isOk)
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
        arrayListOf("2001"), arrayListOf("zanarkand"), "this game will make you cry")

      given(gameInstanceRepository.findOneById(any())).willReturn(game)
      endpoint += "$id1/"
      requestBuilder.runPatchRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(MockMvcResultMatchers.status().isOk)
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
        arrayListOf("2004"), arrayListOf("url"), "Fun turn-based fun")
      val games = ArrayList<GameInstance>()
      games.add(newGame)
      given(gameInstanceRepository.deleteById(any())).willAnswer {
        games.remove(newGame)
      }
      requestBuilder.runDeleteRequest("$endpoint/$id1")
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Successfully deleted game instance")))
      Assertions.assertTrue(games.isEmpty())
    }
  }
}