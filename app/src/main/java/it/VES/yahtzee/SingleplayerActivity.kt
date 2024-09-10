package it.VES.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import it.VES.yahtzee.ui.theme.YahtzeeTheme

class SingleplayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BackgroundSingleplayer()
                    SinglePlayer()
                }
            }
        }
    }

    @Composable
    fun SinglePlayer() {
        Box(
            modifier = Modifier
                .fillMaxSize()//Riempie tutta la schermata

        ) {

            //Uso row per affiancare i due bottoni in basso
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)//Allinea i bottoni al centro in basso
                    .padding(16.dp)//distanzio i bottoni
            ) {
                Button(
                    onClick = {/*azione per il bottone roll*/ },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(200.dp)
                        .height(45.dp),
                ) {
                    Text(text = "Roll")
                }
                Button(
                    onClick = {/*azione per il bottone play*/ },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(100.dp)
                        .height(45.dp),
                ) {
                    Text(text = "Play")
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SinglePlayerPreview() {
        YahtzeeTheme {
            BackgroundSingleplayer()
            SinglePlayer()
        }

    }

    @Composable
    fun BackgroundSingleplayer() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.singleplayer),
                contentDescription = "Single player background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

    }
}