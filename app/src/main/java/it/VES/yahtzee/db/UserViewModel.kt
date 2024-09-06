package it.VES.yahtzee.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// from view model we're going to access all our queries from DAO
// view model provide data to the UI and survive configuration changes
// A ViewModel acts as a communication center between the Repository and the UI
class UserViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<User>>
    private val repository: UserRepository

    // metodo che viene eseguito per prima
    init{
        val userDAO = UserDatabase.getDatabase(application)!!.userDao()
        repository = UserRepository(userDAO)
        readAllData = repository.readAllData
    }


    fun addUser(user: User){
        // run code in background
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

}