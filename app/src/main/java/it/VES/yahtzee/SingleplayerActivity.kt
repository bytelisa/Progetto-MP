package it.VES.yahtzee

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp


class SingleplayerActivity : ComponentActivity() {

    var scorePlaceholder = List(14){-1}
    private var _categoryToPlay by mutableStateOf(-1)
    var categoryToPlay: Int
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
                            )                            //posiziono la tabella dei punteggi a destra
                            ScoreTable(
                                scorePreviewList = scorePlaceholder,
                                selectedCategory = categoryToPlay,
                                onCategorySelect = { newCategory ->
                                    categoryToPlay = newCategory
                                }
                            )                        }
                    }
                )
            }
        }
    }
}


@Composable
fun SinglePlayer(categoryToPlay: Int, onCategoryToPlayChange: (Int) -> Unit) {

    //queste mi servono per mostrare i dadi quando viene premuto roll
    var rolledDice by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val context = LocalContext.current
    var showDialog  by remember {mutableStateOf(false)}
    var showPlayDialog  by remember {mutableStateOf(false)}
    var rolls  by rememberSaveable { mutableIntStateOf(0) }
    val scorePreviewList = remember { mutableStateListOf(*List(14) { -1 }.toTypedArray()) }
    val scoreList = remember { mutableStateListOf(*List(14) { -1 }.toTypedArray()) }

    Box(
        modifier=Modifier
            .fillMaxSize()//Riempie tutta la schermata
    ){

        //Uso row per affiancare i due bottoni in basso
        Row(
            modifier= Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (rolls < 2){
                        rolledDice = DiceRollActivity().rollDice() //genera numeri casuali
                        rolls+=1

                    } else if (rolls == 2) { //ultimo lancio
                        rolledDice = DiceRollActivity().rollDice() //genera numeri casuali
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
                    containerColor = Color(0xB5FF0000)
                ),
                modifier= Modifier
                    .padding(end = 8.dp)
                    .width(200.dp)
                    .height(45.dp),
            ) {
                Text(text = "Roll")
            }
            Button(
                onClick = {
                    // quando viene premuto play il punteggio del bottone selezionato viene salvato nell'array dello score finale nella posizione i-esima
                    val i = categoryToPlay

                    if (i != -1){
                        scoreList[i] = scorePreviewList[i]
                        Log.d("SinglePlayerActivity", "#selected score: ${scoreList[i]}")
                    } else {
                        showPlayDialog = true
                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xB5FF0000)
                ),
                modifier= Modifier
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

        if(showDialog){
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Fine Turno :(") },
                text = {
                    Column {
                        Text("Hai terminato il numero di tiri a disposizione per questo turno! Scegli un punteggio da giocare.")

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

    ScoreTable(scorePreviewList, selectedCategory = categoryToPlay, onCategorySelect = { index ->
        onCategoryToPlayChange(index)
    })}

@Composable
fun ScoreTable(scorePreviewList: List<Int>, selectedCategory: Int, onCategorySelect: (Int) -> Unit) {

    var clickedButtonIndex by remember { mutableStateOf(-1) }

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
                        clickedButtonIndex = i
                        onCategorySelect(clickedButtonIndex) // Aggiorna la variabile globale
                        Log.d("SinglePlayerActivity", "#selected category: ${selectedCategory}")

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (clickedButtonIndex == i) Color(0xB5FF0000) else Color(0x5E969696),
                    ),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .width(80.dp)
                        .height(30.dp)
                        .offset(x = 0.dp, y = (i * 2.5).dp)
                ) {
                    if (scorePreviewList[i] != -1) {
                        Text(
                            text = scorePreviewList[i].toString(), // Mostra il punteggio se non è -1
                            color = if (clickedButtonIndex == i) Color.White else Color.Black // Bianco se selezionato, nero altrimenti
                        )
                    }
                }
            }
        }
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
