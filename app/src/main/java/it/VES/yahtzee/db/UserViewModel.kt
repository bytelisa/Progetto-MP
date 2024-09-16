package it.VES.yahtzee.db

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// from view model we're going to access all our queries from DAO
// view model provide data to the UI and survive configuration changes
// A ViewModel acts as a communication center between the Repository and the UI
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    val allUsers: LiveData<List<User>>

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        allUsers = repository.allUsers
        Log.d("UserViewModel", "UserViewModel initialized")
    }

    fun insert(user: User) {
        Log.d("UserViewModel", "Attempting to insert user: $user")
        repository.insert(user)
        Log.d("UserViewModel", "Insert request sent for user: $user")

    }

    fun delete(user: User) {
        // Eliminazione gestita dal repository
        repository.delete(user)
    }
}