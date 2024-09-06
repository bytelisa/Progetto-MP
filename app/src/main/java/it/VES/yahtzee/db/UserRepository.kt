package it.VES.yahtzee.db

import androidx.lifecycle.LiveData


// A repository class abstracts access to multiple data source
// is a best practice for code separation and architecture
class UserRepository(private val userDAO: UserDAO) {


    val readAllData: LiveData<List<User>> = userDAO.getAllUserInfo()

    suspend fun addUser(user: User){
        userDAO.insertUser(user)
    }


}
