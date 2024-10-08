package it.VES.yahtzee

import android.annotation.SuppressLint

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import it.VES.yahtzee.ui.theme.YahtzeeTheme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import it.VES.yahtzee.db.User
import it.VES.yahtzee.db.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SingleplayerActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var diceRollLauncher: ActivityResultLauncher<Intent>
    private var scorePlaceholder = List(14) { -1 }
    private var categoryToPlay by mutableIntStateOf(-1)
    private var rolledDice by mutableStateOf(List(5) { 1 })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ottieni il ViewModel
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        diceRollLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val diceResults = result.data?.getIntArrayExtra("diceResults") //prendiamo come risultato diceResults
                diceResults?.let {
                    onDiceRolled(it.toList())   //usiamo questa callback per riportare i risultati alla funzione composable
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            BackgroundSingleplayer()
                            SinglePlayer(
                                categoryToPlay = categoryToPlay,
                                onCategoryToPlayChange = { newCategory ->
                                    categoryToPlay = newCategory
                                },
                                onGameFinish = { totalScore ->
                                    // Quando il gioco finisce, salva i dati nel DB
                                    saveGameToDB(totalScore as Int)
                                },
                                rolledDice = rolledDice,
                                diceRollLauncher = diceRollLauncher,
                                onDiceRolled = { rolledDiceResult ->
                                    // lambda che gestisce il risultato
                                    rolledDice = rolledDiceResult
                                    Log.d("SinglePlayerActivity", "Rolled Dice: $rolledDiceResult")
                                }
                            )

                            ScoreTable(
                                scorePreviewList = scorePlaceholder,
                                onCategorySelect = { newCategory ->
                                    categoryToPlay = newCategory
                                },
                                scoreList = List(14) { 0 },
                                playedCategories = List(14) { false },
                                playPressed = false
                            )
                        }
                    }
                )
            }
        }
    }

    //funzione di callback triggerata dalla ricezione di un nuovo risultato da parte di DiceRollActivity
    private fun onDiceRolled(diceResults: List<Int>) {
        rolledDice = diceResults
        Log.d("SinglePlayerActivity", "Dice rolled: $diceResults")
    }

    private fun saveGameToDB(score: Int) {

        // Recupera il contesto e le SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "NomeGiocatore") ?: "NomeGiocatore"


        val user = User(
            player = userName, // Usa il nome recuperato dalle SharedPreferences
            score = score.toString(),   // Converti il punteggio in stringa
            mod = "Singleplayer",
            date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())  // Data e ora correnti
        )

        Log.d("SingleplayerActivity", "Dati da salvare: $user")


        try {
            userViewModel.insert(user)   // Salva nel database
            Log.d("SingleplayerActivity", "Salvataggio riuscito")
        } catch (e: Exception) {
            Log.e("SingleplayerActivity", "Errore durante il salvataggio: ${e.message}")
        }

    }

}

    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun SinglePlayer(
        categoryToPlay: Int,
        onCategoryToPlayChange: (Int) -> Unit,
        onGameFinish: (Int?) -> Unit,
        rolledDice: List<Int>,
        diceRollLauncher: ActivityResultLauncher<Intent>,
        onDiceRolled: (List<Int>) -> Unit, // Callback che gestisce risultato
    ) {
        val context = LocalContext.current
        var rolls by rememberSaveable { mutableIntStateOf(0) } // max 3
        var rounds by rememberSaveable { mutableIntStateOf(0) } // max 13
        var diceResults by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
        var showDialog by remember { mutableStateOf(false) }
        val scorePreviewList = remember { mutableStateListOf(*List(14) { -1 }.toTypedArray()) }
        val scoreList = remember { mutableStateListOf(*List(14) { 0 }.toTypedArray()) }
        var totalScore by remember { mutableIntStateOf(0) }
        var gameFinished by rememberSaveable { mutableStateOf(false) }
        val playedCategories = remember { mutableStateListOf(*List(14) { false }.toTypedArray()) }
        var playPressed by rememberSaveable { mutableStateOf(false) }
        var rollPressed by rememberSaveable { mutableStateOf(false) }
        var previousCategory by rememberSaveable { mutableIntStateOf(-1) }
        val sharedPreferences=context.getSharedPreferences("user_prefs",Context.MODE_PRIVATE)
        val soundEnabled=sharedPreferences.getBoolean("soundEnabled",false)

        val clickedStates = remember { mutableStateListOf(*List(5) { false }.toTypedArray()) }
        val shake = shakeMode(context = context)
        var scorePreview: List<Int>

        // When the dice are rolled, update the rolledDice variable
        fun handleDiceRoll(diceResults: List<Int>) {
            onDiceRolled(diceResults)
        }
        LaunchedEffect(rolledDice) {
            if ((rounds != 0 && !shake) || (shake && rolls>0)){
                scorePreview = PlayUtils().getScorePreview(rolledDice)
                scorePreviewList.clear()
                scorePreviewList.addAll(scorePreview)
            }

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
                        if (rolls < 3) {
                            playPressed = false

                            if (shake) {
                                rolls += 1
                                val intent = Intent(context, DiceRollActivity::class.java)
                                diceRollLauncher.launch(intent)
                                rollPressed = true
                                Log.d("SinglePlayerActivity", "Roll with sensor: $rolledDice")
                                scorePreview = PlayUtils().getScorePreview(rolledDice) //non è più di stato per singleplayer!

                            } else {
                                    diceResults = if (rolls == 0) {
                                    DiceRollActivity().rollDice().toMutableList()

                                } else {
                                    DiceRollActivity().rollDiceStates(diceResults, clickedStates)
                                        .toMutableList()
                                }
                                handleDiceRoll(diceResults)
                                rollPressed = true
                                rolls += 1
                                scorePreview = PlayUtils().getScorePreview(diceResults)
                            }

                            scorePreviewList.clear()
                            scorePreviewList.addAll(scorePreview)
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
                    enabled = rolls < 3,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(200.dp)
                        .height(45.dp),
                ) {
                    Text(
                        text = when {
                            shake -> "Shake! (${3 - rolls} left)"
                            else -> "Roll (${3 - rolls} left)"
                        },
                        fontSize = 16.sp

                    )
                }


            Button(
                onClick = { // play
                    if (rounds < 13) {
                        if (categoryToPlay != -1) {

                            if (!playedCategories[categoryToPlay - 1]){
                                scoreList[categoryToPlay - 1] = scorePreviewList[categoryToPlay - 1]

                                //bonus
                                scoreList[6] = ScoreCalculator().bonusCheck(scoreList)
                                if (scoreList[6] == 35){
                                    playedCategories[6] = true
                                    previousCategory = categoryToPlay - 1
                                }
                                playedCategories[categoryToPlay - 1] = true
                                previousCategory = categoryToPlay - 1
                            }
                        }
                        Log.d("SinglePlayerActivity", "New Score List: $scoreList")
                        Log.d("SinglePlayerActivity", "Round finished: ${rounds + 1}")

                        totalScore = ScoreCalculator().totalScore(scoreList)
                        rolls = 0
                        if (rounds == 12){
                            rounds += 1
                            gameFinished = true
                        } else {
                            rounds += 1
                            scorePreviewList.clear()
                            playPressed = true
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
                enabled = when {
                    (categoryToPlay == -1) -> false
                    else -> (rolls != 0 && !playedCategories[categoryToPlay - 1])},
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

    if (!playPressed) {
        ScoreTable(scorePreviewList, onCategorySelect = { index ->
            onCategoryToPlayChange(index)
        }, scoreList, playedCategories, playPressed)
    }

    if (rounds > 13){
        gameFinished = true
    }


    if (gameFinished) {
        GameFinish(score = totalScore, onConfirm = {

            onGameFinish(totalScore)
        })
    }

    Score(totalScore)
    PlayUtils().RoundsLeft(rounds)

    if (rollPressed){
        ScoreTable(scorePreviewList, onCategorySelect = { index ->
            onCategoryToPlayChange(index)
        }, scoreList, playedCategories, playPressed)

    }
    }

}

fun shakeMode(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("clickMode", false)
}


@Composable
fun GameFinish(score: Int, onConfirm: () -> Unit) {

    val activity = (LocalContext.current as? Activity)  // Ottieni l'activity corrente
    var gameFinished by rememberSaveable {mutableStateOf(true)}

    AlertDialog(
        onDismissRequest = { gameFinished = false },
        title = { Text("Game Finished") },
        text = { Text("Your total score is $score!") },
        confirmButton = {
            Button(onClick = {
                onConfirm()  // Chiama la funzione che salva i dati
                activity?.finish()  // Chiudi l'attività e torna alla schermata precedente
            }) {
                Text("OK")
            }
        }
    )
}


@Composable
fun ScoreTable(
    scorePreviewList: List<Int>,
    onCategorySelect: (Int) -> Unit,
    scoreList: List<Int>,
    playedCategories: List<Boolean>,
    playPressed: Boolean = false
) {

    var clickedButtonIndex by remember { mutableIntStateOf(-1) }
    var playedCategory by remember { mutableIntStateOf(-1) }
    val justPlayed by remember { derivedStateOf { playPressed } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 70.dp, top = 1.dp, bottom = 90.dp) // necessario a spostare i bottoni
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {

            for (i in 0..13) {
                Button(
                    onClick = {
                        if (!playedCategories[i] && i!=6) { // se la categoria non è già stata giocata e se non è il punteggio bonus
                            clickedButtonIndex = i
                            onCategorySelect(clickedButtonIndex + 1) // Aggiorna la variabile globale
                            playedCategory = i
                            Log.d("SinglePlayerActivity", "#selected category: ${clickedButtonIndex + 1}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            playedCategories[i] -> Color(0x9E80C0DD)
                            (clickedButtonIndex == i && !justPlayed) -> Color(0x99DA4141)
                            else -> Color.Transparent
                        },
                        disabledContainerColor = when {     //per il bottone disabilitato
                            i==6 && !playedCategories[6]-> Color.Transparent
                            else -> Color(0x9E80C0DD)
                        },
                        contentColor = when {
                            clickedButtonIndex == i -> Color.White
                            else -> Color.DarkGray},
                        disabledContentColor = Color.Black
                    ),
                    enabled = when {
                        i == 6 -> false
                        else -> !playedCategories[i]},

                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .width(100.dp)
                        .height(30.dp)
                        .offset(x = 0.dp, y = (i * 2.5).dp)
                ) {
                    if (scorePreviewList.isNotEmpty() && scorePreviewList[i] != -1 && !playedCategories[i]) {

                        Text(
                            text = scorePreviewList[i].toString(),
                            color = when {
                                clickedButtonIndex == i -> Color.White
                                else -> Color.DarkGray},
                            fontSize = 16.sp
                        )
                    }
                    if (playedCategories[i]) {
                        Text(
                            text = scoreList[i].toString(),
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }

        }
    }
}


@Composable
fun Score(score: Int) {

    Box(
        modifier = Modifier
    ) {
        Text(
            text = score.toString(),
            fontSize = 35.sp, // Big
            color = Color.White, // Colore del testo
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(start = when{
                    score < 100 -> 320.dp
                    else -> 300.dp},
                    top = 28.dp, end = 10.dp
                )

        )
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

