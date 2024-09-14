package it.VES.yahtzee


import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import it.VES.yahtzee.ui.theme.YahtzeeTheme
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class SingleplayerActivity : ComponentActivity() {

    private var _categoryToPlay by mutableIntStateOf(-1)
    private var scorePlaceholder = List(14){-1}
    private var categoryToPlay: Int
        get() = _categoryToPlay
        set(value) {
            _categoryToPlay = value
        }

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
                            BackgroundSingleplayer()
                            //per i bottoni roll and play
                            SinglePlayer(
                                categoryToPlay = categoryToPlay,
                                onCategoryToPlayChange = { newCategory ->
                                    categoryToPlay = newCategory
                                }
                            )
                            ScoreTable(     //posiziono la tabella dei punteggi a destra
                                scorePreviewList = scorePlaceholder,
                                onCategorySelect = { newCategory ->
                                    categoryToPlay = newCategory
                                }, scorePlaceholder
                            )
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun SinglePlayer(categoryToPlay: Int, onCategoryToPlayChange: (Int) -> Unit) {

    //queste mi servono per mostrare i dadi quando viene premuto roll
    var rolls  by rememberSaveable { mutableIntStateOf(0) } //max 3
    var rounds  by rememberSaveable { mutableIntStateOf(0) } //max 13
    var rolledDice by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val context = LocalContext.current
    var showDialog  by remember {mutableStateOf(false)}
    var showPlayDialog  by remember {mutableStateOf(false)}
    val scorePreviewList = remember { mutableStateListOf(*List(14) { -1 }.toTypedArray()) }
    val scoreList = remember { mutableStateListOf(*List(14) { -1 }.toTypedArray()) }
    var totalScore by remember { mutableIntStateOf(0) }
    var gameFinished by rememberSaveable {mutableStateOf(false)}

    Box(
        modifier=Modifier
            .fillMaxSize()
    ){

        Row(
            modifier= Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button( //ROLL
                onClick = {

                    if (rolls < 3) {
                        rolledDice = DiceRollActivity().rollDice()
                        rolls+=1
                        val scorePreview = PlayUtils().getScorePreview(rolledDice)
                        scorePreviewList.clear()
                        scorePreviewList.addAll(scorePreview)

                    } else {
                        //finisce il turno di gioco, l'utente deve scegliere un punteggio
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
                modifier= Modifier
                    .padding(end = 8.dp)
                    .width(200.dp)
                    .height(45.dp),
            ) {
                Text(text = "Roll (${ 3 - rolls } left)")
            }

            Button( //PLAY
                onClick = {

                    if (rounds < 13) {
                        if (categoryToPlay != -1) {
                            scoreList[categoryToPlay-1] = scorePreviewList[categoryToPlay-1]
                            Log.d(
                                "SinglePlayerActivity",
                                "#selected score: ${scoreList[categoryToPlay-1]}"
                            )

                        } else {
                            showPlayDialog = true
                        }
                        Log.d("SinglePlayerActivity", "New Score List: $scoreList")
                        Log.d("SinglePlayerActivity", "Round finished: ${rounds+1}")

                        totalScore = ScoreCalculator().totalScore(scoreList)
                        rolls = 0
                        rounds += 1
                        scorePreviewList.clear()

                    } else {
                        //finisce la partita
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

        if (rolledDice.isNotEmpty() && rolls!=0) {
            val rotationValues = listOf(0f, 15f, -10f, 20f, -5f)

            PlayUtils().ImageSequence(
                rolledDice,
                rotationValues = rotationValues,
                context
            )
        }

        if(showDialog){
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

        if(showPlayDialog){
            AlertDialog(
                onDismissRequest = { showPlayDialog = false },
                title = {
                    Text(
                        text = "Play",
                        color = Color.Red
                    )},

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

    if (gameFinished){
        GameFinish(score = totalScore)
    }

    Score(totalScore)
    RoundsLeft(rounds)
    ScoreTable(scorePreviewList, onCategorySelect = { index ->
        onCategoryToPlayChange(index)
    }, scoreList)
}

@Composable
fun ScoreTable(scorePreviewList: List<Int>, onCategorySelect: (Int) -> Unit, scoreList: List<Int>) {

    var clickedButtonIndex by remember { mutableIntStateOf(-1) }

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
                        if (scoreList[i] == -1){
                            clickedButtonIndex = i
                            onCategorySelect(clickedButtonIndex+1) // Aggiorna la variabile globale
                            Log.d("SinglePlayerActivity", "#selected category: ${clickedButtonIndex+1}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            scoreList[i] != -1 -> Color(0xFF80C0DD) // Categoria già giocata, colore fisso (blu)
                            clickedButtonIndex == i && scoreList[i] == -1 -> Color(0xB5DA4141) // Rosso se selezionato e non giocato
                            else -> Color(0xB5D3D3D3) // Colore predefinito se non selezionato e non giocato
                        },
                    ),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .width(80.dp)
                        .height(30.dp)
                        .offset(x = 0.dp, y = (i * 2.5).dp)
                ) {
                    if (scorePreviewList.isNotEmpty() && scorePreviewList[i] !=-1 && scoreList[i] == -1) {
                        Text(
                            text = scorePreviewList[i].toString(), // Mostra il punteggio se non è -1
                            color = if (clickedButtonIndex == i) Color.White else Color.Black // Bianco se selezionato, nero altrimenti
                        )
                    }
                    if (scoreList[i] != -1){
                        //la categoria è già stata giocata
                        Text(
                            text = scoreList[i].toString(),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameFinish(score: Int){

    var gameFinished by rememberSaveable {mutableStateOf(true)}


    AlertDialog(
        onDismissRequest = { gameFinished = false },
        title = { Text(
            text = "Final Score:",
            fontSize = 35.sp, // Big
        ) },
        text = {
            Column {
                Text("Nice game, see you next time! :)")
                Text(text = score.toString(), fontSize = 35.sp)
            }
        },
        confirmButton = {
            Button(onClick = { gameFinished = false }) {
                Text("OK")
            }
        },
    )
}

@Composable
fun Score(score: Int){

    Box(
        modifier= Modifier
    ){
        Text (
            text = score.toString(),
            fontSize = 35.sp, // Big
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(start = 356.dp, top = 28.dp, end = 10.dp)
        )
    }
}

@Composable
fun RoundsLeft(rounds: Int){

    Box(
        modifier= Modifier
    ){
        Text (
            text = "Rounds left: ${13-rounds}",
            fontSize = 25.sp, // Big
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 15.dp, top = 15.dp)
        )
    }
}

@Composable
fun BackgroundSingleplayer(){
    Box(
        modifier=Modifier.fillMaxSize()
    ){
        Image(
            painter=painterResource(id= R.drawable.singleplayer),
            contentDescription="Single player background",
            contentScale= ContentScale.Crop,
            modifier=Modifier.matchParentSize()
        )
    }

}

/*@Preview(showBackground = true)
@Composable
fun SinglePreview() {
    YahtzeeTheme {
        BackgroundSingleplayer()
        ScoreTable()
        SinglePlayer()
    }
}*/
