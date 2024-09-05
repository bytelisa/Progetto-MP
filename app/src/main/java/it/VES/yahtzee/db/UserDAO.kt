package it.VES.yahtzee.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserDAO {

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getAllUserInfo(): List<User>? // function to get the list of a user's dataset

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User?) // function to insert datasets into the database

    @Delete
    fun deleteUser(user: User?) // function to delete datasets from the database

    @Query("SELECT * FROM User WHERE mod LIKE :search ORDER BY id ASC")
    fun searchByMod(search: String): MutableList<User> // query to filter by game mode


}