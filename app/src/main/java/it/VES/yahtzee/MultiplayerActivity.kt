package it.VES.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.material3.ButtonDefaults

import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.VES.yahtzee.ui.theme.YahtzeeTheme
import it.VES.yahtzee.PlayUtils


class MultiplayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content= { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            BackgroundMultiplayer()
                            //per i bottoni roll and play
                            MultiPlayer()
                            //posiziono la tabella dei punteggi a destra
                            ScoreTableM()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MultiPlayer() {

    var rolledDice by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    rolledDice = DiceRollActivity().rollDice()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xB5FF0000)
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(200.dp)
                    .height(45.dp),
            ) {
                Text(text = "Roll")
            }
            Button(
                onClick = { /*azione per il bottone play*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xB5FF0000)
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(100.dp)
                    .height(45.dp),
            ) {
                Text(text = "Play")
            }
        }

        if (rolledDice.isNotEmpty()) {
            val rotationValues = listOf(0f, 15f, -10f, 20f, -5f)

            PlayUtils().ImageSequence(
                rolledDice,
                rotationValues = rotationValues,
                context
            )
        }
    }
}


@Composable
fun ScoreTableM() {
    var clickedButtonIndex by remember { mutableStateOf(-1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 70.dp, top = 1.dp, bottom = 90.dp) // Padding per spostare i bottoni
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd) // Allinea la colonna a destra
        ) {
            for (i in 0 until 14) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Button(
                        onClick = {
                            clickedButtonIndex = i * 2 + 1
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (clickedButtonIndex == i * 2 + 1) Color(0xB5FF0000) else Color(0x5E969696)
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(80.dp)
                            .height(30.dp)
                            .offset(x = 19.dp,y=(i*2.5).dp) // Aggiungi l'offset desiderato
                    ) {
                        Text(text = "Button ${i * 2 + 1}")
                    }
                    Button(
                        onClick = {
                            clickedButtonIndex = i * 2 + 2
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (clickedButtonIndex == i * 2 + 2) Color(0xB5FF0000) else Color(0x5E969696)
                        ),
                        modifier = Modifier
                            .width(80.dp)
                            .height(30.dp)
                            .offset(x = 19.dp,y=(i*2.5).dp) // Aggiungi l'offset desiderato
                    ) {
                        Text(text = "Button ${i * 2 + 2}")
                    }
                }
            }
        }
    }
}



@Composable
fun BackgroundMultiplayer(){
    Box(
        modifier=Modifier.fillMaxSize()
    ){
        Image(
            painter=painterResource(id= R.drawable.multiplayer),
            contentDescription="Multi player background",
            contentScale= ContentScale.Crop,
            modifier=Modifier.matchParentSize()
        )
    }

}

@Preview(showBackground = true)
@Composable
fun Preview() {
    YahtzeeTheme {
        BackgroundMultiplayer()
        ScoreTableM()
        MultiPlayer()
    }
}
