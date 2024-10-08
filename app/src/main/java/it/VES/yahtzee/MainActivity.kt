package it.VES.yahtzee

import android.app.Application
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.VES.yahtzee.db.Scores
import it.VES.yahtzee.db.UserViewModel
import it.VES.yahtzee.ui.theme.YahtzeeTheme
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BackgroundPicture()
                    //Home()
                    AppNavigation()
                }
            }
        }
    }
}


@Composable
fun Home(navController: NavController) {
    val context = LocalContext.current
    var showDialog  by remember {mutableStateOf(false)}


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
            fontSize = 50.sp, // Big
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF5221AA),
            modifier = Modifier.padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(150.dp))

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val intent = Intent(context, PlayModeActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .width(200.dp)
                .height(45.dp),
        ) {
            Text(text = "Play")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Second Button
        Button(

            onClick = {
                navController.navigate("howToPlay")
            },

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
                navController.navigate("settings")
            },

            modifier = Modifier
                .width(200.dp)
                .height(45.dp),
        ) {
            Text(text = "Settings")
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick={showDialog=true},
            modifier= Modifier
                .width(200.dp)
                .height(45.dp),
        ){
            Text(text="About Us")
        }
        Spacer(modifier = Modifier.height(24.dp))

        // New Button for Score Screen
        Button(
            onClick = {
                navController.navigate("score") // Naviga alla schermata dei punteggi
                Log.d("HomeScreen", "Pulsante 'Go to Scores' cliccato")            },
            modifier = Modifier
                .width(200.dp)
                .height(45.dp),
        ) {
            Text(text = "Score")
        }



        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "About Us",
                        textAlign = TextAlign.Center, // Centra il testo
                        fontSize = 24.sp, // Aumenta la dimensione del testo
                        fontWeight = FontWeight.Bold, // Imposta il testo in grassetto
                        modifier = Modifier
                            .fillMaxWidth() // Riempie la larghezza per centrare il titolo
                            .padding(bottom = 8.dp) // Aggiunge padding per migliorare la spaziatura
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(), // Colonna riempie la larghezza
                        horizontalAlignment = Alignment.CenterHorizontally // Centra il contenuto
                    ) {
                        Text("Elisa Marzioli", fontSize = 18.sp, textAlign = TextAlign.Center) // Nome centrato
                        Text("Sofia Tosti", fontSize = 18.sp, textAlign = TextAlign.Center) // Nome centrato
                        Text("Valentina Jin", fontSize = 18.sp, textAlign = TextAlign.Center) // Nome centrato
                        Spacer(modifier = Modifier.height(16.dp))
                        ClickableText(
                            text = AnnotatedString("Learn more on Wikipedia"),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Yahtzee"))
                                context.startActivity(intent)
                            },
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { showDialog = false },
                            modifier = Modifier.width(120.dp)
                        ) {
                            Text("OK", textAlign = TextAlign.Center)
                        }
                    }
                },
            )
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



@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Home(navController) }
        composable("settings") { Setting(navController) }
        composable("howToPlay") { howToPlay(navController)}

        composable("score") { ScoreScreen(navController) }
    }
}





