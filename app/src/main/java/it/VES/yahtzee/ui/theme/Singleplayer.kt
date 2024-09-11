package it.VES.yahtzee.ui.theme

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.VES.yahtzee.R
import androidx.constraintlayout.compose.ConstraintLayout

class Singleplayer : ComponentActivity() {
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

    Box(
        modifier=Modifier
            .fillMaxSize()//Riempie tutta la schermata
    ){

        //Uso row per affiancare i due bottoni in basso
        Row(
            modifier=Modifier
                .align(Alignment.BottomCenter)//Allinea i bottoni al centro in basso
                .padding(16.dp)//distanzio i bottoni
        ) {
            Button(
                onClick = {/*azione per il bottone roll*/ },
                        modifier=Modifier
                            .padding(end=8.dp)
                            .width(200.dp)
                            .height(45.dp),
            ) {
                Text(text = "Roll")
            }
            Button(
                onClick = {/*azione per il bottone play*/ },
                        modifier=Modifier
                            .padding(end=8.dp)
                            .width(100.dp)
                            .height(45.dp),
            ) {
                Text(text = "Play")
            }
        }
    }
}

@Composable

fun ScoreTable() {
    Box(modifier = Modifier.fillMaxSize()) {

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Creazione riferimenti per i bottoni
            val (button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14) = createRefs()

            // Array di riferimenti per facilitare l'accesso
            val buttonRefs = listOf(button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14)

            // Crea 14 bottoni uno sotto l'altro
            buttonRefs.forEachIndexed { index, ref ->
                Button(
                    onClick = { /* Azione per il bottone */ },
                    modifier = Modifier
                        .constrainAs(ref) {
                            if (index == 0) {
                                top.linkTo(parent.top, margin = 35.dp) // Il primo bottone è ancorato al top
                            } else {
                                top.linkTo(buttonRefs[index - 1].bottom, margin = 16.dp) // I successivi bottoni sono ancorati sotto il precedente
                            }
                            end.linkTo(parent.end) // Allineati a sinistra
                        }
                        .width(90.dp)
                        .height(30.dp)
                ) {
                    Text(text = "")
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
