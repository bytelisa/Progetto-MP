package it.VES.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.VES.yahtzee.ui.theme.YahtzeeTheme


class MultiplayerActivity : ComponentActivity() {

    //variabile che tiene traccia del giocatore corrente, varia tra 1 e 2
    private var currentPlayer by mutableIntStateOf(1)


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

                            MultiPlayer(
                                player = currentPlayer,
                                onTurnEnd = {nextPlayer()}
                            )

                            ScoreTableM(1, SingleplayerActivity())
                        }
                    }
                )
            }
        }
    }

    private fun nextPlayer() {
        // Cambia il giocatore corrente
        currentPlayer = (currentPlayer + 1) % 2
    }

}

@Composable
fun NamesPopup(){

    var open by rememberSaveable {mutableStateOf(true)}

    AlertDialog(
        onDismissRequest = { open = false },
        title = { Text(
            text = "Multiplayer",
            fontSize = 35.sp, // Big
        ) },
        text = {
            Column {
                Text("Player 1:")
                Text("Player 2:")
            }
        },
        confirmButton = {
            Button(onClick = {
                open = false

            }) {
                Text("OK")
            }
        },
    )
}


@Composable
fun MultiPlayer(player: Int, onTurnEnd: () -> Unit) {

    var currentPlayer by rememberSaveable { mutableIntStateOf(player) }
    //var currentPlayerActivity by rememberSaveable { mutableStateOf(SingleplayerActivity()) }

    /*voglio usare due istanze di singleplayer per gestire i singoli giochi,
    val Player1 = SingleplayerActivity()
    val Player2 = SingleplayerActivity()

    if (currentPlayer == 1){
        currentPlayerActivity = Player1
    } else if (currentPlayer == 2) {
        currentPlayerActivity = Player2
    }
    */


    val player1State = remember { SinglePlayerState() }
    val player2State = remember { SinglePlayerState() }

    if (currentPlayer == 1) {
        SinglePlayerScreen(
            state = player1State,
            onTurnEnd = onTurnEnd // Passa la gestione della fine del turno
        )
    } else if (currentPlayer == 2){
        SinglePlayerScreen(
            state = player2State,
            onTurnEnd = onTurnEnd // Passa la gestione della fine del turno
        )
    }

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
            /*
            Button(
                onClick = {
                    rolledDice = DiceRollActivity().rollDice()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xB5DA4141)
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
                    containerColor = Color(0xB5DA4141)
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(100.dp)
                    .height(45.dp),
            ) {
                Text(text = "Play")
            }

             */
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
fun ScoreTableM(currentPlayer: Int, currentActivity: SingleplayerActivity) {

    var clickedButtonIndex by remember { mutableIntStateOf(-1) }
    var player by rememberSaveable { mutableIntStateOf(currentPlayer) }

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
                    Button( //player 1
                        onClick = {
                            if (player == 1){
                                clickedButtonIndex = i * 2 + 1
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if ((clickedButtonIndex == i * 2 + 1) && (player == 1)) Color(0xB5DA4141) else Color(0x5E969696)
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(80.dp)
                            .height(30.dp)
                            .offset(x = 19.dp, y = (i * 2.5).dp) // Aggiungi l'offset desiderato
                    ) {
                        Text(text = "Button ${i * 2 + 1}")
                    }
                    Button( //player 2
                        onClick = {
                            if (player == 2){
                                clickedButtonIndex = i * 2 + 2
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if ((clickedButtonIndex == i * 2 + 2) && (player == 2)) Color(0xB5DA4141) else Color(0x5E969696)
                        ),
                        modifier = Modifier
                            .width(80.dp)
                            .height(30.dp)
                            .offset(x = 19.dp, y = (i * 2.5).dp) // Aggiungi l'offset desiderato
                    ) {
                        Text(text = "Button ${i * 2 + 2}")
                    }
                }
            }
        }
    }
}

class SinglePlayerState {
    var rolls by mutableIntStateOf(0)
    var rounds by mutableIntStateOf(0)
    var scoreList = mutableStateListOf(*List(14) { 0 }.toTypedArray())
    var playedCategories = mutableStateListOf(*List(14) { false }.toTypedArray())
    var totalScore by mutableIntStateOf(0)
    var categoryToPlay by androidx.compose.runtime.mutableIntStateOf(0)
    // Aggiungi qui altri stati di gioco del singolo giocatore
}

@Composable
fun SinglePlayerScreen(
    state: SinglePlayerState,
    onTurnEnd: () -> Unit
) {
    // Usa il singolo stato del giocatore, simile a come fai nel singleplayer
    SinglePlayer(
        categoryToPlay = state.categoryToPlay,
        onCategoryToPlayChange = { newCategory ->
            state.categoryToPlay = newCategory
        },
        onTurnEnd = onTurnEnd,
        usedByMultiplayer = true
    )
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

/*
@Preview(showBackground = true)
@Composable
fun Preview() {
    YahtzeeTheme {
        BackgroundMultiplayer()
        ScoreTableM(1, SingleplayerActivity())
        MultiPlayer(1)
    }
}
*/