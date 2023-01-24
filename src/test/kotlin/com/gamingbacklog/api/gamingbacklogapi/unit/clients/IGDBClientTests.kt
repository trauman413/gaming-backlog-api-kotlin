package com.gamingbacklog.api.gamingbacklogapi.unit.clients

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.clients.util.ExternalAPIClient
import com.gamingbacklog.api.gamingbacklogapi.unit.testutil.models.MockResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.given
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@SpringBootTest
@Import(IGDBClient::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class IGDBClientTests {
  lateinit var externalAPIClient: ExternalAPIClient
  lateinit var igdbClient: IGDBClient

  @BeforeEach
  fun configureSystem() {
    externalAPIClient = mock(ExternalAPIClient::class.java)
    igdbClient = IGDBClient(externalAPIClient)
  }

  @Nested
  @DisplayName("Tests for Authenticate")
  inner class Authenticate {

    @Test
    fun shouldSuccessfullyReturnToken() {
      given(externalAPIClient.postExternalCall(any(), eq(null), eq(null))).willReturn(
        MockResponse(
          "{\"access_token\":\"test_secret\",\"expires_in\":5180593,\"token_type\":\"bearer\"}",
          200
      )
      )
      val result = igdbClient.authenticate()
      Assertions.assertEquals("test_secret", result.access_token)
      Assertions.assertEquals("5180593", result.expires_in)
      Assertions.assertEquals("bearer", result.token_type)
    }

    @Test
    fun shouldErrorOnAuthentication() {
      given(externalAPIClient.postExternalCall(any(), eq(null), eq(null))).willReturn(
        MockResponse(
          "{\"status\":400,\"message\":\"invalid\"}",
          400
        )
      )
      val result = igdbClient.authenticate()
      Assertions.assertEquals("", result.access_token)
      Assertions.assertEquals("", result.expires_in)
      Assertions.assertEquals("", result.token_type)
    }
  }

  @Nested
  @DisplayName("Tests for Games Request")
  inner class GamesRequest {
    // successful return of game
    @Test
    fun shouldSuccessfullyReturnGame() {
      given(externalAPIClient.postExternalCall(any(), any(), any())).willReturn(
        MockResponse(
          "[\n" +
            "  {\n" +
            "    \"id\": 191411,\n" +
            "    \"artworks\": [\n" +
            "      {\n" +
            "        \"id\": 74625,\n" +
            "        \"url\": \"//images.igdb.com/igdb/image/upload/t_thumb/ar1lkx.jpg\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"franchises\": [\n" +
            "      {\n" +
            "        \"id\": 1932,\n" +
            "        \"name\": \"Xenoblade\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"genres\": [\n" +
            "      {\n" +
            "        \"id\": 12,\n" +
            "        \"name\": \"Role-playing (RPG)\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 31,\n" +
            "        \"name\": \"Adventure\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"involved_companies\": [\n" +
            "      {\n" +
            "        \"id\": 163667,\n" +
            "        \"company\": {\n" +
            "          \"id\": 1119,\n" +
            "          \"name\": \"Monolith Soft\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 163668,\n" +
            "        \"company\": {\n" +
            "          \"id\": 70,\n" +
            "          \"name\": \"Nintendo\"\n" +
            "        }\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \"Xenoblade Chronicles 3\",\n" +
            "    \"platforms\": [\n" +
            "      {\n" +
            "        \"id\": 130,\n" +
            "        \"name\": \"Nintendo Switch\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"release_dates\": [\n" +
            "      {\n" +
            "        \"id\": 354757,\n" +
            "        \"human\": \"Jul 29, 2022\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"summary\": \"Eunie's the bussss\"\n" +
            "  }\n" +
            "]",
          200
        )
      )
      val result = igdbClient.gamesRequest("test_secret", "191")
      Assertions.assertEquals(result[0].id, 191411)
      Assertions.assertEquals(result[0].artworks[0].url, "//images.igdb.com/igdb/image/upload/t_thumb/ar1lkx.jpg")
      Assertions.assertEquals(result[0].franchises[0].name, "Xenoblade")
      Assertions.assertEquals(result[0].genres[0].name, "Role-playing (RPG)")
      Assertions.assertEquals(result[0].genres[1].name, "Adventure")
      Assertions.assertEquals(result[0].involved_companies[0].company.name, "Monolith Soft")
      Assertions.assertEquals(result[0].involved_companies[1].company.name, "Nintendo")
      Assertions.assertEquals(result[0].name, "Xenoblade Chronicles 3")
      Assertions.assertEquals(result[0].release_dates[0].human, "Jul 29, 2022")
      Assertions.assertEquals(result[0].summary, "Eunie's the bussss")
    }
    @Test
    fun shouldReturnError() {
      given(externalAPIClient.postExternalCall(any(), any(), any())).willReturn(
        MockResponse(
        "Status Code: 400, Error: [\n" +
          "  {\n" +
          "    \"title\": \"Syntax Error\",\n" +
          "    \"status\": 400\n" +
          "  }\n" +
          "]",
        400
      )
      )
      val result = igdbClient.gamesRequest("test_secret", "000")
      Assertions.assertEquals(result[0].name, "")
      Assertions.assertEquals(result[0].summary, "")
    }

    @Test
    fun shouldReturnEmptyFor404() {
      given(externalAPIClient.postExternalCall(any(), any(), any())).willReturn(
        MockResponse(
        "[]",
        404
      )
      )
      val result = igdbClient.gamesRequest("test_secret", "1i93837189")
      Assertions.assertEquals(result[0].name, "")
      Assertions.assertEquals(result[0].summary, "")
    }
  }
}