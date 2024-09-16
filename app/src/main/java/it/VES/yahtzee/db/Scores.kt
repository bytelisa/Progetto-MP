package it.VES.yahtzee.db

import java.util.Date


data class Scores(
    val username: String,
    val gameMode: String,
    val score: String,
    val datePlayed: Date,
    val opponentUsername: String = "",
    val opponentScore: Int = 0
)
