package it.VES.yahtzee.ui.theme

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnessioneDB {

    private var connection: Connection? = null

    init {
        try {
            // Inizializza la connessione al database
            connection = DriverManager.getConnection(Companion.DATABASE_URL)
            println("Connessione al database stabilita.")
        } catch (e: SQLException) {
            println("Errore durante la connessione al database: ${e.message}")
        }
    }

    fun getConnection(): Connection? {
        return connection
    }

    fun closeConnection() {
        try {
            connection?.close()
            println("Connessione al database chiusa.")
        } catch (e: SQLException) {
            println("Errore durante la chiusura della connessione: ${e.message}")
        }
    }

    companion object {
        private const val DATABASE_URL = "jdbc:sqlite:progettoMP_DB.db"
    }


}