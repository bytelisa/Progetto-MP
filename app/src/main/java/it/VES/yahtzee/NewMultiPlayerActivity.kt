package it.VES.yahtzee

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.material3.ButtonDefaults

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.VES.yahtzee.ui.theme.YahtzeeTheme


class NewMultiPlayerActivity : ComponentActivity() {

    //variabile che tiene traccia del giocatore corrente, varia tra 1 e 2
    private var currentPlayer by mutableIntStateOf(1)
    private var categoryToPlay by mutableIntStateOf(-1)

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


                            newMultiPlayer(
                                currentPlayer = currentPlayer,
                                categoryToPlay = categoryToPlay,
                                onCategoryToPlayChange = { newCategory ->
                                    categoryToPlay = newCategory
                                },
                                onTurnEnd = {nextPlayer()}
                            )

                            ScoreTableM(
                                currentPlayer= currentPlayer,
                                scorePreview1 = List(14){-1},
                                scorePreview2 = List(14){-1},
                                scoreList1 = List(14){0},
                                scoreList2 = List(14){0},
                                playedCategories1 = List(14){false},
                                playedCategories2 = List(14){false},
                                onCategorySelect1 = { newCategory ->
                                    categoryToPlay = newCategory
                                },
                                onCategorySelect2 = { newCategory ->
                                    categoryToPlay = newCategory
                                },

                            )
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
fun newMultiPlayer(currentPlayer: Int, categoryToPlay: Int, onCategoryToPlayChange: (Int) -> Unit, onTurnEnd: (() -> Unit)){
    var rolls by rememberSaveable { mutableIntStateOf(0) } // max 3
    var rounds by rememberSaveable { mutableIntStateOf(0) } // max 13
    var rolledDice by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showPlayDialog by remember { mutableStateOf(false) }
    val scorePreviewList = remember { mutableStateListOf(*List(14) { -1 }.toTypedArray()) }
    val scoreList = remember { mutableStateListOf(*List(14) { 0 }.toTypedArray()) }
    var totalScore by remember { mutableIntStateOf(0) }
    var gameFinished by rememberSaveable { mutableStateOf(false) }
    val playedCategories = remember { mutableStateListOf(*List(14) { false }.toTypedArray()) }
    var playPressed by rememberSaveable { mutableStateOf(false) }
    var previousCategory by rememberSaveable { mutableIntStateOf(-1) }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = { // roll

                    if (rolls < 3) {
                        playPressed = false
                        rolledDice = DiceRollActivity().rollDice()
                        rolls += 1
                        val scorePreview = PlayUtils().getScorePreview(rolledDice)
                        scorePreviewList.clear()
                        scorePreviewList.addAll(scorePreview)
                        playPressed = false
                    } else {
                        // finisce il turno di gioco, l'utente deve scegliere un punteggio
                        showDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (rolls < 3) {
                        Color(0xB5DA4141)
                    } else {
                        Color(0xB5A5A5A5)
                    }
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(200.dp)
                    .height(45.dp),
            ) {
                Text(text = "Roll (${3 - rolls} left)")
            }

            Button(
                onClick = { // play

                    if (rounds < 13) {
                        if (categoryToPlay != -1) {
                            scoreList[categoryToPlay - 1] = scorePreviewList[categoryToPlay - 1]
                            playedCategories[categoryToPlay - 1] = true
                            previousCategory = categoryToPlay - 1
                            Log.d(
                                "SinglePlayerActivity",
                                "#selected score: ${scoreList[categoryToPlay - 1]}"
                            )

                        } else {
                            showPlayDialog = true
                        }
                        Log.d("SinglePlayerActivity", "New Score List: $scoreList")
                        Log.d("SinglePlayerActivity", "Round finished: ${rounds + 1}")

                        totalScore = ScoreCalculator().totalScore(scoreList)
                        rolls = 0
                        rounds += 1
                        scorePreviewList.clear()
                        playPressed = true


                        if (onTurnEnd != null) {
                            onTurnEnd()
                        }


                    } else {
                        // finisce la partita
                        gameFinished = true
                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (rolls != 0) {
                        Color(0xB5DA4141)
                    } else {
                        Color(0xB5A5A5A5)
                    }
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(100.dp)
                    .height(45.dp),
            ) {
                Text(text = "Play")
            }

        }

        if (rolledDice.isNotEmpty() && rolls != 0) {
            val rotationValues = listOf(0f, 15f, -10f, 20f, -5f)

            PlayUtils().ImageSequence(
                rolledDice,
                rotationValues = rotationValues,
                context
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Uh-oh :(") },
                text = {
                    Column {
                        Text("You're out of rolls for this round. Choose a category and play!")

                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                },
            )
        }

        if (showPlayDialog) {
            AlertDialog(
                onDismissRequest = { showPlayDialog = false },
                title = {
                    Text(
                        text = "Play",
                        color = Color.Red
                    )
                },

                text = {
                    Column {
                        Text("Select a category to play.")

                    }
                },
                confirmButton = {
                    Button(onClick = { showPlayDialog = false }) {
                        Text("OK")
                    }
                },
            )
        }

    }

    if (!playPressed) {
        ScoreTableM(
            currentPlayer= currentPlayer,
            scorePreview1 = List(14){-1},
            scorePreview2 = List(14){-1},
            scoreList1 = List(14){0},
            scoreList2 = List(14){0},
            playedCategories1 = List(14){false},
            playedCategories2 = List(14){false},
            onCategorySelect1 = { index ->
                onCategoryToPlayChange(index)
            },
            onCategorySelect2 = { index ->
                onCategoryToPlayChange(index)
            },

            )
    }

    if (gameFinished) {
        GameFinish(score = totalScore)
    }

    Score(totalScore)
    RoundsLeft(rounds)


}


@Composable
fun ScoreTableM(
    currentPlayer: Int,
    scorePreview1: List<Int>, scorePreview2: List<Int>,
    scoreList1: List<Int>, scoreList2: List<Int>,
    playedCategories1: List<Boolean>, playedCategories2: List<Boolean>,
    onCategorySelect1: (Int) -> Unit, onCategorySelect2: (Int) -> Unit,
    state1: SinglePlayerState, state2: SinglePlayerState
) {

    var clickedButtonIndex by remember { mutableIntStateOf(-1) }
    var player by rememberSaveable { mutableIntStateOf(currentPlayer) }
    var playedCategory by remember { mutableIntStateOf(-1) }


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
                            if (player == 1 && !playedCategories1[i]){
                                clickedButtonIndex = i * 2 + 1
                                onCategorySelect1(clickedButtonIndex) // Aggiorna la variabile globale
                                playedCategory = i + 1
                                Log.d("MultiplayerActivity", "Player $player selected category: ${clickedButtonIndex + 1}")
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
                        //Text(text = "Button ${i * 2 + 1}")

                        if (player==1 && scorePreview1.isNotEmpty() && scorePreview1[i] != -1 && !playedCategories1[i]) {
                            Text(
                                text = scorePreview1[i].toString(), // Mostra il punteggio se non è -1
                                color = if (playedCategories1[i] && player==1) Color.White else Color.Black // Bianco se selezionato, nero altrimenti
                            )
                        }
                        if (scoreList1[i] != 0) {
                            // la categoria è già stata giocata
                            Text(
                                text = scoreList1[i].toString(),
                                color = Color.White
                            )
                        }

                    }
                    Button( //player 2
                        onClick = {
                            if (player == 2 && !playedCategories2[i]){
                                clickedButtonIndex = i * 2 + 2
                                onCategorySelect2(clickedButtonIndex) // Aggiorna la variabile globale
                                playedCategory = i + 1
                                Log.d("MultiplayerActivity", "Player $player selected category: ${clickedButtonIndex + 1}")

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
                        //Text(text = "Button ${i * 2 + 2}")
                        if (player == 2 && scorePreview2.isNotEmpty() && scorePreview2[i] != -1 && !playedCategories2[i]) {
                            Text(
                                text = scorePreview2[i].toString(), // Mostra il punteggio se non è -1
                                color = if (playedCategories2[i]) Color.White else Color.Black // Bianco se selezionato, nero altrimenti
                            )
                        }
                        if (scoreList2[i] != 0) {
                            // la categoria è già stata giocata
                            Text(
                                text = scoreList2[i].toString(),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
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
