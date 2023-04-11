package com.gamingbacklog.api.gamingbacklogapi.models.responses

class GameResponse : Response {
    var id: String
    var name: String

    // Custom fields
    var rating: Int?
    var review: String?
    var ranking: String?
    var yearPlayed: Int?
    var yearReceived: Int?
    var notes: String?
    var platformsOwnedOn: List<String>?

    constructor(
            id: String,
            name: String,
            rating: Int,
            review: String,
            ranking: String,
            yearPlayed: Int,
            yearReceived: Int,
            notes: String,
            platformsOwnedOn: List<String>,
    ) {
        this.id = id
        this.name = name
        this.rating = rating
        this.review = review
        this.ranking = ranking
        this.yearPlayed = yearPlayed
        this.yearReceived = yearReceived
        this.notes = notes
        this.platformsOwnedOn = platformsOwnedOn
    }

    constructor(id: String, name: String) {
        this.id = id
        this.name = name
        this.rating = null
        this.review = null
        this.ranking = null
        this.yearPlayed = null
        this.yearReceived = null
        this.notes = null
        this.platformsOwnedOn = null
    }
}
