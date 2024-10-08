package it.VES.yahtzee

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import it.VES.yahtzee.db.User
import it.VES.yahtzee.db.UserViewModel
import it.VES.yahtzee.ui.theme.YahtzeeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MultiPlayerActivity : ComponentActivity() {


    private lateinit var userViewModel: UserViewModel

    private var playerOneScore: Int = 0
    private var playerTwoScore: Int = 0
    private lateinit var playerOneName: String
    private lateinit var playerTwoName: String

    private var currentPlayer by mutableIntStateOf(1)
    private var categoryToPlay by mutableIntStateOf(-1)

    private var gameFinished by mutableStateOf(false)
    private var winningPlayer: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        playerOneName = intent.getStringExtra("PLAYER_ONE_NAME") ?: "Player 1"
        playerTwoName = intent.getStringExtra("PLAYER_TWO_NAME") ?: "Player 2"


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


                            val playerOne = User(
                                player = playerOneName,
                                score = playerOneScore.toString(),
                                mod = "Multiplayer",
                                date = System.currentTimeMillis().toString()
                            )

                            Log.d("GameLog11", "Punteggio di $playerOneName aggiornato: $playerOneScore")


                            val playerTwo = User(
                                player = playerTwoName,
                                score = playerTwoScore.toString(),
                                mod = "Multiplayer",
                                date = System.currentTimeMillis().toString()
                            )

                            Log.d("GameLog11", "Punteggio di $playerTwoName aggiornato: $playerTwoScore")




                            if (gameFinished) {



                                WinningPlayer(
                                    winner = winningPlayer,
                                    score = if (winningPlayer == 1) playerOneScore else playerTwoScore,
                                    playerOne = playerOne,
                                    playerTwo = playerTwo,
                                    onDismiss = {
                                        saveGameResult(playerOneScore, playerTwoScore)
                                    }
                                )



                            } else {


                                MultiPlayer(
                                    currentPlayer = currentPlayer,
                                    categoryToPlay = categoryToPlay,
                                    onCategoryToPlayChange = { newCategory ->
                                        categoryToPlay = newCategory
                                    },
                                    onTurnEnd = { nextPlayer() },
                                    playerOne = playerOne,
                                    playerTwo = playerTwo,
                                    onGameFinish = { score1, score2 ->
                                        playerOneScore = score1
                                        playerTwoScore = score2
                                        gameFinished = true
                                        winningPlayer = if (score1 > score2) 1 else if (score2 > score1) 2 else 0
                                    }
                                )



                                ScoreTableM(
                                    currentPlayer = currentPlayer,
                                    scorePreview1 = List(14) { -1 },
                                    scorePreview2 = List(14) { -1 },
                                    scoreList1 = List(14) { 0 },
                                    scoreList2 = List(14) { 0 },
                                    playedCategories1 = List(14) { false },
                                    playedCategories2 = List(14) { false },
                                    onCategorySelect1 = { newCategory ->
                                        categoryToPlay = newCategory

                                    },
                                    onCategorySelect2 = { newCategory ->
                                        categoryToPlay = newCategory

                                    },

                                    )

                                var showNameDialog by remember { mutableStateOf(true) }
                                var player1Name by remember { mutableStateOf("") }
                                var player2Name by remember { mutableStateOf("") }

                                if (showNameDialog) {
                                    NamesPopup(onDismiss = { name1, name2 ->
                                        player1Name = name1
                                        player2Name = name2
                                        playerOneName = player1Name
                                        playerTwoName = player2Name
                                        showNameDialog = false
                                    })
                                }

                                if (!showNameDialog) {
                                    PlayersNames(player1 = playerOneName, player2 = playerTwoName)

                                }
                            }
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



    private fun saveGameResult(playerOneScore: Int, playerTwoScore: Int) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())


        val playerOne = User(
            id = 0,
            player = playerOneName,
            score = playerOneScore.toString(),
            mod = "Multiplayer",
            date = currentDate
        )


        val playerTwo = User(
            id = 0,
            player = playerTwoName,
            score = playerTwoScore.toString(),
            mod = "Multiplayer",
            date = currentDate
        )


        userViewModel.insert(playerOne)
        userViewModel.insert(playerTwo)

        Log.d("MultiplayerActivity11", "Dati salvati per i giocatori: $playerOneName e $playerTwoName")

        finish()
    }
}




@Composable
fun MultiPlayer(currentPlayer: Int,
                categoryToPlay: Int,
                onCategoryToPlayChange: (Int) -> Unit,
                onTurnEnd: (() -> Unit),
                playerOne: User,
                playerTwo: User,
                onGameFinish: (Int, Int) -> Unit){
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
    val clickedStates = remember { mutableStateListOf(*List(5) { false }.toTypedArray()) } //deve essere ricordabile perché va riaggiornata la schermata quando cambia
    val sharedPreferences=context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val soundEnabled=sharedPreferences.getBoolean("soundEnabled",false)

    if (currentPlayer == 1){
        Score(totalScore1)
        PlayUtils().RoundsLeft(rounds1)

    } else {
        Score(totalScore2)
        PlayUtils().RoundsLeft(rounds = rounds2)
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
                    if(soundEnabled){
                        playDiceSound(context)
                    }
                    Log.d("MultiPlayerActivity11", "Roll button clicked: rolls = $rolls")

                    if (rolls < 3) {
                        playPressed = false

                        rolledDice = if (rolls == 0){
                            DiceRollActivity().rollDice().toMutableList()

                        } else {
                            DiceRollActivity().rollDiceStates(rolledDice, clickedStates).toMutableList()
                        }

                        rolls += 1
                        onCategoryToPlayChange(-1)
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
                Text(text = "Roll (${3 - rolls} left)",
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = { // play
                    if (currentPlayer == 1){
                        if (rounds1 < 13) {
                            if (categoryToPlay != -1 && !playedCategories1[categoryToPlay - 1]) {

                                Log.d("MultiPlayerActivity11", "Player 1 selecting category $categoryToPlay")

                                scoreList1[categoryToPlay - 1] = scorePreviewList1[categoryToPlay - 1]

                                //punto bonus
                                scoreList1[6] = ScoreCalculator().bonusCheck(scoreList1)
                                if (scoreList1[6] == 35){
                                    playedCategories1[6] = true
                                    previousCategory = categoryToPlay - 1
                                }

                                playedCategories1[categoryToPlay - 1] = true
                                previousCategory = categoryToPlay - 1
                                Log.d(
                                    "MultiPlayerActivity11",
                                    "#selected score: ${scoreList2[categoryToPlay - 1]}"
                                )

                            } else {
                                showPlayDialog = true
                            }
                            Log.d("MultiPlayerActivity11", "New Score List player 1: $scoreList1")
                            Log.d("MultiPlayerActivity11", "Round finished for player 1: ${rounds1 + 1}")

                            rolls = 0
                            rounds1 += 1
                            scorePreviewList1.clear()
                            playPressed = true
                            totalScore1 = ScoreCalculator().totalScore(scoreList1)
                            turnEndDialog = true

                        }

                    } else if (currentPlayer == 2){
                        if (rounds2 < 13) {

                            if (categoryToPlay != -1 && !playedCategories2[categoryToPlay - 1]) {
                                Log.d("MultiPlayerActivity", "Player 2 selected category: $categoryToPlay")

                                scoreList2[categoryToPlay - 1] = scorePreviewList2[categoryToPlay - 1]

                                //bonus
                                scoreList2[6] = ScoreCalculator().bonusCheck(scoreList2)
                                if (scoreList2[6] == 35){
                                    playedCategories2[6] = true
                                    previousCategory = categoryToPlay - 1
                                }

                                playedCategories2[categoryToPlay - 1] = true
                                previousCategory = categoryToPlay - 1
                                Log.d(
                                    "MultiPlayerActivity11",
                                    "#selected score: ${scoreList2[categoryToPlay - 1]}"
                                )

                            } else {
                                showPlayDialog = true
                            }
                            Log.d("MultiPlayerActivity11", "New Score List player 2: $scoreList2")
                            Log.d("MultiPlayerActivity11", "Round finished for player 2: ${rounds2 + 1}")

                            rolls = 0
                            rounds2 += 1
                            scorePreviewList2.clear()
                            playPressed = true
                            totalScore2 = ScoreCalculator().totalScore(scoreList2)
                            turnEndDialog = true
                        }
                    }

                    if (rounds1 >= 13 && rounds2 >= 13) {
                        Log.d("MultiPlayerActivity11", "Game finished: rounds1 = $rounds1, rounds2 = $rounds2")
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
                enabled = when {
                    categoryToPlay == -1 -> false
                    currentPlayer == 1 -> ((rolls != 0) && (!playedCategories1[categoryToPlay - 1]))
                    else -> ((rolls != 0) && (!playedCategories2[categoryToPlay - 1]))
                               },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(100.dp)
                    .height(45.dp),
            ) {
                Text(text = "Play",
                    fontSize = 16.sp
                )
            }

        }



        if (rolledDice.isNotEmpty() && rolls != 0) {
            val rotationValues = listOf(0f, 15f, -10f, 20f, -5f)

            val newClickedStates = PlayUtils().imageSequence(rolledDice, rotationValues = rotationValues, context)

            clickedStates.clear()
            clickedStates.addAll(newClickedStates)
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
                        Log.d("MultiPlayerActivity11", "Turn end confirmed, calling onTurnEnd()")
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

        // Determina il vincitore
        val winner = if (totalScore1 > totalScore2) 1 else if (totalScore2 > totalScore1) 2 else 0
        val winningScore = if (winner == 1) totalScore1 else totalScore2   //pareggio

        Log.d("winner11", "punteggi : player1 - player 2: $totalScore1 - $totalScore2")

        WinningPlayer(
            winner = winner,
            score = winningScore,
            playerOne = playerOne.copy(score = totalScore1.toString()), // Passa il punteggio aggiornato
            playerTwo = playerTwo.copy(score = totalScore2.toString()), // Passa il punteggio aggiornato
            onDismiss = {
                onGameFinish(totalScore1, totalScore2)
            }
        )
    }

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
    onCategorySelect2: (Int) -> Unit
) {
    var clickedButtonIndex by remember { mutableIntStateOf(-1) }

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

                    Button(
                        onClick = {

                            if (currentPlayer == 1 && !playedCategories1[i]) {
                                clickedButtonIndex = i * 2 + 1
                                onCategorySelect1(i+1)
                            }
                        },

                        colors = ButtonDefaults.buttonColors(

                            containerColor = when {
                                playedCategories1[i] -> Color(0xFF80C0DD)
                                (clickedButtonIndex == i * 2 + 1 && currentPlayer == 1) -> Color(
                                    0x943688AD
                                )
                                else -> Color.Transparent
                            },

                            disabledContainerColor = when {     //per il bottone disabilitato
                                i==6 && !playedCategories1[6]-> Color.Transparent
                                else -> Color(0x9C9FD8F1)
                            },

                            contentColor = Color.DarkGray,
                            disabledContentColor = Color.Black
                        ),
                        enabled = when {
                            i == 6 -> false
                            else -> !playedCategories1[i]
                                       },

                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(80.dp)
                            .height(30.dp)
                            .offset(x = 19.dp, y = (i * 2.5).dp)
                    ) {
                        if (scorePreview1.isNotEmpty() && scorePreview1[i] != -1 && !playedCategories1[i]) {
                            Text(
                                text = scorePreview1[i].toString(),
                                color = when {
                                    clickedButtonIndex == i -> Color.White
                                    else -> Color.DarkGray},
                                fontSize = 16.sp

                            )
                        }
                        if (playedCategories1[i]) {
                            Text(
                                text = scoreList1[i].toString(),
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }


                    Button( // Player 2 buttons
                        onClick = {
                            Log.d("MultiPlayerActivity", "currentPlayer: $currentPlayer")

                            if (currentPlayer == 2 && !playedCategories2[i]) {
                                clickedButtonIndex = i * 2 + 2
                                onCategorySelect2(i+1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(

                            containerColor = when {
                                playedCategories2[i] -> Color(0xA6E69696)
                                (clickedButtonIndex == i * 2 + 2 && currentPlayer == 2) -> Color(
                                    0x90DA4141
                                )
                                else -> Color.Transparent
                            },
                            disabledContainerColor = when {     //per il bottone disabilitato
                                i==6 && !playedCategories2[6]-> Color.Transparent
                                else -> Color(0xA9D39B98)
                            },
                            contentColor = Color.DarkGray,
                            disabledContentColor = Color.Black
                            ),

                        enabled = when {
                            i == 6 -> false
                            else -> !playedCategories2[i]},

                        modifier = Modifier
                            .width(80.dp)
                            .height(30.dp)
                            .offset(x = 19.dp, y = (i * 2.5).dp)
                    ) {
                        if (scorePreview2.isNotEmpty() && scorePreview2[i] != -1 && !playedCategories2[i]) {
                            Text(
                                text = scorePreview2[i].toString(),
                                color = when {
                                    clickedButtonIndex == i -> Color.White
                                    else -> Color.DarkGray},
                                fontSize = 16.sp
                            )
                        }
                        if (playedCategories2[i]) {
                            Text(
                                text = scoreList2[i].toString(),
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayersNames(player1: String, player2: String) {
    Box(
        modifier = Modifier
            .padding(start = 150.dp, end = 30.dp, top = 68.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 19.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = player1,
                modifier = Modifier.weight(1f), // Divide lo spazio equamente tra i due Text
                textAlign = TextAlign.Center,
                fontSize = 16.sp

            )
            Text(
                text = player2,
                modifier = Modifier
                            .weight(1f)
                            .padding(start = 30.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp

            )
        }
    }
}


@Composable
fun WinningPlayer(winner: Int,
                  score: Int,
                  playerOne: User,     // Passa i giocatori
                  playerTwo: User,
                  onDismiss: () -> Unit // Aggiungi una funzione di callback per la chiusura
){    //funzione di salvataggio

    var gameFinished by rememberSaveable {mutableStateOf(true)}


    if (gameFinished) {
        AlertDialog(
            onDismissRequest = { gameFinished = false
                onDismiss()
                },
                title = {
                Text(
                    text = "Player #$winner wins!",
                    fontSize = 35.sp, // Big
                )
            },
            text = {
                Column {
                    Text("You scored $score points.")
                }
            },
            confirmButton = {
                Button(onClick = {
                    gameFinished = false

                    onDismiss() // Chiama il callback quando l'utente clicca su OK
                }) {
                    Text("OK")
                }
            },
        )
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamesPopup(onDismiss: (String, String) -> Unit){

    var open by rememberSaveable {mutableStateOf(true)}
    var player1 by remember { mutableStateOf("") }
    var player2 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { open = false },
        title = { Text(
            text = "Who's playing?",
            fontSize = 25.sp, // Big
        ) },
        text = {
            Column {

                TextField(
                    value = player1,
                    onValueChange = {player1 = it},
                    label = { Text(
                                    text = "Player 1:",
                                    color = Color(0xC80A5699)
                    )
                            },

                    modifier = Modifier
                        .width(250.dp) // Accorcia il TextField
                        .clip(RoundedCornerShape(16.dp)), // Angoli più tondi

                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xD880C0DD), // Blu fumoso (Smoky Blue)
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White // Cambia il colore del cursore
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    value = player2,
                    onValueChange = {player2 = it},
                    label = { Text(
                        text = "Player 2:",
                        color = Color(0xDDC42525)
                    )
                    },

                    modifier = Modifier
                        .width(250.dp)
                        .clip(RoundedCornerShape(16.dp)),

                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xA6E69696),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss(player1, player2) }) {
                Text("OK")
            }
        },
    )
}


