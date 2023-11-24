package com.gamingbacklog.api.gamingbacklogapi.unit.services

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.*
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.services.GameService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class GameServiceTests {

  private val mockIgdbClient = Mockito.mock(IGDBClient::class.java)
  private val mockGameRepository = Mockito.mock(GameRepository::class.java)

  private val gameService = GameService(mockGameRepository, mockIgdbClient)

  @Nested
  @DisplayName("Tests igdbToGame")
  inner class IGDBToGameTests {
    @Test
    fun shouldSuccessfullyConvertNullFranchise() {
      val igdbGame = IGDBGame(
        id = 12,
        artworks = listOf(),
        franchises = null,
        genres = listOf(FieldInfo(1, "Platform"), FieldInfo(2, "RPG")),
        involved_companies = listOf(),
        name = "ABC",
        platforms = listOf(FieldInfo(1, "Nintendo Switch")),
        release_dates = listOf(),
        summary = "Best game ever"
      )
      val result = gameService.igdbGameToGame(igdbGame)
      assertEquals("12", result.igdbId)
      assertEquals("Best game ever", result.summary)
      assertEquals(listOf("Platform", "RPG"), result.genres)
      assertEquals("ABC", result.name)
      assertEquals(listOf("Nintendo Switch"), result.platforms)
    }

    @Test
    fun shouldSuccessfullyConvert() {
      val igdbGame = IGDBGame(
        id = 254339,
        artworks = listOf(ArtworkInfo(1, "link1"), ArtworkInfo(2, "link2")),
        franchises = listOf(FieldInfo(1, "Mario")),
        genres = listOf(FieldInfo(1, "Platform")),
        involved_companies = listOf(CompanyFieldInfo(1, FieldInfo(1, "Nintendo EPD"))),
        name = "Super Mario Bros. Wonder",
        platforms = listOf(FieldInfo(1, "Nintendo Switch")),
        release_dates = listOf(ReleaseDate(1, "October 20 2023")),
        summary = "Wowie Zowie!"
      )
      val result = gameService.igdbGameToGame(igdbGame)
      assertEquals("254339", result.igdbId)
      assertEquals(listOf("link1", "link2"), result.images)
      assertEquals(listOf("Mario"), result.universes)
      assertEquals("Wowie Zowie!", result.summary)
      assertEquals(listOf("Platform"), result.genres)
      assertEquals(listOf("Nintendo EPD"), result.companies)
      assertEquals("Super Mario Bros. Wonder", result.name)
      assertEquals(listOf("Nintendo Switch"), result.platforms)
      assertEquals(listOf("October 20 2023"), result.releaseDate)
    }
  }
}