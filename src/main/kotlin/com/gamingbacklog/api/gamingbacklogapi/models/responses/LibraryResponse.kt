package com.gamingbacklog.api.gamingbacklogapi.models.responses
class LibraryResponse(
        var id: String,
        var name: String,
        var games: List<GameResponse>
) : Response() {
}
