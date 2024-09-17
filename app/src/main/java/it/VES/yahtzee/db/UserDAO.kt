package it.VES.yahtzee.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import it.VES.yahtzee.db.User

@Dao
interface UserDAO {

    @Query("SELECT * FROM user_db ORDER BY id DESC")
    fun getAllUserInfo(): LiveData<List<User>>

    @Insert
    fun insertUser(user: User): Long  // Restituisce l'ID dell'elemento inserito

    @Delete
    fun deleteUser(user: User)


    @Query("SELECT * FROM user_db WHERE mod = :gameMode AND date = :date ORDER BY id DESC")
    fun getUsersByGameModeAndDate(gameMode: String, date: String): LiveData<List<User>>
}



