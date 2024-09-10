package it.VES.yahtzee

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import it.VES.yahtzee.db.User
import it.VES.yahtzee.db.UserViewModel
import it.VES.yahtzee.ui.theme.YahtzeeTheme
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BackgroundPicture()
                    Home()
                }
            }
        }
    }
}


@Composable
fun Home() {
    val context = LocalContext.current

    // Inizializza il UserViewModel
    val userViewModel: UserViewModel = viewModel()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){

            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Yahtzee!",
                //fontStyle = FontStyle.Normal.,
                fontSize = 50.sp, // Big font size
                fontWeight = FontWeight.Bold, // Bold text
                textAlign = TextAlign.Center, // Center the text
                color = Color(0xFF5221AA),
                modifier = Modifier.padding(top = 24.dp) // Add some padding from the top
            )
            // Space between title and buttons
            Spacer(modifier = Modifier.height(150.dp))

            Button(
                onClick = { /*TODO: Action*/

                    // Avvia la nuova Activity per il lancio dei dadi
                    val intent = Intent(context, DiceRollActivity::class.java)
                    context.startActivity(intent)

                },
                modifier = Modifier
                    .width(200.dp) // Set width for all buttons
                    .height(45.dp), // Set height for all buttons)
            ) {
                Text(text = "Play")
            }
            // Space between title and buttons
            Spacer(modifier = Modifier.height(24.dp))

            // Second Button
            Button(
                onClick = { /*TODO: Action*/ },
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp),
                ) {
                Text(text = "How to Play")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Third Button
            Button(
                onClick = {
                    val intent = Intent(context, SettingsActivity::class.java)
                    context.startActivity(intent) },
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp),
            ) {
                Text(text = "Settings")
            }
            Spacer(modifier = Modifier.height(24.dp))

            // funzione di prova per DB (da cancellare)
            UserNameInput(userViewModel = userViewModel)
        }

    }


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    YahtzeeTheme {
        BackgroundPicture()
        Home()
    }
}

@Composable
fun SimpleTextDisplay(message: String) {
    Text(text = message)
}

@Composable
fun Play() {

}

// funzione di prova per DB (da cancellare)
@Composable
fun UserNameInput(userViewModel: UserViewModel/* = viewModel()*/) {


    // Stato per memorizzare il testo inserito
    var text by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Inserisci il tuo nome") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {Text("Nome")}
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Bottone per salvare il nome dell'utente nel database
        Button(
            onClick = {
                if (text.isNotEmpty()) {
                    val user = User(player = text)
                    userViewModel.addUser(user)
                }
                      },
            modifier = Modifier.padding(top = 16.dp)

        ) {
            Text(text = "Conferma")
        }
    }

}

@Composable
fun BackgroundPicture(){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = "Home background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
            )
    }
}