package it.VES.yahtzee

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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


                            NewMultiPlayer(
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

        currentPlayer = if (currentPlayer == 1) {
            2
        } else {
            1
        }
    }
}

@Composable
fun NewMultiPlayer(currentPlayer: Int, categoryToPlay: Int, onCategoryToPlayChange: (Int) -> Unit, onTurnEnd: (() -> Unit)){
    var rolls by rememberSaveable { mutableIntStateOf(0) } // max 3
    var rounds1 by rememberSaveable { mutableIntStateOf(0) } // max 13
    var rounds2 by rememberSaveable { mutableIntStateOf(0) } // max 13
    var rolledDice by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showPlayDialog by remember { mutableStateOf(false) }
    val scorePreviewList1 = remember { mutableStateListOf(*List(14) { -1 }.toTypedArray()) }
    val scorePreviewList2 = remember { mutableStateListOf(*List(14) { -1 }.toTypedArray()) }
    val scoreList1 = remember { mutableStateListOf(*List(14) { 0 }.toTypedArray()) }
    val scoreList2 = remember { mutableStateListOf(*List(14) { 0 }.toTypedArray()) }
    var totalScore1 by remember { mutableIntStateOf(0) }
    var totalScore2 by remember { mutableIntStateOf(0) }
    var gameFinished by rememberSaveable { mutableStateOf(false) }
    val playedCategories1 = remember { mutableStateListOf(*List(14) { false }.toTypedArray()) }
    val playedCategories2 = remember { mutableStateListOf(*List(14) { false }.toTypedArray()) }
    var playPressed by rememberSaveable { mutableStateOf(false) }
    var previousCategory by rememberSaveable { mutableIntStateOf(-1) }
    var turnEndDialog by rememberSaveable { mutableStateOf(false) }


    if (currentPlayer == 1){
        Score(totalScore1)
        RoundsLeft(rounds1)

    } else {
        Score(totalScore2)
        RoundsLeft(rounds = rounds2)
    }

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
                        if (currentPlayer == 1){
                            scorePreviewList1.clear()
                            scorePreviewList1.addAll(scorePreview)
                        } else {
                            scorePreviewList2.clear()
                            scorePreviewList2.addAll(scorePreview)
                        }
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
                enabled=rolls<3,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(200.dp)
                    .height(45.dp),
            ) {
                Text(text = "Roll (${3 - rolls} left)")
            }

            Button(
                onClick = { // play
                    if (currentPlayer == 1){
                        if (rounds1 < 13) {
                            if (categoryToPlay != -1) {
                                scoreList1[categoryToPlay - 1] = scorePreviewList1[categoryToPlay - 1]
                                //scoreList1[6] = ScoreCalculator().bonusCheck(scoreList1)
                                playedCategories1[categoryToPlay - 1] = true
                                previousCategory = categoryToPlay - 1
                                Log.d(
                                    "MultiPlayerActivity",
                                    "#selected score: ${scoreList2[categoryToPlay - 1]}"
                                )

                            } else {
                                showPlayDialog = true
                            }
                            Log.d("MultiPlayerActivity", "New Score List player 1: $scoreList1")
                            Log.d("MultiPlayerActivity", "Round finished for player 1: ${rounds1 + 1}")

                            rolls = 0
                            rounds1 += 1
                            scorePreviewList1.clear()
                            playPressed = true
                            totalScore1 = ScoreCalculator().totalScore(scoreList1) //così sotto al pop up viene visualizzato lo score del giocatore corrente
                            turnEndDialog = true

                        }

                    } else if (currentPlayer == 2){
                        if (rounds2 < 13) {
                            if (categoryToPlay != -1) {
                                Log.d("MultiPlayerActivity", "Player 2 selected category: $categoryToPlay")

                                scoreList2[categoryToPlay - 1] = scorePreviewList2[categoryToPlay - 1]
                                //scoreList2[6] = ScoreCalculator().bonusCheck(scoreList2)
                                playedCategories2[categoryToPlay - 1] = true
                                previousCategory = categoryToPlay - 1
                                Log.d(
                                    "MultiPlayerActivity",
                                    "#selected score: ${scoreList2[categoryToPlay - 1]}"
                                )

                            } else {
                                showPlayDialog = true
                            }
                            Log.d("MultiPlayerActivity", "New Score List player 2: $scoreList2")
                            Log.d("MultiPlayerActivity", "Round finished for player 2: ${rounds2 + 1}")

                            rolls = 0
                            rounds2 += 1
                            scorePreviewList2.clear()
                            playPressed = true
                            totalScore2 = ScoreCalculator().totalScore(scoreList2)
                            turnEndDialog = true
                        }
                    }

                    if (rounds1 >= 13 && rounds2 >= 13) {
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
                enabled = rolls!=0,
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

        if (turnEndDialog) {

            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = when {
                            currentPlayer == 1 -> "+ ${scoreList1[categoryToPlay - 1]}!"
                            else -> "+ ${scoreList2[categoryToPlay - 1]}!"
                        },
                        color = when {
                            currentPlayer == 1 -> Color.Blue
                            else -> Color.Red
                        }
                    )
                },

                text = {
                    Column {
                        Text(
                            text = when {
                                currentPlayer == 1 -> "It's player #2's turn!"
                                else -> "It's player #1's turn!"
                            }
                        )

                    }
                },
                confirmButton = {
                    Button(onClick = {
                        turnEndDialog = false
                        onTurnEnd()

                    }) {
                        Text("OK")

                    }
                },
            )
        }
    }

    if (!playPressed) {
        ScoreTableM(
            currentPlayer= currentPlayer,
            scorePreview1 = scorePreviewList1,
            scorePreview2 = scorePreviewList2,
            scoreList1 = scoreList1,
            scoreList2 = scoreList2,
            playedCategories1 = playedCategories1,
            playedCategories2 = playedCategories2,
            onCategorySelect1 = { index ->
                onCategoryToPlayChange(index)
            },
            onCategorySelect2 = { index ->
                onCategoryToPlayChange(index)
            },

            )
    }

    if (gameFinished) {
        WinningPlayer(currentPlayer, score = when {
            totalScore1 > totalScore2 -> totalScore1
            totalScore2 > totalScore1 -> totalScore2
            else -> totalScore1 //pareggio
        })
    }



    ScoreTableM(
        currentPlayer= currentPlayer,
        scorePreview1 = scorePreviewList1,
        scorePreview2 = scorePreviewList2,
        scoreList1 = scoreList1,
        scoreList2 = scoreList2,
        playedCategories1 = playedCategories1,
        playedCategories2 = playedCategories2,
        onCategorySelect1 = { index -> onCategoryToPlayChange(index) },
        onCategorySelect2 = { index -> onCategoryToPlayChange(index) },
        currentPlayerColor=if(currentPlayer==1)Color.LightGray else Color.LightGray
    )



}

@Composable
fun ScoreTableM(
    currentPlayer: Int,
    scorePreview1: List<Int>,
    scorePreview2: List<Int>,
    scoreList1: List<Int>,
    scoreList2: List<Int>,
    playedCategories1: List<Boolean>,
    playedCategories2: List<Boolean>,
    onCategorySelect1: (Int) -> Unit,
    onCategorySelect2: (Int) -> Unit,
    currentPlayerColor: Color = Color.LightGray
) {
    var clickedButtonIndex by remember { mutableStateOf(-1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 70.dp, top = 1.dp, bottom = 90.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            for (i in 0 until 14) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    // Player 1 buttons
                    Button(
                        onClick = {
                            if (currentPlayer == 1 && !playedCategories1[i]) {
                                onCategorySelect1(i + 1)
                                clickedButtonIndex = i * 2 + 1
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                playedCategories1[i] -> Color(0xFF80C0DD)
                                clickedButtonIndex == i * 2 + 1 && currentPlayer == 1 -> Color(0xFF4CAF50)
                                else -> Color.Transparent
                            }
                        ),
                        enabled = !playedCategories1[i],
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(80.dp)
                            .height(30.dp)
                            .offset(x = 19.dp, y = (i * 2.5).dp)
                            .border(
                                width = 2.dp,
                                color = if (currentPlayer == 1) currentPlayerColor else Color.Transparent
                            )
                    ) {
                        if (scorePreview1.isNotEmpty() && scorePreview1[i] != -1 && !playedCategories1[i]) {
                            Text(
                                text = scorePreview1[i].toString(),
                                color = if (playedCategories1[i]) Color.White else Color.Black
                            )
                        }
                        if (scoreList1[i] != 0) {
                            Text(
                                text = scoreList1[i].toString(),
                                color = Color.White
                            )
                        }
                    }

                    // Player 2 buttons
                    Button(
                        onClick = {
                            if (currentPlayer == 2 && !playedCategories2[i]) {
                                clickedButtonIndex = i * 2 + 2
                                onCategorySelect2(i + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                playedCategories2[i] -> Color(0xFF80C0DD)
                                clickedButtonIndex == i * 2 + 2 && currentPlayer == 2 -> Color(0xFF2196F3)
                                else -> Color.Transparent
                            }
                        ),
                        enabled = !playedCategories2[i],
                        modifier = Modifier
                            .width(80.dp)
                            .height(30.dp)
                            .offset(x = 19.dp, y = (i * 2.5).dp)
                            .border(
                                width = 2.dp,
                                color = if (currentPlayer == 2) currentPlayerColor else Color.Transparent
                            )
                    ) {
                        if (scorePreview2.isNotEmpty() && scorePreview2[i] != -1 && !playedCategories2[i]) {
                            Text(
                                text = scorePreview2[i].toString(),
                                color = if (playedCategories2[i]) Color.White else Color.Black
                            )
                        }
                        if (scoreList2[i] != 0) {
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
fun WinningPlayer(winner: Int, score: Int){

    var gameFinished by rememberSaveable {mutableStateOf(true)}


    AlertDialog(
        onDismissRequest = { gameFinished = false },
        title = { Text(
            text = "Player #$winner wins!",
            fontSize = 35.sp, // Big
        ) },
        text = {
            Column {
                Text ("You scored $score points." )
            }
        },
        confirmButton = {
            Button(onClick = {
                gameFinished = false

            }) {
                Text("OK")
            }
        },
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



