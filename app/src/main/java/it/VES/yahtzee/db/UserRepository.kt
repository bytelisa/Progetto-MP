package it.VES.yahtzee.db

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData


// A repository class abstracts access to multiple data source
// is a best practice for code separation and architecture

class UserRepository(private val userDao: UserDAO) {


    val allUsers: LiveData<List<User>> = userDao.getAllUserInfo()

    fun insert(user: User) {
        // Esegui inserimento in un thread separato
        Thread {
            userDao.insertUser(user)
        }.start()
        Log.d("UserRep", "Insert request sent for user: $user")
    }

    fun delete(user: User) {
        // Esegui eliminazione in un thread separato
        Thread {
            userDao.deleteUser(user)
        }.start()
    }


}