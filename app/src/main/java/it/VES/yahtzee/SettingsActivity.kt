package it.VES.yahtzee

import androidx.compose.ui.text.font.FontFamily

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import it.VES.yahtzee.ui.theme.YahtzeeTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BackgroundPictureSettings()
                    Setting()
                }
            }
        }
    }
}
@Composable
fun Setting(){
    var switch1State by remember { mutableStateOf(false) }
    var switch2State by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxSize() // Riempie tutta la schermata
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(270.dp)) // Titolo in alto

            // Casella di testo per il nome dell'utente
            TextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Your username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            // Primo switch con parole
            Spacer(modifier=Modifier.height(70.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = "Sound")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = "Off")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = switch1State,
                        onCheckedChange = { switch1State = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "On")
                }
            }

            // Secondo switch con parole
            Spacer(modifier=Modifier.height(20.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Dice Roll mode")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = "Click")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = switch2State,
                        onCheckedChange = { switch2State = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Shake")
                }
            }
            Spacer(modifier=Modifier.height(100.dp))
            Button(
                onClick = { /* Azione per il bottone Go Back */ },
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Go Back")
            }
        }
    }
}

@Composable
fun BackgroundPictureSettings(){
    Box(
        modifier=Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.settings),
            contentDescription = "settings",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}
@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    YahtzeeTheme {
        BackgroundPictureSettings()
        Setting()
    }
}




