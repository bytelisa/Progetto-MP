package it.VES.yahtzee.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// Tabella DB "user" ha 5 colonne:id(PK-AI), player, score, game mode (single/multi), date
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id : Int = 0,
    @ColumnInfo(name = "player") val player: String,
    @ColumnInfo(name = "score") val score: String,
    @ColumnInfo(name = "mod") val mod: String,
    @ColumnInfo(name = "date") val date: String
)
