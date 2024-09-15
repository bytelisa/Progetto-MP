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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.material3.ButtonDefaults

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.VES.yahtzee.ui.theme.YahtzeeTheme


class MultiplayerActivity : ComponentActivity() {

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

                            oldMultiPlayer(
                                player = currentPlayer,
                                onTurnEnd = {nextPlayer()},
                                categoryToPlay = categoryToPlay
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
                                //state1 = SinglePlayerState(),
                                //state2 = SinglePlayerState()
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
fun oldMultiPlayer(player: Int, onTurnEnd: () -> Unit, categoryToPlay: Int) {
    // Usa il Saver per ricordare lo stato dei giocatori
    var player1State by rememberSaveable(stateSaver = SinglePlayerStateSaver) { mutableStateOf(SinglePlayerState()) }
    var player2State by rememberSaveable(stateSaver = SinglePlayerStateSaver) { mutableStateOf(SinglePlayerState()) }

    val currentState = if (player == 1) player1State else player2State

    SinglePlayerScreen(
        state = currentState,
        onTurnEnd = { onTurnEnd() }
    )

    ScoreTableM(
        currentPlayer = player,
        scorePreview1 = player1State.scorePreviewList,
        scorePreview2 = player2State.scorePreviewList,
        scoreList1 = player1State.scoreList,
        scoreList2 = player2State.scoreList,
        playedCategories1 = player1State.playedCategories,
        playedCategories2 = player2State.playedCategories,
        onCategorySelect1 = { newCategory ->
            player1State.categoryToPlay = newCategory
        },
        onCategorySelect2 = { newCategory ->
            player2State.categoryToPlay = newCategory
        }
    )
}


@Composable
fun SinglePlayerScreen(
    state: SinglePlayerState,
    onTurnEnd: () -> Unit
) {
    SinglePlayer(
        categoryToPlay = state.categoryToPlay,
        onCategoryToPlayChange = { newCategory ->
            state.categoryToPlay = newCategory
        },
        onTurnEnd = onTurnEnd,
        usedByMultiplayer = true,
        playerState = state
    )
}




/*
@Composable
fun MultiPlayer(player: Int, onTurnEnd: () -> Unit, categoryToPlay: Int) {

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


    var player1State = remember { SinglePlayerState() }
    var player2State = remember { SinglePlayerState() }

    if (currentPlayer == 1) {
          player1State = singlePlayerScreen(
              state = player1State,
              onTurnEnd = onTurnEnd // Passa la gestione della fine del turno
          )!!
    } else if (currentPlayer == 2){
        player2State = singlePlayerScreen(
            state = player2State,
            onTurnEnd = onTurnEnd // Passa la gestione della fine del turno
        )!!
    }

    var currentCategoryToPlay by rememberSaveable {
        mutableIntStateOf(-1)
    }
    val rolledDice by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            //ho tolto i bottoni perché usiamo quelli delle rispettive activity singlePlayer
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

        ScoreTableM(
            currentPlayer = player,
            scorePreview1 = player1State.scorePreviewList,
            scorePreview2 = player2State.scorePreviewList,
            scoreList1 = List(14){0},
            scoreList2 = List(14){0},
            playedCategories1 = player1State.playedCategories,
            playedCategories2 =player2State.playedCategories,
            onCategorySelect1 = { newCategory ->
                currentCategoryToPlay = newCategory
            },
            onCategorySelect2 = { newCategory ->
                currentCategoryToPlay = newCategory
            },
            state1 = player1State,
            state2 = player2State
        )
    }
}
*/
/*
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
*/

class SinglePlayerState {
    var rolls by mutableStateOf(0)
    var rounds by mutableStateOf(0)
    var scoreList = mutableStateListOf(*List(14) { 0 }.toTypedArray())
    var playedCategories = mutableStateListOf(*List(14) { false }.toTypedArray())
    var totalScore by mutableStateOf(0)
    var categoryToPlay by mutableStateOf(0)
    var scorePreviewList = mutableStateListOf(*List(14) { -1 }.toTypedArray())
    var rolledDice by mutableStateOf<List<Int>>(emptyList())
}

/*
@Composable
fun singlePlayerScreen(
    state: SinglePlayerState?,
    onTurnEnd: () -> Unit
): SinglePlayerState? {
    var currentState by rememberSaveable{ mutableStateOf(state) }

    // Usa il singolo stato del giocatore, simile a come fai nel singleplayer
    if (state != null) {
        currentState = SinglePlayer(
            categoryToPlay = state.categoryToPlay,
            onCategoryToPlayChange = { newCategory ->
                state.categoryToPlay = newCategory
            },
            onTurnEnd = onTurnEnd,
            usedByMultiplayer = true,
            playerState = state,)
        return currentState

    } else {
        return null
    }
}
*/
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





// Crea un Saver per SinglePlayerState
val SinglePlayerStateSaver: Saver<SinglePlayerState, Any> = Saver(
    save = { state ->
        // Definisci come salvare lo stato. Restituisci una lista dei campi salvabili.
        listOf(
            state.rolls,
            state.rounds,
            state.scoreList.toList(),           // Converte la SnapshotStateList in una normale lista per salvarla
            state.playedCategories.toList(),
            state.totalScore,
            state.categoryToPlay,
            state.scorePreviewList.toList(),
            state.rolledDice.toList()
        )
    },
    restore = { savedData ->
        // Ripristina lo stato a partire dalla lista di campi salvati
        val data = savedData as List<*>
        SinglePlayerState().apply {
            rolls = data[0] as Int
            rounds = data[1] as Int
            scoreList = mutableStateListOf(*((data[2] as List<Int>).toTypedArray()))  // Converte una normale lista in SnapshotStateList
            playedCategories = mutableStateListOf(*((data[3] as List<Boolean>).toTypedArray()))
            totalScore = data[4] as Int
            categoryToPlay = data[5] as Int
            scorePreviewList = mutableStateListOf(*((data[6] as List<Int>).toTypedArray()))
            rolledDice = data[7] as List<Int>
        }
    }
)

