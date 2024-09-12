package it.VES.yahtzee

import it.VES.yahtzee.ui.theme.YahtzeeTheme



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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview


import androidx.compose.ui.unit.dp

import it.VES.yahtzee.R




class SingleplayerActivity : ComponentActivity() {

    //var rolledDice = listOf(0,0,0,0,0,0)


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
    var showDice1: Int by rememberSaveable { mutableIntStateOf(0) }
    var showDice2 by rememberSaveable { mutableIntStateOf(0) }
    var showDice3 by rememberSaveable { mutableIntStateOf(0) }
    var showDice4 by rememberSaveable { mutableIntStateOf(0) }
    var showDice5 by rememberSaveable { mutableIntStateOf(0) }
    var rolledDice: List<Int>

    Box(
        modifier=Modifier
            .fillMaxSize()//Riempie tutta la schermata
    ){
        Row(
            modifier= Modifier
                .align(Alignment.BottomCenter)//Allinea i bottoni al centro in basso
                .padding(16.dp)//distanzio i bottoni
        ) {
            Button(
                onClick = {
                    //bottone che lancia i dadi
                    rolledDice = DiceRollActivity().rollDice()
                    DiceRoll(rolledDice)
                },
                modifier= Modifier
                    .padding(end = 8.dp)
                    .width(200.dp)
                    .height(45.dp),
            ) {
                Text(text = "Roll")
            }
            Button(
                onClick = {/*azione per il bottone play*/ },
                modifier= Modifier
                    .padding(end = 8.dp)
                    .width(100.dp)
                    .height(45.dp),
            ) {
                Text(text = "Play")
            }

        }

        for (i in 0..5){
            Row(){
                Image(
                    //first dice
                    //var diceNumber: String = qui costruisco il nome del dado da chiamare in base all'elemento i-esimo della lista ottenuta
                    painter = painterResource(id = R.drawable.dice1),
                    contentDescription = "Immagine di esempio",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )
            }

        }
    }
}



fun DiceRoll(rolledDice: List<Int>){
    //funzione che carica le immagini dei dadi secondo i numeri prodotti dalla funzione DiceRoll
}


@Composable
fun ScoreTable() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(start = 16.dp, end = 70.dp, top = 1.dp, bottom = 100.dp) // necessario a spostare i bottoni
    ) {
        // Usa una Column per allineare i bottoni verticalmente
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd) // Allinea la colonna a destra
        ) {
            for (i in 1..14) {
                Button(
                    onClick = {/*azione per il bottone*/},
                    modifier = Modifier
                        .padding(bottom = 8.dp) // Margine tra i bottoni
                        .width(85.dp) // Larghezza dei bottoni
                        .height(37.dp) // Altezza dei bottoni
                ) {
                    Text(text = "Button $i")
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
fun Preview() {
    YahtzeeTheme {
        BackgroundSingleplayer()
        ScoreTable()
        SinglePlayer()
    }
}*/