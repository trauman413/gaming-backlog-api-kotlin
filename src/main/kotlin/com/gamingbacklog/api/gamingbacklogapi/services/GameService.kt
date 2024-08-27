package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.clients.IGDBClient
import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.*
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.models.requests.GameRequest
import com.gamingbacklog.api.gamingbacklogapi.models.requests.Request
import org.bson.types.ObjectId
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.collections.ArrayList

@Service
class GameService(
  private val gameRepository: GameRepository,
  private val igdbClient: IGDBClient

) : IService<Game> {
  override fun getAll(): List<Game> {
    return gameRepository.findAll()
  }

  override fun getSingle(id: String): Game? {
    return gameRepository.findOneById(ObjectId(id))
  }

  override fun getSingleByName(name: String): Game? {
    return gameRepository.findByName(name)

  }

  // TODO: figure out how to get substring matching to work with spring
  fun searchGamesBySubstring(substring: String): List<Game> {
    val matcher = ExampleMatcher.matching()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
    val example = Example.of(
            Game(
                    id = ObjectId.get().toString(),
                    "",
                    substring,
                    listOf(""),
                    listOf(""),
                    listOf(""),
                    listOf(""),
                    listOf(""),
                    listOf(""),
                    "" ),
            matcher)
    val sort = Sort.by(Sort.Direction.ASC, "name");
    return gameRepository.findAll(example, sort)
  }

  fun getByIGDBId(igdbId: String): Game? {
    return gameRepository.findByigdbId(igdbId)
  }

  override fun create(request: Request): Game? {
    val gameRequest = request as GameRequest
    val igdbId = gameRequest.igdbId
    return try {
      val igdbGame = igdbClient.gamesRequest(igdbClient.authenticate().access_token, igdbId)
      val game = igdbGameToGame(igdbGame[0])
      gameRepository.save(game)
      game
    } catch (ex: Exception) {
      println("Exception when calling IGDB ${ex.localizedMessage}")
      null
    }
  }

  override fun delete(id: String) {
    gameRepository.deleteById(id)
  }

  override fun update(model: Game) {
    gameRepository.save(model)
  }

  fun igdbGameToGame(igdbGame: IGDBGame): Game {
    return Game(
      name = igdbGame.name,
      platforms = extractFieldInfo(igdbGame.platforms),
      genres = extractFieldInfo(igdbGame.genres),
      companies = extractCompanyFieldInfo(igdbGame.involved_companies),
      universes = if (igdbGame.franchises.isNullOrEmpty()) null else extractFieldInfo(igdbGame.franchises),
      images = extractArtworkFieldInfo(igdbGame.artworks),
      releaseDate = extractReleaseDateFieldInfo(igdbGame.release_dates),
      summary = igdbGame.summary,
      igdbId = igdbGame.id.toString()
    )
  }

  fun extractFieldInfo(igdbProperties: List<FieldInfo>?): List<String> {
    val fields = ArrayList<String>()
    if (igdbProperties != null) {
      for (field in igdbProperties) {
        fields.add(field.name)
      }
    }
    return fields
  }

  fun extractCompanyFieldInfo(companies: List<CompanyFieldInfo>?): List<String> {
    val fields = ArrayList<String>()
    if (companies != null) {
      for (companyInfo in companies) {
        fields.add(companyInfo.company.name);
      }
    }
    return fields
  }

  fun extractArtworkFieldInfo(artworks: List<ArtworkInfo>?): List<String> {
    val fields = ArrayList<String>()
    if (artworks != null) {
      for (artwork in artworks) {
        fields.add(artwork.url);
      }
    }
    return fields
  }

  fun extractReleaseDateFieldInfo(releaseDates: List<ReleaseDate>?): List<String> {
    val fields = ArrayList<String>()
    if (releaseDates != null) {
      for (releaseDate in releaseDates) {
        fields.add(releaseDate.human);
      }
    }
    return fields
  }
}
