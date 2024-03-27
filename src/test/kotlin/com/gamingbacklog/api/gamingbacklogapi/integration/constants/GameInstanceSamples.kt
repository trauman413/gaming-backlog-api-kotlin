package com.gamingbacklog.api.gamingbacklogapi.integration.constants

import com.gamingbacklog.api.gamingbacklogapi.models.GameInstance

object GameInstanceSamples {

  private const val id1 = "70b664a416135a6e967fadc6"
  private const val id2 = "dd7f03b962f1f3416d08ee0f"
  private const val id3 = "5e11b8c6fbb706c75e058337"
  private const val id4 = "8e9f5b65026d638f7cd75166"

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
  val game3 = GameInstance(
    id1, "igdb1", "Live a Live",
    arrayListOf("Nintendo Switch"), arrayListOf("RPG"), arrayListOf("Live a Live"), arrayListOf("Square Enix"),
    arrayListOf("2022"), arrayListOf("url"), "so many stories"
  )

  val game4 = GameInstance(
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

  val game5 = GameInstance(
    id1, "igdb1", "Trails in the Sky",
    arrayListOf("PC", "PSP"),
    arrayListOf("RPG"), arrayListOf("Trails", "Legend of Heroes"), arrayListOf("Falcom"),
    arrayListOf("2004"), arrayListOf("sky"), "wholesome"
  )

  val game6 = GameInstance(id1, "igdb1", "Final Fantasy X",
    arrayListOf("a lot"),
    arrayListOf("RPG"), arrayListOf("Final Fantasy"), arrayListOf("Square Enix"),
    arrayListOf("2001"), arrayListOf("zanarkand"), "this game will make you cry",
    review = "i <3 this game",
    rating = 9,
    ranking = "1",
    yearPlayed = 2006,
    yearReceived = 2004,
    notes = null
  )

  val game7 = GameInstance(id2, "19", "Fire Emblem: Engage",
    arrayListOf("Nintendo Switch"), arrayListOf("RPG"), arrayListOf("Fire Emblem"), arrayListOf("Nintendo", "Intelligent Systems"),
    arrayListOf("January 20 2023"), arrayListOf(""), "Shine on, Emblem of Beginnings!")

  val game8 = GameInstance(
    id = id2,
    igdbId = "",
    name = "Super Mario 64",
    platforms = listOf(),
    companies = listOf(),
    genres = listOf(),
    images = listOf(),
    summary = "",
    releaseDate = listOf()
  )
  val game9 = GameInstance(
    id = id3,
    igdbId = "",
    name = "Super Mario Sunshine",
    platforms = listOf(),
    companies = listOf(),
    genres = listOf(),
    images = listOf(),
    summary = "",
    releaseDate = listOf()
  )
  val game10 = GameInstance(
    id = id4,
    igdbId = "",
    name = "Super Mario Galaxy",
    platforms = listOf(),
    companies = listOf(),
    genres = listOf(),
    images = listOf(),
    summary = "",
    releaseDate = listOf()
  )
}