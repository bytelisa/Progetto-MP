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
class UserViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<User>>
    private val repository: UserRepository

    // metodo che viene eseguito per prima
    init{

        // Ottieni il DAO dal database
        val userDAO = UserDatabase.getDatabase(application).userDao()
            ?: throw IllegalStateException("UserDatabase is not initialized properly")

        // Inizializza il repository
        repository = UserRepository(userDAO)

        // Ottieni i dati dal repository
        readAllData = repository.readAllData

        // Log per il debugging
        Log.d("UserViewModel", "UserViewModel initialized")
    }


    // Funzione per aggiungere un utente
    fun addUser(user: User){
        // Esegui il codice in background
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

}