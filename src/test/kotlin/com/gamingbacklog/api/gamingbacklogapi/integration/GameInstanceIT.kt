package com.gamingbacklog.api.gamingbacklogapi.integration

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.controllers.GameInstanceController
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game1
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game2
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game3
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game4
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game5
import com.gamingbacklog.api.gamingbacklogapi.integration.constants.GameInstanceSamples.game6
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
  final val id1 = "70b664a416135a6e967fadc6"

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
      given(gameInstanceRepository.findOneById(any())).willReturn(game3)
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
      given(gameRepository.findByigdbId(any())).willReturn(
        Game(
          id = "",
          igdbId = "igdb1",
          name = game4.name,
          platforms = game4.platforms,
          genres = game4.genres,
          companies = game4.companies,
          releaseDate = game4.releaseDate,
          images = game4.images,
          summary = game4.summary
        )
      )
      val gameInstanceRequest = GameInstanceRequest(
        "igdb1",
        null, null, null, null, null, null, null
      )
      requestBuilder.runPostRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(MockMvcResultMatchers.status().isCreated)
        .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.equalTo(game4.name)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.igdbId", CoreMatchers.equalTo(game4.igdbId)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[0]", CoreMatchers.equalTo(game4.platforms[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[1]", CoreMatchers.equalTo(game4.platforms[1])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[2]", CoreMatchers.equalTo(game4.platforms[2])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.platforms[3]", CoreMatchers.equalTo(game4.platforms[3])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.genres[0]", CoreMatchers.equalTo(game4.genres[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.universes", CoreMatchers.equalTo(null)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.companies[0]", CoreMatchers.equalTo(game4.companies[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate[0]", CoreMatchers.equalTo(game4.releaseDate[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.images[0]", CoreMatchers.equalTo(game4.images[0])))
        .andExpect(MockMvcResultMatchers.jsonPath("$.summary", CoreMatchers.equalTo(game4.summary)))
    }
  }

  @Nested
  @DisplayName("Tests for updateGameInstance")
  inner class UpdateGameInstance {
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

      endpoint += "$id1/"
      given(gameInstanceRepository.findOneById(any())).willReturn(game5)
      requestBuilder.runPatchRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(MockMvcResultMatchers.status().isOk)
      assertEquals(game5.rating, gameInstanceRequest.rating)
      assertEquals(game5.review, gameInstanceRequest.review)
      assertEquals(game5.ranking, gameInstanceRequest.ranking)
      assertEquals(game5.yearPlayed, gameInstanceRequest.yearPlayed)
      assertEquals(game5.yearReceived, gameInstanceRequest.yearReceived)
      assertEquals(game5.notes, gameInstanceRequest.notes)
      assertEquals(game5.platformsOwnedOn, gameInstanceRequest.platformsOwnedOn)
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

      given(gameInstanceRepository.findOneById(any())).willReturn(game6)
      endpoint += "$id1/"
      requestBuilder.runPatchRequest(endpoint, requestToString(gameInstanceRequest))
        .andExpect(MockMvcResultMatchers.status().isOk)
      assertEquals(gameInstanceRequest.rating, game6.rating)
      assertEquals("i <3 this game", game6.review)
      assertEquals("1", game6.ranking)
      assertEquals(2006, game6.yearPlayed)
      assertEquals(2004, game6.yearReceived)
      assertEquals(gameInstanceRequest.notes, game6.notes)
      assertEquals(gameInstanceRequest.platformsOwnedOn, game6.platformsOwnedOn)
    }
  }

  @Nested
  @DisplayName("Tests for deleteGameInstance")
  inner class DeleteGameInstances {
    @Test
    fun shouldSuccessfullyDeleteGameInstance() {
      val games = ArrayList<GameInstance>()
      games.add(game6)
      given(gameInstanceRepository.deleteById(any())).willAnswer {
        games.remove(game6)
      }
      requestBuilder.runDeleteRequest("$endpoint/$id1")
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Successfully deleted game instance")))
      assertTrue(games.isEmpty())
    }
  }
}