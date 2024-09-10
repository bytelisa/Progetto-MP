package it.VES.yahtzee.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// una classe astratta che estende RoomDatabase.
// In fase di esecuzione, è possibile acquisirne un'istanza tramite Room.databaseBuilder
// La classe Database definisce l'elenco di entità e oggetti di accesso ai dati nel database.

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {

    abstract fun userDao(): UserDAO

    // "companion object" contiene gli oggetti che hanno visibilità pubblica, ovvero visibili da altre classi
    companion object {
        @Volatile
        // rendere la connessione a DB come singleton
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {

            // se l'istanza già esiste, restituisce l'istanza
            // se l'istanza è null, allora lo crea
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}