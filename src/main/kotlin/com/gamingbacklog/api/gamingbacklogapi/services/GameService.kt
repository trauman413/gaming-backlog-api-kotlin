package com.gamingbacklog.api.gamingbacklogapi.services

import com.gamingbacklog.api.gamingbacklogapi.models.Game
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.CompanyFieldInfo
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.FieldInfo
import com.gamingbacklog.api.gamingbacklogapi.models.igdb.IGDBGame
import com.gamingbacklog.api.gamingbacklogapi.repositories.GameRepository
import com.gamingbacklog.api.gamingbacklogapi.requests.GameRequest
import com.gamingbacklog.api.gamingbacklogapi.requests.Request
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList

@Service
class GameService(
  private val gameRepository: GameRepository,

) : IService<Game> {
  override fun getAll(): List<Game> {
    return gameRepository.findAll()
  }

  override fun getSingle(id: String): Game? {
    return gameRepository.findOneById(ObjectId(id))
  }

  override fun create(request: Request): Game {
    val gameRequest = request as GameRequest
    val platform: List<String> = if (gameRequest.platform == null) emptyList() else gameRequest.platform!!
    val genre: List<String> = if (gameRequest.genre == null) emptyList() else gameRequest.genre!!
    val universe: List<String> = if (gameRequest.universe == null) emptyList() else gameRequest.universe!!
    val company: List<String> = if (gameRequest.company == null) emptyList() else gameRequest.company!!
    val game = Game(
      name = gameRequest.name,
      platforms = platform,
      genres = genre,
      universes = universe,
      companies = company,
      igdbId = 10919, // TODO: last few values are placeholder
      releaseDate = Date(182),
      images = listOf("er")
    )
    gameRepository.save(game)
    return game
  }

  override fun delete(id: String) {
    gameRepository.deleteById(id)
  }

  override fun update(model: Game) {
    gameRepository.save(model)
  }

//  fun igdbGameToGame(igdbGame: IGDBGame): Game {
//    // TODO: implement
//
//    return Game(
//      name = igdbGame.name,
//      platforms = extractFieldInfo(igdbGame.platforms),
//      genres = extractFieldInfo(igdbGame.genres),
//      companies =
//
//    )
//    return Game()
//  }

  fun extractFieldInfo(igdbProperties: List<FieldInfo>): List<String> {
    val fields = ArrayList<String>()
    for (field in igdbProperties) {
      fields.add(field.name)
    }
    return fields
  }

//  fun extractCompanyFieldInfo(companies: List<CompanyFieldInfo>): List<String> {
//    companies.map {  }
//  }
}