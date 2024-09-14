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

    var rolls: Int = 0

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
                            SinglePlayer()
                            //posiziono la tabella dei punteggi a destra
                            ScoreTable()
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun SinglePlayer() {

    //queste mi servono per mostrare i dadi quando viene premuto roll
    var rolledDice by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val context = LocalContext.current
    var showDialog  by remember {mutableStateOf(false)}
    var rolls  by rememberSaveable { mutableIntStateOf(0) }
    val scorePreviewByCategory = remember { mutableStateListOf(*List(5) { 0 }.toTypedArray()) }


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
                    if (rolls < 3){
                        rolledDice = DiceRollActivity().rollDice() //genera numeri casuali
                        rolls+=1
                        Log.d("SinglePlayerActivity", "#rolls: $rolls")

                    } else {
                        //finisce il turno di gioco, l'utente deve scegliere un punteggio
                        showDialog=true
                        //scorePreview!
                        getScorePreview(rolledDice)
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
                onClick = {/*azione per il bottone play*/ },
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

    }
}

@Composable
fun ScoreTable() {
    var clickedButtonIndex by remember { mutableStateOf(-1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 70.dp, top = 1.dp, bottom = 90.dp) // necessario a spostare i bottoni
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd) // Allinea la colonna a destra
        ) {
            for (i in 1..14) {
                Button(
                    onClick = {
                        clickedButtonIndex = i
                        // seleziona preview del punteggio
                        // quando poi viene premuto play il punteggio del bottone selezionato viene salvato nell'array dello score finale nella posizione i-esima
                        // TODO gestione del click su bottoni punteggio
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (clickedButtonIndex == i) Color(0xB5FF0000) else Color(0x5E969696),
                    ),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .width(80.dp)
                        .height(30.dp)
                        .offset(x = 0.dp, y = (i * 2.5).dp) // Offset per distanziare i bottoni
                ) {
                    // TODO: il testo va inserito solo dopo che è stato aggiornato il set di dadi
                    Text(text = "Button $i")
                    Text(text = "ciao")
                }
            }
        }
    }
}



fun getScorePreview(rolledDice: List<Int>): List<Int> {
    //questa funzione sfrutta la classe ScoreCalculator per calcolare la preview di tutti i punteggi che poi verrà usata da ScoreTable
    return List(rolledDice.size) {
        index -> ScoreCalculator().point(index, ArrayList(rolledDice))
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
