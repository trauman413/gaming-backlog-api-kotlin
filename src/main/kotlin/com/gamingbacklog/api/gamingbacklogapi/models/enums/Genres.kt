package com.gamingbacklog.api.gamingbacklogapi.models.enums

enum class Genres(val displayName: String) {
    PointAndClick("Point-and-click"),
    Fighting("Fighting"),
    Shooter("Shooter"),
    Music("Music"),
    Platform("Platform"),
    Puzzle("Puzzle"),
    Racing("Racing"),
    RealTimeStrategy("Real Time Strategy (RTS)"),
    RolePlaying("Role-playing (RPG)"),
    Simulator("Simulator"),
    Sport("Sport"),
    Strategy("Strategy"),
    TurnBasedStrategy("Turn-based strategy (TBS)"),
    Tactical("Tactical"),
    QuizTrivia("Quiz/Trivia"),
    HackAndSlashBeatEmUp("Hack and slash/Beat 'em up"),
    Pinball("Pinball"),
    Adventure("Adventure"),
    Arcade("Arcade"),
    VisualNovel("Visual Novel"),
    Indie("Indie"),
    CardBoardGame("Card & Board Game"),
    MOBA("MOBA");

    override fun toString(): String {
        return displayName
    }
}
