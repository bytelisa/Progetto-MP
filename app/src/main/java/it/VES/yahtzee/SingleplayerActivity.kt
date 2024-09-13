package it.VES.yahtzee

import android.content.Context
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview


import androidx.compose.ui.unit.dp


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
    var rolledDice by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    val context = LocalContext.current

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
                    rolledDice = DiceRollActivity().rollDice() //genera soltanto numeri casuali

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

        if (rolledDice.isNotEmpty()) {
            // Puoi fornire valori di rotazione predefiniti
            val rotationValues = listOf(0f, 15f, -10f, 20f, -5f)

            ImageSequence(
                imageIds = getImageResourceIds(rolledDice, context), //qua dovrei mettere gli id delle immagini
                rotationValues = rotationValues
            )
        }

    }
}


@Composable
fun ImageSequence(
    imageIds: List<Int>,  // Lista di ID delle immagini da visualizzare
    rotationValues: List<Float>  // Valori di rotazione da applicare
) {
    Column(
        modifier=Modifier
            .fillMaxSize()
    ){
        Spacer(modifier = Modifier.height(700.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Itera su ciascun ID immagine
            for (i in imageIds.indices) {

                Image(
                    painter = painterResource(id = imageIds[i]),  // Carica l'immagine con il suo ID
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .scale(0.5f)
                        .rotate(rotationValues[i])
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

    }

}



@Composable
fun PreviewImageSequence() {
    // Lista di ID delle immagini da visualizzare
    val imageIds = listOf(
        R.drawable.dice1,
        R.drawable.dice2,
        R.drawable.dice3,
        R.drawable.dice4,
        R.drawable.dice5,
        R.drawable.dice6
    )

    val rotationValues = listOf(0f, 15f, -10f, 20f, -5f)

    // Chiamata alla funzione con i valori definiti
    ImageSequence(
        imageIds = imageIds,
        rotationValues = rotationValues
    )
}

// Funzione che ottiene gli ID delle immagini dato un elenco di numeri
fun getImageResourceIds(diceValues: List<Int>, context: Context): List<Int> {
    return diceValues.map { diceValue ->
        val resourceName = "dice$diceValue" // Costruisce il nome dell'immagine (es. dice1, dice2...)
        val resourceId = getDrawableResourceByName(resourceName, context)
        resourceId
    }
}

// Funzione che ottiene l'ID di una risorsa drawable dato il nome
fun getDrawableResourceByName(name: String, context: Context): Int {
    val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
    if (resourceId == 0) {
        throw IllegalArgumentException("Risorsa drawable non trovata per nome: $name")
    }
    return resourceId
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